
package xiu.ui;

import DAO.*;
import xiu.common.*;
import ENTITY.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Màn hình đặt hàng: tab lập phiếu + tab danh sách phiếu (xác nhận / hủy). */
public class DatHangPanel extends JPanel implements Refreshable {
    private final DefaultTableModel model = Theme.model(
            "Mã phiếu", "Ngày đặt", "Khách hàng", "Nhân viên",
            "Tổng tiền", "Trạng thái", "Ghi chú");

    private final DefaultTableModel detail = Theme.model(
            "Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền");

    private final JTable tb = Theme.table(model);
    private final GioHangPanel lap;

    private final PhieuDatHangDAO pdhDao = new PhieuDatHangDAO();
    private final HoaDonDAO hdDao = new HoaDonDAO();

    // Danh sách tương ứng với thứ tự các dòng trong model.
    private final List<PhieuDatHang> phieuRows = new ArrayList<>();

    public DatHangPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        add(Theme.title("Đặt hàng"), BorderLayout.NORTH);

        lap = new GioHangPanel(GioHangPanel.Mode.DAT_HANG, this::reload);

        JPanel list = Theme.transparent(new BorderLayout(0, 10));
        list.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton ok = Theme.success("Xác nhận đặt hàng");
        JButton cancel = Theme.danger("Hủy phiếu");

        ok.addActionListener(e -> confirm());
        cancel.addActionListener(e -> cancelPhieu());

        JPanel bt = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bt.add(cancel);
        bt.add(ok);
        list.add(bt, BorderLayout.NORTH);

        JSplitPane sp = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                Theme.scroll(tb),
                Theme.scroll(Theme.table(detail))
        );
        sp.setResizeWeight(0.6);
        sp.setBorder(null);
        sp.setOpaque(false);
        list.add(sp, BorderLayout.CENTER);

        tb.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            detail.setRowCount(0);
            PhieuDatHang p = selected(false);

            if (p != null) {
                for (ChiTietPhieuDatHang c : p.getChiTietList()) {
                    detail.addRow(new Object[]{
                            c.getMaSanPham().getMaSanPham(),
                            c.getMaSanPham().getTenSanPham(),
                            c.getSoLuong(),
                            c.getDonGia(),
                            c.getSoLuong() * c.getDonGia()
                    });
                }
            }
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BOLD);
        tabs.addTab("Lập phiếu đặt hàng", lap);
        tabs.addTab("Danh sách phiếu đặt hàng", list);

        add(tabs, BorderLayout.CENTER);

        reload();
    }

    private PhieuDatHang selected(boolean warn) {
        int r = tb.getSelectedRow();

        if (r < 0) {
            if (warn) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn một phiếu đặt hàng",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            return null;
        }

        int modelRow = tb.convertRowIndexToModel(r);

        if (modelRow < 0 || modelRow >= phieuRows.size()) {
            return null;
        }

        return phieuRows.get(modelRow);
    }

    private void confirm() {
        PhieuDatHang p = selected(true);
        if (p == null) return;

        if (p.daXacNhan()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Phiếu này đã được xác nhận",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        p.xacNhan();

        if (!pdhDao.capNhat(p)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể cập nhật trạng thái phiếu đặt hàng",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        reload();
    }

    private void cancelPhieu() {
        PhieuDatHang p = selected(true);
        if (p == null) return;

        // Không cho hủy phiếu đã được lập hóa đơn.
        if (hdDao.timTheoPhieuDat(p) != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Phiếu đã lập hóa đơn, không thể hủy",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hủy phiếu " + p.getMaPhieuDatHang() + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // DAO thực hiện cập nhật trạng thái thành Đã hủy trong SQL Server.
        if (!pdhDao.xoa(p)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể hủy phiếu đặt hàng",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        reload();
    }

    private void reload() {
        model.setRowCount(0);
        phieuRows.clear();

        List<PhieuDatHang> ds = pdhDao.getAll();

        for (PhieuDatHang p : ds) {
            phieuRows.add(p);

            String trangThai = p.daXacNhan()
                    ? "Đã xác nhận"
                    : "Chờ xử lý";

            // Nếu phiếu đã có hóa đơn hợp lệ thì hiển thị Đã giao.
            if (hdDao.timTheoPhieuDat(p) != null) {
                trangThai = "Đã giao";
            }

            model.addRow(new Object[]{
                    p.getMaPhieuDatHang(),
                    Fmt.dateTime(p.getNgayDatHang()),
                    p.getMaKhachHang(),
                    p.getMaNhanVien(),
                    p.tinhTongTien(),
                    trangThai,
                    p.getGhiChu() == null ? "" : p.getGhiChu()
            });
        }

        detail.setRowCount(0);
    }

    @Override
    public void refresh() {
        lap.refresh();
        reload();
    }
}
