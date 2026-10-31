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
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Màn hình BÁN HÀNG & THANH TOÁN (gộp Đặt hàng + Hóa đơn thành một màn hình) gồm 4 tab:
 *  1. Bán hàng & thanh toán : giỏ hàng, khách hàng, khuyến mãi, phương thức thanh toán -> lập hóa đơn.
 *  2. Lập phiếu đặt hàng    : khách đặt trước, chờ xác nhận.
 *  3. Phiếu đặt hàng        : xác nhận / hủy phiếu, hoặc chuyển thẳng sang thanh toán.
 *  4. Hóa đơn               : tra cứu, xem chi tiết, in và hủy hóa đơn.
 */
public class BanHangPanel extends JPanel implements Refreshable {
    // ---- danh sách hóa đơn ----
    private final DefaultTableModel hdModel = Theme.model("Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên", "Khuyến mãi",
            "Phương thức", "Tổng tiền", "Giảm giá", "Thành tiền", "Trạng thái", "Lý do hủy", "Phiếu đặt hàng");
    private final DefaultTableModel hdDetail = Theme.model("Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền");
    private final JTable tbHd = Theme.table(hdModel);
    private final JTextField search = Theme.field(20);
    private final JComboBox<Object> cbStatus = new JComboBox<>();
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(hdModel);
    private final JLabel lbHdSum = new JLabel();
    // ---- danh sách phiếu đặt hàng ----
    private final DefaultTableModel pdhModel = Theme.model("Mã phiếu", "Ngày đặt", "Khách hàng", "Nhân viên", "Tổng tiền", "Trạng thái", "Ghi chú");
    private final DefaultTableModel pdhDetail = Theme.model("Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền");
    private final JTable tbPdh = Theme.table(pdhModel);

    private final GioHangPanel banHang, datHang;
    private final JTabbedPane tabs = new JTabbedPane();
    private final HoaDonDAO hdDao = new HoaDonDAO();
    private final PhieuDatHangDAO pdhDao = new PhieuDatHangDAO();
    private final SanPhamDAO spDao = new SanPhamDAO();
    private final KhachHangDAO khDao = new KhachHangDAO();
    private List<HoaDon> dsHd = new ArrayList<>();
    private List<PhieuDatHang> dsPdh = new ArrayList<>();

    public BanHangPanel() {
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        add(Theme.title("Bán hàng & thanh toán"), BorderLayout.NORTH);
        banHang = new GioHangPanel(GioHangPanel.Mode.THANH_TOAN, this::reload);
        datHang = new GioHangPanel(GioHangPanel.Mode.DAT_HANG, this::reload);
        tabs.setFont(Theme.BOLD);
        tabs.addTab("Bán hàng & thanh toán", banHang);
        tabs.addTab("Lập phiếu đặt hàng", datHang);
        tabs.addTab("Phiếu đặt hàng", buildPhieuList());
        tabs.addTab("Hóa đơn", buildHoaDonList());
        add(tabs, BorderLayout.CENTER);
        reload();
    }

