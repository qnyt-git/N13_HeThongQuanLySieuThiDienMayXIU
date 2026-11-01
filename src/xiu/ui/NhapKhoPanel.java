package xiu.ui;

import DAO.*;
import ENTITY.*;
import ENUM.*;
import xiu.common.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Nhập kho (thuộc nhóm Danh mục): lập phiếu nhập (nhiều dòng), gợi ý SP sắp hết, quản lý xác nhận/hủy phiếu để cập nhật tồn kho. */
public class NhapKhoPanel extends JPanel implements Refreshable {
    private static class Dong {
        final SanPham sp; final int sl; final double gia;
        Dong(SanPham sp, int sl, double gia) { this.sp = sp; this.sl = sl; this.gia = gia; }
    }

    /** Nghiệp vụ: nhân viên kho chỉ lập phiếu (chờ duyệt); chỉ quản lý mới được xác nhận nhập kho / cập nhật tồn kho. */
    private final boolean quyenDuyet = "QUAN_LY".equals(PhienLamViec.quyen());
    private static final String CHI_QUAN_LY = "Chỉ quản lý mới được xác nhận nhập kho";

    private final List<Dong> dong = new ArrayList<>();
    private final JComboBox<NhaCungCap> cbNcc = new JComboBox<>();
    private final JComboBox<SanPham> cbSp = new JComboBox<>();
    private final JTextField tfLyDo = Theme.field(24), tfGia = Theme.field(10);
    private final JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final DefaultTableModel lines = Theme.model("Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền");
    private final DefaultTableModel model = Theme.model("Mã phiếu", "Ngày nhập", "Nhà cung cấp", "Nhân viên", "Lý do nhập", "Tổng tiền", "Trạng thái");
    private final DefaultTableModel detail = Theme.model("Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền");
    private final JTable tbLines = Theme.table(lines), tbList = Theme.table(model);
    private final JLabel lbTong = new JLabel("0đ");
    private final PhieuNhapKhoDAO pnkDao = new PhieuNhapKhoDAO();
    private final NhaCungCapDAO nccDao = new NhaCungCapDAO();
    private final SanPhamDAO spDao = new SanPhamDAO();
    private List<PhieuNhapKho> ds = new ArrayList<>();

