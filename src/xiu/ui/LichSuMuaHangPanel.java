package xiu.ui;

import DAO.*;
import ENTITY.*;
import ENUM.*;
import xiu.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Tra cứu lịch sử mua hàng của khách hàng (theo mã hoặc số điện thoại). */
public class LichSuMuaHangPanel extends JPanel implements Refreshable {
    private final JTextField tfKey = Theme.field(20);
    private final JLabel lbInfo = new JLabel("Nhập mã hoặc số điện thoại khách hàng rồi bấm Tìm."), lbSum = new JLabel(" ");
    private final DefaultTableModel history = Theme.model("Mã HĐ", "Ngày lập", "Nhân viên", "Phương thức", "Thành tiền", "Trạng thái");
    private final DefaultTableModel detail = Theme.model("Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền");
    private final JTable tbHis = Theme.table(history);
    private final List<HoaDon> shown = new ArrayList<>();

    public LichSuMuaHangPanel() {
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        JPanel top = Theme.transparent(new BorderLayout(0, 12));
        top.add(Theme.title("Lịch sử mua hàng"), BorderLayout.NORTH);
        JPanel card = Theme.card();
        JPanel row = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton find = Theme.primary("Tìm");
        find.addActionListener(e -> search()); tfKey.addActionListener(e -> search());
        row.add(Theme.label("Mã KH / SĐT:")); row.add(tfKey); row.add(find);
        lbInfo.setFont(Theme.BOLD); lbInfo.setForeground(Theme.NAVY); lbSum.setFont(Theme.BASE); lbSum.setForeground(Theme.MUTED);
        JPanel info = Theme.transparent(new GridLayout(2, 1, 0, 2)); info.add(lbInfo); info.add(lbSum);
        info.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(row, BorderLayout.NORTH); card.add(info, BorderLayout.CENTER);
        top.add(card, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Theme.scroll(tbHis), Theme.scroll(Theme.table(detail)));
        sp.setResizeWeight(0.6); sp.setBorder(null); sp.setOpaque(false);
        add(sp, BorderLayout.CENTER);
        tbHis.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            detail.setRowCount(0);
            int r = tbHis.getSelectedRow(); if (r < 0) return;
            for (ChiTietHoaDon c : shown.get(tbHis.convertRowIndexToModel(r)).getChiTietList())
                detail.addRow(new Object[]{c.getMaSanPham().getMaSanPham(), c.getMaSanPham().getTenSanPham(), c.getSoLuong(), c.getDonGia(), c.thanhTien()});
        });
    }

    private void search() {
        String k = tfKey.getText().trim();
        history.setRowCount(0); detail.setRowCount(0); shown.clear();
        if (k.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hoặc số điện thoại khách hàng", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
        KhachHang kh = null;
        for (KhachHang x : new KhachHangDAO().getAll())
            if (x.getMaKhachHang().equalsIgnoreCase(k) || x.getSoDienThoai().equals(k)) { kh = x; break; }
        if (kh == null) { lbInfo.setText("Không tìm thấy khách hàng \"" + k + "\""); lbSum.setText(" "); return; }
        lbInfo.setText(kh.getMaKhachHang() + " - " + kh.getHoTenKhachHang() + "   |   " + kh.getSoDienThoai());
        double sum = 0; int n = 0;
        for (HoaDon h : new HoaDonDAO().getAll()) {
            if (h.getMaKhachHang() == null
                    || !h.getMaKhachHang().getMaKhachHang()
                            .equalsIgnoreCase(kh.getMaKhachHang())) {
                continue;
            }

            shown.add(h);

            history.addRow(new Object[]{
                h.getMaHoaDon(),
                Fmt.dateTime(h.getNgayLap()),
                h.getMaNhanVien(),
                h.getPhuongThucThanhToan(),
                h.thanhTienDaLap(),
                h.getTrangThai()
            });

            if (h.daThanhToan()) {
                sum += h.thanhTienDaLap();
                n++;
            }
        }
        lbSum.setText(n + " hóa đơn đã thanh toán  |  Tổng chi tiêu: " + Fmt.money(sum) + "  |  Điểm tích lũy: " + kh.getDiemTichLuy() + "  |  Hạng: " + kh.getLoaiKH());
    }

    @Override public void refresh() { if (!tfKey.getText().trim().isEmpty()) search(); }
}
