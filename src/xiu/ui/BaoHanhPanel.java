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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Bảo hành: tiếp nhận phiếu bảo hành (kiểm tra thời hạn theo hóa đơn) và theo dõi trả máy. */
public class BaoHanhPanel extends JPanel implements Refreshable {
    private static class Dong {
        final SanPham sp; final String tinhTrang, noiDung;
        Dong(SanPham sp, String tt, String nd) { this.sp = sp; this.tinhTrang = tt; this.noiDung = nd; }
    }

    private final List<Dong> dong = new ArrayList<>();
    private final JComboBox<KhachHang> cbKh = new JComboBox<>();
    private final JComboBox<SanPham> cbSp = new JComboBox<>();
    private final JTextField tfHen = Theme.field(12), tfTinhTrang = Theme.field(16), tfNoiDung = Theme.field(22);
    private final DefaultTableModel lines = Theme.model("Mã SP", "Tên sản phẩm", "Tình trạng sản phẩm", "Nội dung bảo hành");
    private final DefaultTableModel model = Theme.model("Mã phiếu", "Khách hàng", "Ngày tiếp nhận", "Ngày hẹn trả", "Trạng thái");
    private final DefaultTableModel detail = Theme.model("Mã SP", "Tên sản phẩm", "Tình trạng sản phẩm", "Nội dung bảo hành");
    private final JTable tbLines = Theme.table(lines), tbList = Theme.table(model);
    private final PhieuBaoHanhDAO pbhDao = new PhieuBaoHanhDAO();
    private final KhachHangDAO khDao = new KhachHangDAO();
    private final SanPhamDAO spDao = new SanPhamDAO();
    private final HoaDonDAO hdDao = new HoaDonDAO();
    private List<PhieuBaoHanh> ds = new ArrayList<>();

