package xiu.ui;

import xiu.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import ENTITY.*;

import java.awt.*;

/** Màn hình đặt hàng: tab lập phiếu + tab danh sách phiếu (xác nhận / hủy). */
public class DatHangPanel extends JPanel implements Refreshable {
    private final DefaultTableModel model = Store.model("Mã phiếu", "Ngày đặt", "Khách hàng", "Nhân viên", "Tổng tiền", "Trạng thái", "Ghi chú");
    private final DefaultTableModel detail = Store.model("Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền");
    private final JTable tb = Theme.table(model);
    private final GioHangPanel lap;

    public DatHangPanel() {
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        add(Theme.title("Đặt hàng"), BorderLayout.NORTH);
        lap = new GioHangPanel(GioHangPanel.Mode.DAT_HANG, this::reload);

        JPanel list = Theme.transparent(new BorderLayout(0, 10));
        list.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton ok = Theme.success("Xác nhận đặt hàng"), cancel = Theme.danger("Hủy phiếu");
        ok.addActionListener(e -> confirm());
        cancel.addActionListener(e -> cancelPhieu());
        JPanel bt = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0)); bt.add(cancel); bt.add(ok);
        list.add(bt, BorderLayout.NORTH);
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Theme.scroll(tb), Theme.scroll(Theme.table(detail)));
        sp.setResizeWeight(0.6); sp.setBorder(null); sp.setOpaque(false);
        list.add(sp, BorderLayout.CENTER);
        tb.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            detail.setRowCount(0);
            PhieuDatHang p = selected(false);
            if (p != null) for (ChiTietPhieuDatHang c : p.getChiTietList())
                detail.addRow(new Object[]{c.getMaSanPham().getMaSanPham(), c.getMaSanPham().getTenSanPham(), c.getSoLuong(), c.getDonGia(), c.getSoLuong() * c.getDonGia()});
        });

        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(Theme.BOLD);
        tabs.addTab("Lập phiếu đặt hàng", lap);
        tabs.addTab("Danh sách phiếu đặt hàng", list);
        add(tabs, BorderLayout.CENTER);
        reload();
    }

    private PhieuDatHang selected(boolean warn) {
        int r = tb.getSelectedRow();
        if (r < 0) { if (warn) JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu đặt hàng", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return null; }
        return Store.phieuDat.get(tb.convertRowIndexToModel(r));
    }

    private void confirm() {
        PhieuDatHang p = selected(true); if (p == null) return;
        if (p.isTrangThai()) { JOptionPane.showMessageDialog(this, "Phiếu này đã được xác nhận", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        p.xacNhan(); reload();
    }

    private void cancelPhieu() {
        PhieuDatHang p = selected(true); if (p == null) return;
        if (Store.hoaDonCuaPhieu(p) != null) { JOptionPane.showMessageDialog(this, "Phiếu đã lập hóa đơn, không thể hủy", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this, "Hủy phiếu " + p.getMaPhieuDatHang() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        p.huyDat();
        Store.phieuDat.remove(p);       // entity chỉ có cờ boolean nên phiếu hủy được loại khỏi danh sách
        reload();
    }

    private void reload() {
        model.setRowCount(0);
        for (PhieuDatHang p : Store.phieuDat)
            model.addRow(new Object[]{p.getMaPhieuDatHang(), Fmt.dateTime(p.getNgayDatHang()), p.getMaKhachHang(), p.getMaNhanVien(),
                    p.tinhTongTien(), Store.trangThaiPhieuDat(p), p.getGhiChu() == null ? "" : p.getGhiChu()});
    }

    @Override public void refresh() { lap.refresh(); reload(); }
}