    // ===================== PHIẾU ĐẶT HÀNG =====================
    private JPanel buildPhieuList() {
        JPanel list = Theme.transparent(new BorderLayout(0, 10));
        list.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton ok = Theme.success("Xác nhận đặt hàng"), cancel = Theme.danger("Hủy phiếu"), pay = Theme.primary("Thanh toán phiếu này");
        ok.addActionListener(e -> confirmPhieu());
        cancel.addActionListener(e -> cancelPhieu());
        pay.addActionListener(e -> payPhieu());
        JPanel bt = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0)); bt.add(cancel); bt.add(ok); bt.add(pay);
        list.add(bt, BorderLayout.NORTH);
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Theme.scroll(tbPdh), Theme.scroll(Theme.table(pdhDetail)));
        sp.setResizeWeight(0.6); sp.setBorder(null); sp.setOpaque(false);
        list.add(sp, BorderLayout.CENTER);
        tbPdh.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            pdhDetail.setRowCount(0);
            PhieuDatHang p = selectedPhieu(false);
            if (p != null) for (ChiTietPhieuDatHang c : p.getChiTietList())
                pdhDetail.addRow(new Object[]{c.getMaSanPham().getMaSanPham(), c.getMaSanPham().getTenSanPham(), c.getSoLuong(), c.getDonGia(), c.thanhTien()});
        });
        return list;
    }

    private PhieuDatHang selectedPhieu(boolean warn) {
        int r = tbPdh.getSelectedRow();
        if (r < 0) { if (warn) info("Vui lòng chọn một phiếu đặt hàng"); return null; }
        return dsPdh.get(tbPdh.convertRowIndexToModel(r));
    }

    private void confirmPhieu() {
        PhieuDatHang p = selectedPhieu(true); if (p == null) return;
        if (p.isTrangThai()) { warn("Phiếu này đã được xác nhận"); return; }
        p.xacNhan(); pdhDao.capNhat(p); reload();
    }

    private void cancelPhieu() {
        PhieuDatHang p = selectedPhieu(true); if (p == null) return;
        if (hdDao.timTheoPhieuDat(p) != null) { warn("Phiếu đã lập hóa đơn, không thể hủy"); return; }
        if (JOptionPane.showConfirmDialog(this, "Hủy phiếu " + p.getMaPhieuDatHang() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        p.huyDat();
        pdhDao.xoa(p);       // entity chỉ có cờ boolean nên phiếu hủy được loại khỏi danh sách
        reload();
    }

    /** Nghiệp vụ: phiếu đã xác nhận, chưa có hóa đơn -> chuyển sang tab bán hàng với giỏ hàng lấy từ phiếu. */
    private void payPhieu() {
        PhieuDatHang p = selectedPhieu(true); if (p == null) return;
        if (!p.isTrangThai()) { warn("Phiếu chưa được xác nhận, hãy xác nhận trước khi thanh toán"); return; }
        if (hdDao.timTheoPhieuDat(p) != null) { warn("Phiếu này đã được thanh toán"); return; }
        banHang.chonPhieu(p);
        tabs.setSelectedIndex(0);
    }

    // ===================== HÓA ĐƠN =====================
    private JPanel buildHoaDonList() {
        JPanel p = Theme.transparent(new BorderLayout(0, 10)); p.setBorder(new EmptyBorder(12, 0, 0, 0));
        tbHd.setRowSorter(sorter);
        tbHd.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);       // 12 cột: cho phép cuộn ngang thay vì nén chữ
        int[] w = {80, 145, 200, 190, 160, 120, 120, 110, 120, 130, 160, 130};
        for (int i = 0; i < w.length; i++) tbHd.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        JPanel bar = Theme.transparent(new BorderLayout());
        JPanel left = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cbStatus.setFont(Theme.BASE);
        cbStatus.addItem("Tất cả"); for (TrangThaiHoaDon t : TrangThaiHoaDon.values()) cbStatus.addItem(t);
        left.add(Theme.label("Tìm kiếm:")); left.add(search); left.add(Theme.label("Trạng thái:")); left.add(cbStatus);
        JPanel right = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton view = Theme.ghost("Xem / In hóa đơn"), cancel = Theme.danger("Hủy hóa đơn");
        view.addActionListener(e -> { HoaDon h = selectedHd(true); if (h != null) showReceipt(this, h); });
        cancel.addActionListener(e -> cancelInvoice());
        right.add(view); right.add(cancel);
        bar.add(left, BorderLayout.WEST); bar.add(right, BorderLayout.EAST);
        lbHdSum.setFont(Theme.BOLD); lbHdSum.setForeground(Theme.BLUE);
        JPanel north = Theme.transparent(new BorderLayout(0, 6)); north.add(bar, BorderLayout.NORTH); north.add(lbHdSum, BorderLayout.SOUTH);
        p.add(north, BorderLayout.NORTH);

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Theme.scroll(tbHd), Theme.scroll(Theme.table(hdDetail)));
        sp.setResizeWeight(0.65); sp.setBorder(null); sp.setOpaque(false);
        p.add(sp, BorderLayout.CENTER);

        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
        cbStatus.addActionListener(e -> filter());
        tbHd.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            hdDetail.setRowCount(0);
            HoaDon h = selectedHd(false);
            if (h != null) for (ChiTietHoaDon c : h.getChiTietList())
                hdDetail.addRow(new Object[]{c.getMaSanPham().getMaSanPham(), c.getMaSanPham().getTenSanPham(), c.getSoLuong(), c.getDonGia(), c.thanhTien()});
        });
        return p;
    }

    private void filter() {
        List<RowFilter<DefaultTableModel, Integer>> fs = new ArrayList<>();
        String t = search.getText().trim();
        if (!t.isEmpty()) fs.add(RowFilter.regexFilter("(?i)" + Pattern.quote(t)));
        if (cbStatus.getSelectedIndex() > 0) fs.add(RowFilter.regexFilter("^" + Pattern.quote(cbStatus.getSelectedItem().toString()) + "$", 9));
        sorter.setRowFilter(fs.isEmpty() ? null : RowFilter.andFilter(fs));
    }

    private HoaDon selectedHd(boolean warn) {
        int r = tbHd.getSelectedRow();
        if (r < 0) { if (warn) info("Vui lòng chọn một hóa đơn"); return null; }
        return dsHd.get(tbHd.convertRowIndexToModel(r));
    }

    private void cancelInvoice() {
        HoaDon h = selectedHd(true); if (h == null) return;
        if (h.getTrangThai() == TrangThaiHoaDon.DA_HUY) { warn("Hóa đơn này đã được hủy"); return; }
        // Nghiệp vụ: nhân viên bán hàng chỉ được hủy hóa đơn lập trong ngày; quản lý hủy được mọi hóa đơn
        if (!"QUAN_LY".equals(PhienLamViec.quyen()) && !Fmt.isToday(h.getNgayLap())) {
            warn("Chỉ quản lý mới được hủy hóa đơn của ngày trước.\nHóa đơn này lập ngày " + Fmt.date(h.getNgayLap()) + ".");
            return;
        }
        String reason = JOptionPane.showInputDialog(this, "Nhập lý do hủy hóa đơn " + h.getMaHoaDon() + ":", "Hủy hóa đơn", JOptionPane.QUESTION_MESSAGE);
        if (reason == null) return;
        if (reason.trim().isEmpty()) { warn("Lý do hủy không được để trống"); return; }
        try { h.huyVaHoanKho(reason.trim()); }          // hủy + hoàn tồn kho + thu hồi điểm (logic nằm trong entity)
        catch (IllegalStateException | IllegalArgumentException ex) { warn(ex.getMessage()); return; }
        hdDao.capNhat(h);
        for (ChiTietHoaDon c : h.getChiTietList()) spDao.capNhat(c.getMaSanPham());
        khDao.capNhat(h.getMaKhachHang());
        reload();
    }

    // ===================== NẠP DỮ LIỆU =====================
    private void reload() {
        dsHd = hdDao.getAll();
        hdModel.setRowCount(0);
        double sumToday = 0; int cntToday = 0;
        for (HoaDon h : dsHd) {
            hdModel.addRow(new Object[]{h.getMaHoaDon(), Fmt.dateTime(h.getNgayLap()), h.getMaKhachHang(), h.getMaNhanVien(),
                    h.getMaKhuyenMai() == null ? "" : h.getMaKhuyenMai().getTenKhuyenMai(), h.getPhuongThucThanhToan(),
                    h.tinhTongTien(), h.tienGiam(), h.thanhTienDaLap(), h.getTrangThai(),
                    h.getLyDoHuyHoaDon() == null ? "" : h.getLyDoHuyHoaDon(),
                    h.getMaPhieuDatHang() == null ? "" : h.getMaPhieuDatHang().getMaPhieuDatHang()});
            if (h.daThanhToan() && Fmt.isToday(h.getNgayLap())) { sumToday += h.thanhTienDaLap(); cntToday++; }
        }
        lbHdSum.setText("Hôm nay: " + cntToday + " hóa đơn đã thanh toán, doanh thu " + Fmt.money(sumToday));

        dsPdh = pdhDao.getAll();
        pdhModel.setRowCount(0);
        for (PhieuDatHang p : dsPdh)
            pdhModel.addRow(new Object[]{p.getMaPhieuDatHang(), Fmt.dateTime(p.getNgayDatHang()), p.getMaKhachHang(), p.getMaNhanVien(),
                    p.tinhTongTien(), p.trangThaiText(hdDao.timTheoPhieuDat(p) != null), p.getGhiChu() == null ? "" : p.getGhiChu()});
    }

    @Override public void refresh() { banHang.refresh(); datHang.refresh(); reload(); }

    private void info(String m) { JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Thông báo", JOptionPane.WARNING_MESSAGE); }

    /** Hộp thoại xem/in hóa đơn dạng văn bản. */
    public static void showReceipt(Component parent, HoaDon h) {
        StringBuilder sb = new StringBuilder();
        sb.append("        SIÊU THỊ ĐIỆN MÁY XIU\n        HÓA ĐƠN BÁN HÀNG\n");
        sb.append("--------------------------------------------\n");
        sb.append("Số HĐ      : ").append(h.getMaHoaDon()).append('\n');
        sb.append("Ngày lập   : ").append(Fmt.dateTime(h.getNgayLap())).append('\n');
        sb.append("Khách hàng : ").append(h.getMaKhachHang()).append('\n');
        sb.append("Nhân viên  : ").append(h.getMaNhanVien().getHoTenNhanVien()).append('\n');
        sb.append("Thanh toán : ").append(h.getPhuongThucThanhToan()).append('\n');
        sb.append("--------------------------------------------\n");
        for (ChiTietHoaDon c : h.getChiTietList())
            sb.append(String.format("%s%n   %d x %s = %s%n", c.getMaSanPham().getTenSanPham(), c.getSoLuong(), Fmt.money(c.getDonGia()), Fmt.money(c.thanhTien())));
        sb.append("--------------------------------------------\n");
        sb.append(String.format("Tổng tiền  : %s%n", Fmt.money(h.tinhTongTien())));
        sb.append(String.format("Giảm giá   : %s%n", Fmt.money(h.tienGiam())));
        sb.append(String.format("THÀNH TIỀN : %s%n", Fmt.money(h.thanhTienDaLap())));
        sb.append("Trạng thái : ").append(h.getTrangThai()).append('\n');
        if (h.getGhiChu() != null && !h.getGhiChu().isEmpty()) sb.append("Ghi chú    : ").append(h.getGhiChu()).append('\n');
        if (h.getLyDoHuyHoaDon() != null && !h.getLyDoHuyHoaDon().isEmpty()) sb.append("Lý do hủy  : ").append(h.getLyDoHuyHoaDon()).append('\n');
        sb.append("\n        Cảm ơn quý khách!");

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13)); ta.setEditable(false); ta.setBorder(new EmptyBorder(12, 14, 12, 14));
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(parent), "Hóa đơn " + h.getMaHoaDon(), Dialog.ModalityType.APPLICATION_MODAL);
        JButton print = Theme.primary("In"), close = Theme.ghost("Đóng");
        print.addActionListener(e -> { try { ta.print(); } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Không thể in: " + ex.getMessage()); } });
        close.addActionListener(e -> d.dispose());
        JPanel bt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8)); bt.add(close); bt.add(print);
        d.add(new JScrollPane(ta)); d.add(bt, BorderLayout.SOUTH);
        d.setSize(430, 540); d.setLocationRelativeTo(parent); d.setVisible(true);
    }
}
