package xiu.ui;

import DAO.*;
import ENTITY.*;
import ENUM.*;
import xiu.common.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Giỏ hàng dùng cho 2 nghiệp vụ: lập hóa đơn & thanh toán (THANH_TOAN) và lập phiếu đặt hàng (DAT_HANG).
 * Giỏ hàng gồm các dòng tạm; khi xác nhận mới tạo HoaDon/PhieuDatHang cùng các ChiTiet tương ứng.
 */
public class GioHangPanel extends JPanel implements Refreshable {
    public enum Mode { DAT_HANG, THANH_TOAN }

    /** Một dòng trong giỏ hàng. */
    private static class Dong {
        final SanPham sp; int sl; final double gia;
        Dong(SanPham sp, int sl, double gia) { this.sp = sp; this.sl = sl; this.gia = gia; }
    }

    private static final String KHONG_KM = "Không áp dụng", BAN_TRUC_TIEP = "(Bán trực tiếp)";

    private final Mode mode;
    private final Runnable onDone;
    private final List<Dong> cartLines = new ArrayList<>();
    private final List<SanPham> spRows = new ArrayList<>();
    private final JTextField tfSearch = Theme.field(18), tfNote = Theme.field(18);
    private final DefaultTableModel spView = Theme.model("Mã SP", "Tên sản phẩm", "Giá bán", "Tồn");
    private final DefaultTableModel cart = Theme.model("Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền");
    private final JTable tbSp = Theme.table(spView), tbCart = Theme.table(cart);
    private final JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JComboBox<Object> cbKh = new JComboBox<>(), cbKm = new JComboBox<>(), cbPdh = new JComboBox<>();
    private final JComboBox<PhuongThucThanhToan> cbPt = new JComboBox<>(PhuongThucThanhToan.values());
    private final JLabel lbTong = new JLabel("0đ"), lbGiam = new JLabel("0đ"), lbThanh = new JLabel("0đ");
    private boolean loading;
    private final KhachHangDAO khDao = new KhachHangDAO();
    private final SanPhamDAO spDao = new SanPhamDAO();
    private final KhuyenMaiDAO kmDao = new KhuyenMaiDAO();
    private final PhieuDatHangDAO pdhDao = new PhieuDatHangDAO();
    private final HoaDonDAO hdDao = new HoaDonDAO();

    public GioHangPanel(Mode mode, Runnable onDone) {
        this.mode = mode; this.onDone = onDone;
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(12, 0, 0, 0));