    public BaoHanhPanel() {
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        add(Theme.title("Bảo hành"), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(Theme.BOLD);
        tabs.addTab("Tiếp nhận bảo hành", buildCreate());
        tabs.addTab("Danh sách phiếu bảo hành", buildList());
        add(tabs, BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildCreate() {
        JPanel p = Theme.card();
        JPanel head = new JPanel(new GridBagLayout()); head.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints(); g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.HORIZONTAL;
        cbKh.setFont(Theme.BASE); cbSp.setFont(Theme.BASE);
        tfHen.setText(Fmt.date(LocalDate.now().plusDays(7)));
        g.gridy = 0; g.gridx = 0; head.add(Theme.label("Khách hàng *"), g); g.gridx = 1; g.weightx = 1; head.add(cbKh, g);
        g.gridx = 2; g.weightx = 0; head.add(Theme.label("Ngày tiếp nhận"), g); g.gridx = 3; head.add(Theme.muted(Fmt.date(LocalDate.now()) + " (tự động)"), g);
        g.gridy = 1; g.gridx = 0; head.add(Theme.label("Ngày hẹn trả *"), g); g.gridx = 1; g.weightx = 1; head.add(tfHen, g);
        g.gridx = 2; g.weightx = 0; head.add(Theme.label("Nhân viên"), g); g.gridx = 3; head.add(Theme.muted(PhienLamViec.nhanVien().toString()), g);

        JPanel add = new JPanel(new GridBagLayout()); add.setOpaque(false);
        g = new GridBagConstraints(); g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy = 0; g.gridx = 0; add.add(Theme.label("Sản phẩm *"), g); g.gridx = 1; g.weightx = 1; add.add(cbSp, g);
        g.gridx = 2; g.weightx = 0; add.add(Theme.label("Tình trạng *"), g); g.gridx = 3; g.weightx = 1; add.add(tfTinhTrang, g);
        g.gridx = 4; g.weightx = 0; add.add(Theme.label("Nội dung *"), g); g.gridx = 5; g.weightx = 1; add.add(tfNoiDung, g);
        JButton btn = Theme.primary("+ Thêm dòng"); btn.addActionListener(e -> addLine());
        g.gridx = 6; g.weightx = 0; add.add(btn, g);

        JPanel north = Theme.transparent(new BorderLayout(0, 8)); north.add(head, BorderLayout.NORTH); north.add(add, BorderLayout.CENTER);
        p.add(north, BorderLayout.NORTH);
        p.add(Theme.scroll(tbLines), BorderLayout.CENTER);

        JButton rm = Theme.ghost("Xóa dòng"), save = Theme.success("Tiếp nhận bảo hành");
        rm.addActionListener(e -> { int r = tbLines.getSelectedRow(); if (r >= 0) { dong.remove(tbLines.convertRowIndexToModel(r)); reloadLines(); } });
        save.addActionListener(e -> save());
        JPanel south = Theme.transparent(new BorderLayout()); south.setBorder(new EmptyBorder(10, 0, 0, 0));
        JPanel l = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0)); l.add(rm);
        JPanel r = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0)); r.add(save);
        south.add(l, BorderLayout.WEST); south.add(r, BorderLayout.EAST);
        p.add(south, BorderLayout.SOUTH);
        JPanel wrap = Theme.transparent(new BorderLayout()); wrap.setBorder(new EmptyBorder(12, 0, 0, 0)); wrap.add(p);
        return wrap;
    }

    private JPanel buildList() {
        JPanel p = Theme.transparent(new BorderLayout(0, 10)); p.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton done = Theme.success("Đã trả máy cho khách"), cancel = Theme.danger("Hủy phiếu");
        done.addActionListener(e -> {
            PhieuBaoHanh b = pick(); if (b == null) return;
            if (!b.dangXuLy()) { JOptionPane.showMessageDialog(this, "Chỉ trả được phiếu đang xử lý", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            b.traSanPham(); pbhDao.capNhat(b); reloadList();
        });
        cancel.addActionListener(e -> {
            PhieuBaoHanh b = pick(); if (b == null) return;
            if (!b.dangXuLy()) { JOptionPane.showMessageDialog(this, "Chỉ hủy được phiếu đang xử lý", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            b.huyPhieu(); pbhDao.capNhat(b); reloadList();
        });
        JPanel bt = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0)); bt.add(cancel); bt.add(done);
        p.add(bt, BorderLayout.NORTH);
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Theme.scroll(tbList), Theme.scroll(Theme.table(detail)));
        sp.setResizeWeight(0.6); sp.setBorder(null); sp.setOpaque(false);
        p.add(sp, BorderLayout.CENTER);
        tbList.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            detail.setRowCount(0);
            int r = tbList.getSelectedRow(); if (r < 0) return;
            for (ChiTietPhieuBaoHanh c : ds.get(tbList.convertRowIndexToModel(r)).getChiTietList())
                detail.addRow(new Object[]{c.getMaSanPham().getMaSanPham(), c.getMaSanPham().getTenSanPham(), c.getTinhTrangSanPham(), c.getNoiDungBaoHanh()});
        });
        return p;
    }

    private PhieuBaoHanh pick() {
        int r = tbList.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu bảo hành", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return null; }
        return ds.get(tbList.convertRowIndexToModel(r));
    }

    /** Còn bảo hành nếu khách đã mua (hóa đơn đã thanh toán) trong thời hạn bảo hành của sản phẩm. */
    private boolean conBaoHanh(KhachHang kh, SanPham sp) {
        for (HoaDon h : hdDao.getAll()) {
            if (!h.getMaKhachHang().equals(kh) || !h.daThanhToan()) continue;
            for (ChiTietHoaDon c : h.getChiTietList())
                if (c.getMaSanPham().equals(sp) && sp.conBaoHanh(h.getNgayLap())) return true;
        }
        return false;
    }

    private void addLine() {
        if (cbKh.getSelectedItem() == null || cbSp.getSelectedItem() == null) return;
        if (tfTinhTrang.getText().trim().isEmpty() || tfNoiDung.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tình trạng sản phẩm và nội dung bảo hành không được để trống", "Thông báo", JOptionPane.WARNING_MESSAGE); return;
        }
        SanPham sp = (SanPham) cbSp.getSelectedItem();
        if (!conBaoHanh((KhachHang) cbKh.getSelectedItem(), sp)) {
            int o = JOptionPane.showConfirmDialog(this, "Không tìm thấy hóa đơn còn hạn bảo hành cho sản phẩm này.\nVẫn tiếp nhận (tính phí sửa chữa)?",
                    "Kiểm tra bảo hành", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (o != JOptionPane.YES_OPTION) return;
        }
        dong.add(new Dong(sp, tfTinhTrang.getText().trim(), tfNoiDung.getText().trim()));
        tfTinhTrang.setText(""); tfNoiDung.setText(""); reloadLines();
    }

    private void reloadLines() {
        lines.setRowCount(0);
        for (Dong d : dong) lines.addRow(new Object[]{d.sp.getMaSanPham(), d.sp.getTenSanPham(), d.tinhTrang, d.noiDung});
    }

    private void save() {
        if (cbKh.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        if (dong.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa có sản phẩm cần bảo hành", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        LocalDate hen;
        try { hen = Fmt.parseDate(tfHen.getText()); }
        catch (DateTimeParseException ex) { JOptionPane.showMessageDialog(this, "Ngày hẹn trả không hợp lệ (dd/MM/yyyy)", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        if (hen.isBefore(LocalDate.now())) { JOptionPane.showMessageDialog(this, "Ngày hẹn trả không được trước ngày tiếp nhận", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        PhieuBaoHanh b = new PhieuBaoHanh(pbhDao.taoMaMoi(), (KhachHang) cbKh.getSelectedItem(),
                PhienLamViec.nhanVien(), LocalDateTime.now(), hen.atTime(17, 0), TrangThaiPhieuBaoHanh.DANG_XU_LY, "");
        for (Dong d : dong) b.taoChiTiet(d.sp, d.tinhTrang, d.noiDung);
        if (!pbhDao.them(b)) { JOptionPane.showMessageDialog(this, "Không lưu được phiếu bảo hành", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        JOptionPane.showMessageDialog(this, "Đã tiếp nhận phiếu bảo hành " + b.getMaPhieubaoHanh(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
        dong.clear(); reloadLines(); reloadList();
    }

    private void reloadList() {
        ds = pbhDao.getAll();
        model.setRowCount(0);
        for (PhieuBaoHanh b : ds)
            model.addRow(new Object[]{b.getMaPhieubaoHanh(), b.getMaKhachHang(), Fmt.date(b.getNgayTiepNhan()), Fmt.date(b.getNgayHenTra()),
                    b.trangThaiHienThi()});
    }

    @Override public void refresh() {
        cbKh.removeAllItems(); khDao.getAll().forEach(cbKh::addItem);
        cbSp.removeAllItems(); spDao.getAll().forEach(cbSp::addItem);
        reloadList();
    }
}