    public NhapKhoPanel() {
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        add(Theme.title("Nhập kho"), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(Theme.BOLD);
        tabs.addTab("Lập phiếu nhập kho", buildCreate());
        tabs.addTab("Danh sách phiếu nhập", buildList());
        add(tabs, BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildCreate() {
        JPanel p = Theme.card();
        JPanel head = new JPanel(new GridBagLayout()); head.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints(); g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.HORIZONTAL;
        cbNcc.setFont(Theme.BASE); cbSp.setFont(Theme.BASE);
        g.gridy = 0; g.gridx = 0; head.add(Theme.label("Nhà cung cấp *"), g); g.gridx = 1; g.weightx = 1; head.add(cbNcc, g);
        g.gridx = 2; g.weightx = 0; head.add(Theme.label("Ngày nhập"), g); g.gridx = 3; head.add(Theme.muted(Fmt.date(LocalDate.now()) + " (tự động)"), g);
        g.gridy = 1; g.gridx = 0; head.add(Theme.label("Lý do nhập *"), g); g.gridx = 1; g.weightx = 1; head.add(tfLyDo, g);
        g.gridx = 2; g.weightx = 0; head.add(Theme.label("Nhân viên"), g); g.gridx = 3; head.add(Theme.muted(PhienLamViec.nhanVien().toString()), g);

        JPanel add = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        spQty.setFont(Theme.BASE); spQty.setPreferredSize(new Dimension(80, 34));
        JButton btn = Theme.primary("+ Thêm dòng");
        btn.addActionListener(e -> addLine());
        add.add(Theme.label("Sản phẩm:")); add.add(cbSp); add.add(Theme.label("SL:")); add.add(spQty);
        add.add(Theme.label("Đơn giá nhập:")); add.add(tfGia); add.add(btn);
        JButton low = Theme.ghost("Gợi ý: SP sắp hết hàng");
        low.addActionListener(e -> goiYSapHet());
        add.add(low);

        JPanel north = Theme.transparent(new BorderLayout(0, 8)); north.add(head, BorderLayout.NORTH); north.add(add, BorderLayout.CENTER);
        p.add(north, BorderLayout.NORTH);
        p.add(Theme.scroll(tbLines), BorderLayout.CENTER);

        JButton rm = Theme.ghost("Xóa dòng"), save = Theme.ghost("Lưu chờ duyệt"), ok = Theme.success("Lưu & xác nhận nhập kho");
        rm.addActionListener(e -> { int r = tbLines.getSelectedRow(); if (r >= 0) { dong.remove(tbLines.convertRowIndexToModel(r)); reloadLines(); } });
        save.addActionListener(e -> savePhieu(false));
        ok.addActionListener(e -> savePhieu(true));
        if (!quyenDuyet) { ok.setEnabled(false); ok.setToolTipText(CHI_QUAN_LY); }
        lbTong.setFont(Theme.BASE.deriveFont(Font.BOLD, 18f)); lbTong.setForeground(Theme.BLUE);
        JPanel south = Theme.transparent(new BorderLayout());
        JPanel l = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0)); l.add(rm);
        JPanel r = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 12, 0)); r.add(Theme.label("Tổng tiền:")); r.add(lbTong); r.add(save); r.add(ok);
        south.add(l, BorderLayout.WEST); south.add(r, BorderLayout.EAST);
        south.setBorder(new EmptyBorder(10, 0, 0, 0));
        p.add(south, BorderLayout.SOUTH);
        JPanel wrap = Theme.transparent(new BorderLayout()); wrap.setBorder(new EmptyBorder(12, 0, 0, 0)); wrap.add(p);
        return wrap;
    }

    private JPanel buildList() {
        JPanel p = Theme.transparent(new BorderLayout(0, 10)); p.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton ok = Theme.success("Xác nhận nhập kho"), cancel = Theme.danger("Hủy phiếu");
        if (!quyenDuyet) { ok.setEnabled(false); ok.setToolTipText(CHI_QUAN_LY); }
        ok.addActionListener(e -> {
            PhieuNhapKho pn = pick(); if (pn == null) return;
            try { pn.xacNhan(); luuSauXacNhan(pn); reloadList(); }
            catch (IllegalStateException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE); }
        });
        cancel.addActionListener(e -> {
            PhieuNhapKho pn = pick(); if (pn == null) return;
            try { pn.huyPhieu(); pnkDao.capNhat(pn); reloadList(); }
            catch (IllegalStateException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE); }
        });
        JPanel bt = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0)); bt.add(cancel); bt.add(ok);
        p.add(bt, BorderLayout.NORTH);
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Theme.scroll(tbList), Theme.scroll(Theme.table(detail)));
        sp.setResizeWeight(0.6); sp.setBorder(null); sp.setOpaque(false);
        p.add(sp, BorderLayout.CENTER);
        tbList.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            detail.setRowCount(0);
            int r = tbList.getSelectedRow(); if (r < 0) return;
            for (ChiTietPhieuNhapKho c : ds.get(tbList.convertRowIndexToModel(r)).getChiTietList())
                detail.addRow(new Object[]{c.getMaSanPham().getMaSanPham(), c.getMaSanPham().getTenSanPham(), c.getSoLuong(), c.getDonGia(), c.thanhTien()});
        });
        return p;
    }

    private PhieuNhapKho pick() {
        int r = tbList.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return null; }
        return ds.get(tbList.convertRowIndexToModel(r));
    }

    private void addLine() {
        if (cbSp.getSelectedItem() == null) return;
        double gia;
        try { gia = Double.parseDouble(tfGia.getText().trim()); if (gia <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Đơn giá nhập phải là số lớn hơn 0", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE); tfGia.requestFocus(); return; }
        SanPham sp = (SanPham) cbSp.getSelectedItem();
        for (Dong d : dong)
            if (d.sp.equals(sp)) { JOptionPane.showMessageDialog(this, "Sản phẩm đã có trong phiếu", "Trùng dòng", JOptionPane.WARNING_MESSAGE); return; }
        dong.add(new Dong(sp, (Integer) spQty.getValue(), gia));
        tfGia.setText(""); spQty.setValue(1); reloadLines();
    }

    /** Nghiệp vụ: gợi ý nhập hàng - liệt kê sản phẩm đang bán nhưng tồn kho thấp (dưới 10) để chọn vào phiếu. */
    private void goiYSapHet() {
        List<SanPham> thieu = new ArrayList<>();
        for (SanPham s : spDao.getAll())
            if (s.getTrangThai() != TrangThaiSanPham.VO_HIEU_HOA && s.sapHetHang(10)) thieu.add(s);
        if (thieu.isEmpty()) { JOptionPane.showMessageDialog(this, "Hiện không có sản phẩm nào sắp hết hàng", "Gợi ý nhập hàng", JOptionPane.INFORMATION_MESSAGE); return; }
        String[] ten = new String[thieu.size()];
        for (int i = 0; i < ten.length; i++) ten[i] = thieu.get(i).getMaSanPham() + " - " + thieu.get(i).getTenSanPham() + "  (tồn " + thieu.get(i).getSoLuongTon() + ")";
        Object pick = JOptionPane.showInputDialog(this, "Sản phẩm sắp hết hàng (tồn < 10), chọn sản phẩm cần nhập thêm:", "Gợi ý nhập hàng",
                JOptionPane.QUESTION_MESSAGE, null, ten, ten[0]);
        if (pick == null) return;
        for (int i = 0; i < ten.length; i++) if (ten[i].equals(pick)) cbSp.setSelectedItem(thieu.get(i));
    }

    private void reloadLines() {
        lines.setRowCount(0); double t = 0;
        for (Dong d : dong) { lines.addRow(new Object[]{d.sp.getMaSanPham(), d.sp.getTenSanPham(), d.sl, d.gia, d.gia * d.sl}); t += d.gia * d.sl; }
        lbTong.setText(Fmt.money(t));
    }

    private void savePhieu(boolean confirm) {
        if (cbNcc.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        if (tfLyDo.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Lý do nhập không được để trống", "Thông báo", JOptionPane.WARNING_MESSAGE); tfLyDo.requestFocus(); return; }
        if (dong.isEmpty()) { JOptionPane.showMessageDialog(this, "Phiếu nhập chưa có sản phẩm", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        PhieuNhapKho p = new PhieuNhapKho(pnkDao.taoMaMoi(), LocalDateTime.now(), PhienLamViec.nhanVien(),
                (NhaCungCap) cbNcc.getSelectedItem(), tfLyDo.getText().trim(), TrangThaiPhieuNhapKho.CHO_DUYET, "");
        for (Dong d : dong) p.taoChiTiet(d.sp, d.sl, d.gia);
        if (!pnkDao.them(p)) { JOptionPane.showMessageDialog(this, "Không lưu được phiếu nhập", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        if (confirm && quyenDuyet) { p.xacNhan(); luuSauXacNhan(p); }
        JOptionPane.showMessageDialog(this, "Đã lưu phiếu nhập " + p.getMaPhieuNhapKho() + (confirm && quyenDuyet ? " và cập nhật tồn kho" : " (chờ duyệt)"), "Thành công", JOptionPane.INFORMATION_MESSAGE);
        dong.clear(); tfLyDo.setText(""); reloadLines(); reloadList();
    }

    /** Lưu phiếu và tồn kho các sản phẩm sau khi xác nhận (entity đã cộng tồn trong bộ nhớ). */
    private void luuSauXacNhan(PhieuNhapKho p) {
        pnkDao.capNhat(p);
        for (ChiTietPhieuNhapKho c : p.getChiTietList()) spDao.capNhat(c.getMaSanPham());
    }

    private void reloadList() {
        ds = pnkDao.getAll();
        model.setRowCount(0);
        for (PhieuNhapKho p : ds)
            model.addRow(new Object[]{p.getMaPhieuNhapKho(), Fmt.date(p.getNgayNhap()), p.getMaNCC(), p.getMaNhanVien(), p.getLyDoNhap(), p.tinhTongTien(), p.getTrangThai()});
    }

    @Override public void refresh() {
        cbNcc.removeAllItems(); for (NhaCungCap n : nccDao.getAll()) if (n.isTrangThai()) cbNcc.addItem(n);
        cbSp.removeAllItems(); spDao.getAll().forEach(cbSp::addItem);
        reloadList();
    }
}