        // --- Trái: danh sách sản phẩm ---
        JPanel left = Theme.card();
        JPanel sr = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        sr.add(Theme.label("Tìm sản phẩm:")); sr.add(tfSearch);
        sr.setBorder(new EmptyBorder(0, 0, 8, 0));
        left.add(sr, BorderLayout.NORTH);
        left.add(Theme.scroll(tbSp), BorderLayout.CENTER);
        JPanel addRow = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        spQty.setFont(Theme.BASE); spQty.setPreferredSize(new Dimension(70, 34));
        JButton btnAdd = Theme.primary("Thêm vào giỏ"); btnAdd.addActionListener(e -> addToCart());
        addRow.add(Theme.label("Số lượng:")); addRow.add(spQty); addRow.add(btnAdd);
        addRow.setBorder(new EmptyBorder(8, 0, 0, 0));
        left.add(addRow, BorderLayout.SOUTH);
        tbSp.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { if (e.getClickCount() == 2) addToCart(); }
        });
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadProducts(); }
            public void removeUpdate(DocumentEvent e) { loadProducts(); }
            public void changedUpdate(DocumentEvent e) { loadProducts(); }
        });

        // --- Phải: giỏ hàng + thông tin ---
        JPanel right = Theme.card();
        right.add(Theme.scroll(tbCart), BorderLayout.CENTER);
        JPanel cartBtns = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton rm = Theme.ghost("Xóa dòng"), clr = Theme.ghost("Xóa hết");
        rm.addActionListener(e -> { int r = tbCart.getSelectedRow(); if (r >= 0) { cartLines.remove(tbCart.convertRowIndexToModel(r)); reloadCart(); } });
        clr.addActionListener(e -> { cartLines.clear(); reloadCart(); });
        cartBtns.add(rm); cartBtns.add(clr);

        JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints(); g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;
        y = row(form, g, y, "Khách hàng *", cbKh);
        y = row(form, g, y, "Khuyến mãi", cbKm);
        if (mode == Mode.THANH_TOAN) { y = row(form, g, y, "Phiếu đặt hàng", cbPdh); row(form, g, y, "Thanh toán bằng", cbPt); }
        else row(form, g, y, "Ghi chú", tfNote);
        for (JComboBox<?> c : List.of(cbKh, cbKm, cbPt, cbPdh)) c.setFont(Theme.BASE);
        cbKm.addActionListener(e -> recalc());
        cbPdh.addActionListener(e -> loadFromPhieu());

        JPanel tot = new JPanel(new GridLayout(3, 2, 4, 2)); tot.setOpaque(false);
        for (JLabel l : List.of(lbTong, lbGiam, lbThanh)) { l.setFont(Theme.BOLD); l.setHorizontalAlignment(SwingConstants.RIGHT); }
        lbThanh.setFont(Theme.BASE.deriveFont(Font.BOLD, 20f)); lbThanh.setForeground(Theme.BLUE);
        tot.add(Theme.label("Tổng tiền:")); tot.add(lbTong);
        tot.add(Theme.label("Giảm giá:")); tot.add(lbGiam);
        tot.add(Theme.label("Thành tiền:")); tot.add(lbThanh);

        JButton submit = mode == Mode.THANH_TOAN ? Theme.success("Thanh toán & lập hóa đơn") : Theme.primary("Lập phiếu đặt hàng");
        submit.addActionListener(e -> submit());

        JPanel bottom = Theme.transparent(new BorderLayout(0, 8));
        bottom.add(cartBtns, BorderLayout.NORTH);
        JPanel mid = Theme.transparent(new BorderLayout(16, 0)); mid.add(form, BorderLayout.CENTER); mid.add(tot, BorderLayout.EAST);
        bottom.add(mid, BorderLayout.CENTER);
        JPanel sub = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 0, 0)); sub.add(submit);
        bottom.add(sub, BorderLayout.SOUTH);
        bottom.setBorder(new EmptyBorder(8, 0, 0, 0));
        right.add(bottom, BorderLayout.SOUTH);

        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        sp.setResizeWeight(0.42); sp.setBorder(null); sp.setOpaque(false); sp.setDividerSize(10);
        add(sp, BorderLayout.CENTER);
        refresh();
    }

    /** Chọn sẵn một phiếu đặt hàng đã xác nhận để thanh toán (dùng khi bấm "Thanh toán phiếu này"). */
    public void chonPhieu(PhieuDatHang p) {
        refresh();
        for (int i = 0; i < cbPdh.getItemCount(); i++)
            if (cbPdh.getItemAt(i) instanceof PhieuDatHang x && x.getMaPhieuDatHang().equals(p.getMaPhieuDatHang())) { cbPdh.setSelectedIndex(i); return; }
    }

    private int row(JPanel p, GridBagConstraints g, int y, String label, JComponent c) {
        g.gridy = y; g.gridx = 0; g.weightx = 0; p.add(Theme.label(label), g);
        g.gridx = 1; g.weightx = 1; p.add(c, g); return y + 1;
    }

    @Override public void refresh() {
        loading = true;
        cbKh.removeAllItems();
        khDao.getAll().forEach(cbKh::addItem);
        cbKm.removeAllItems(); cbKm.addItem(KHONG_KM);
        for (KhuyenMai k : kmDao.getAll()) if (k.conHieuLuc()) cbKm.addItem(k);
        cbPdh.removeAllItems(); cbPdh.addItem(BAN_TRUC_TIEP);
        for (PhieuDatHang p : pdhDao.getAll()) if (p.daXacNhan() && hdDao.timTheoPhieuDat(p) == null) cbPdh.addItem(p);
        loading = false;
        loadProducts(); reloadCart();
    }

    private void loadProducts() {
        spView.setRowCount(0); spRows.clear();
        String q = tfSearch.getText().trim().toLowerCase();
        for (SanPham s : spDao.getAll()) {
            if (!s.coTheBanSoLuong(1)) continue;
            String hay = (s.getMaSanPham() + " " + s.getTenSanPham()).toLowerCase();
            if (!q.isEmpty() && !hay.contains(q)) continue;
            spRows.add(s);
            spView.addRow(new Object[]{s.getMaSanPham(), s.getTenSanPham(), s.getGiaBan(), s.getSoLuongTon()});
        }
    }

    private Dong dongOf(SanPham s) {
        for (Dong d : cartLines) if (d.sp.equals(s)) return d;
        return null;
    }

    private void addToCart() {
        int r = tbSp.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần thêm", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        SanPham s = spRows.get(tbSp.convertRowIndexToModel(r));
        int qty = (Integer) spQty.getValue();
        Dong d = dongOf(s);
        int have = d == null ? 0 : d.sl;
        if (have + qty > s.getSoLuongTon()) {
            JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho (còn " + s.getSoLuongTon() + ")", "Không đủ hàng", JOptionPane.WARNING_MESSAGE); return;
        }
        if (d != null) d.sl += qty; else cartLines.add(new Dong(s, qty, s.getGiaBan()));
        spQty.setValue(1); reloadCart();
    }

    private void reloadCart() {
        cart.setRowCount(0);
        for (Dong d : cartLines) cart.addRow(new Object[]{d.sp.getMaSanPham(), d.sp.getTenSanPham(), d.sl, d.gia, d.gia * d.sl});
        recalc();
    }

    private double total() { double t = 0; for (Dong d : cartLines) t += d.sl * d.gia; return t; }

    private KhuyenMai selectedKm() { return cbKm.getSelectedItem() instanceof KhuyenMai k ? k : null; }

    private double discount(double tong) { KhuyenMai k = selectedKm(); return k == null ? 0 : k.tinhTienGiam(tong); }

    private void recalc() {
        double t = total(), g = discount(t);
        lbTong.setText(Fmt.money(t)); lbGiam.setText("-" + Fmt.money(g)); lbThanh.setText(Fmt.money(t - g));
    }

    private void loadFromPhieu() {
        if (loading) return;

        cartLines.clear();

        if (cbPdh.getSelectedItem() instanceof PhieuDatHang p) {

            // Nạp sản phẩm từ phiếu đặt hàng.
            for (ChiTietPhieuDatHang c : p.getChiTietList()) {
                cartLines.add(new Dong(
                        c.getMaSanPham(),
                        c.getSoLuong(),
                        c.getDonGia()
                ));
            }

            // Chọn khách hàng theo mã, không so sánh đối tượng.
            String maKH = p.getMaKhachHang().getMaKhachHang();

            for (int i = 0; i < cbKh.getItemCount(); i++) {
                KhachHang kh = (KhachHang) cbKh.getItemAt(i);

                if (kh.getMaKhachHang().equals(maKH)) {
                    cbKh.setSelectedIndex(i);
                    break;
                }
            }
        }

        reloadCart();
    }

    /** Hỏi số tiền khách đưa (tiền mặt). @return số tiền, hoặc -1 nếu hủy. */
    private double nhapTienKhachDua(double canThu) {
        while (true) {
            String in = JOptionPane.showInputDialog(this, "Cần thu: " + Fmt.money(canThu) + "\nTiền khách đưa (đồng):",
                    "Thu tiền mặt", JOptionPane.QUESTION_MESSAGE);
            if (in == null) return -1;
            try {
                double v = Double.parseDouble(in.trim().replace(".", "").replace(",", ""));
                if (v >= canThu) return v;
                warn("Số tiền khách đưa chưa đủ (còn thiếu " + Fmt.money(canThu - v) + ")");
            } catch (NumberFormatException ex) { warn("Vui lòng nhập số tiền hợp lệ"); }
        }
    }

    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.WARNING_MESSAGE); }

    private void submit() {
        if (cartLines.isEmpty()) { warn("Giỏ hàng đang trống"); return; }
        if (!(cbKh.getSelectedItem() instanceof KhachHang kh)) { warn("Vui lòng chọn khách hàng"); return; }
        // LoaiKhachHang chi phan biet KH_MOI va KH_VIP; khong dung trangThai cu nua.
        for (Dong d : cartLines)    // kiểm tra tồn kho lần cuối
            if (d.sl > d.sp.getSoLuongTon()) { warn("Sản phẩm " + d.sp.getTenSanPham() + " không đủ tồn kho"); return; }
        LocalDateTime now = LocalDateTime.now();

        if (mode == Mode.DAT_HANG) {
            PhieuDatHang p = new PhieuDatHang(pdhDao.taoMaMoi(), kh, PhienLamViec.nhanVien(), now, TrangThaiPhieuDatHang.CHO_XU_LY, tfNote.getText().trim());
            for (Dong d : cartLines) p.taoChiTiet(d.sp, d.sl, d.gia);
            if (!pdhDao.them(p)) { warn("Không lưu được phiếu đặt hàng"); return; }
            JOptionPane.showMessageDialog(this, "Đã lập phiếu đặt hàng " + p.getMaPhieuDatHang() + " (chờ xác nhận)", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            PhieuDatHang pdh = cbPdh.getSelectedItem() instanceof PhieuDatHang p ? p : null;
            PhuongThucThanhToan pt = (PhuongThucThanhToan) cbPt.getSelectedItem();
            String ghiChu = "";
            if (pt == PhuongThucThanhToan.TIEN_MAT) {     // Nghiệp vụ: thu tiền mặt, tính tiền thối
                double can = total() - discount(total());
                double nhan = nhapTienKhachDua(can);
                if (nhan < 0) return;                      // người dùng bấm hủy
                ghiChu = "Khách đưa " + Fmt.money(nhan) + ", tiền thối " + Fmt.money(nhan - can);
            }
            HoaDon hd = new HoaDon(hdDao.taoMaMoi(), pdh, kh, PhienLamViec.nhanVien(), selectedKm(),
                    pt, now, TrangThaiHoaDon.DA_THANH_TOAN, "", ghiChu);
            for (Dong d : cartLines) hd.taoChiTiet(d.sp, d.sl, d.gia);
            if (!hd.lapHoaDon()) { warn("Không thể lập hóa đơn: có sản phẩm không đủ tồn kho"); return; }   // xuất kho + cộng điểm nằm trong entity
            if (!hdDao.them(hd)) { warn("Không lưu được hóa đơn"); refresh(); return; }
            for (ChiTietHoaDon c : hd.getChiTietList()) spDao.capNhat(c.getMaSanPham());                   // lưu tồn kho mới
            khDao.capNhat(kh);                                                                              // lưu điểm tích lũy mới
            BanHangPanel.showReceipt(this, hd);
        }
        cartLines.clear(); tfNote.setText("");
        refresh();
        if (onDone != null) onDone.run();
    }
}
