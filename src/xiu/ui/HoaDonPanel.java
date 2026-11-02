
package xiu.ui;

import DAO.*;
import ENTITY.*;
import xiu.common.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import ENUM.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Màn hình HÓA ĐƠN gồm 2 tab:
 * 1. Lập hóa đơn & thanh toán.
 * 2. Danh sách hóa đơn: tra cứu, xem chi tiết, in và hủy hóa đơn.
 */
public class HoaDonPanel extends JPanel implements Refreshable {

    private final DefaultTableModel model = Theme.model(
            "Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên", "Khuyến mãi",
            "Phương thức", "Tổng tiền", "Giảm giá", "Thành tiền",
            "Trạng thái", "Lý do hủy", "Phiếu đặt hàng"
    );

    private final DefaultTableModel detail = Theme.model(
            "Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"
    );

    private final JTable tb = Theme.table(model);
    private final JTextField search = Theme.field(20);
    private final JComboBox<Object> cbStatus = new JComboBox<>();

    private final TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(model);

    private final GioHangPanel lap;
    private final JTabbedPane tabs = new JTabbedPane();

    private final HoaDonDAO hdDao = new HoaDonDAO();
    private final SanPhamDAO spDao = new SanPhamDAO();
    private final KhachHangDAO khDao = new KhachHangDAO();

    // Danh sách hóa đơn tương ứng với các dòng trong bảng.
    private final List<HoaDon> hoaDonRows = new ArrayList<>();

    public HoaDonPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        add(Theme.title("Hóa đơn"), BorderLayout.NORTH);

        lap = new GioHangPanel(GioHangPanel.Mode.THANH_TOAN, this::reload);

        tabs.setFont(Theme.BOLD);
        tabs.addTab("Lập hóa đơn & thanh toán", lap);
        tabs.addTab("Danh sách hóa đơn", buildList());

        add(tabs, BorderLayout.CENTER);

        reload();
    }

    private JPanel buildList() {
        JPanel p = Theme.transparent(new BorderLayout(0, 10));
        p.setBorder(new EmptyBorder(12, 0, 0, 0));

        tb.setRowSorter(sorter);
        tb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Độ rộng 12 cột.
        int[] w = {
                80, 145, 200, 190, 160, 120,
                120, 110, 120, 130, 160, 130
        };

        for (int i = 0; i < w.length; i++) {
            tb.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        JPanel bar = Theme.transparent(new BorderLayout());

        JPanel left = Theme.transparent(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );

        cbStatus.setFont(Theme.BASE);
        cbStatus.addItem("Tất cả");

        for (TrangThaiHoaDon t : TrangThaiHoaDon.values()) {
            cbStatus.addItem(t);
        }

        left.add(Theme.label("Tìm kiếm:"));
        left.add(search);
        left.add(Theme.label("Trạng thái:"));
        left.add(cbStatus);

        JPanel right = Theme.transparent(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        JButton view = Theme.ghost("Xem / In hóa đơn");
        JButton cancel = Theme.danger("Hủy hóa đơn");

        view.addActionListener(e -> {
            HoaDon h = selected(true);
            if (h != null) showReceipt(this, h);
        });

        cancel.addActionListener(e -> cancelInvoice());

        right.add(view);
        right.add(cancel);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        p.add(bar, BorderLayout.NORTH);

        JSplitPane sp = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                Theme.scroll(tb),
                Theme.scroll(Theme.table(detail))
        );

        sp.setResizeWeight(0.65);
        sp.setBorder(null);
        sp.setOpaque(false);

        p.add(sp, BorderLayout.CENTER);

        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            public void changedUpdate(DocumentEvent e) {
                filter();
            }
        });

        cbStatus.addActionListener(e -> filter());

        tb.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            detail.setRowCount(0);

            HoaDon h = selected(false);

            if (h != null) {
                for (ChiTietHoaDon c : h.getChiTietList()) {
                    detail.addRow(new Object[]{
                            c.getMaSanPham().getMaSanPham(),
                            c.getMaSanPham().getTenSanPham(),
                            c.getSoLuong(),
                            c.getDonGia(),
                            c.thanhTien()
                    });
                }
            }
        });

        return p;
    }

    private void filter() {
        List<RowFilter<DefaultTableModel, Integer>> fs =
                new ArrayList<>();

        String t = search.getText().trim();

        if (!t.isEmpty()) {
            fs.add(RowFilter.regexFilter(
                    "(?i)" + Pattern.quote(t)
            ));
        }

        if (cbStatus.getSelectedIndex() > 0) {
            fs.add(RowFilter.regexFilter(
                    "^" + Pattern.quote(
                            cbStatus.getSelectedItem().toString()
                    ) + "$",
                    9
            ));
        }

        sorter.setRowFilter(
                fs.isEmpty() ? null : RowFilter.andFilter(fs)
        );
    }

    private HoaDon selected(boolean warn) {
        int r = tb.getSelectedRow();

        if (r < 0) {
            if (warn) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn một hóa đơn",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            return null;
        }

        int modelRow = tb.convertRowIndexToModel(r);

        if (modelRow < 0 || modelRow >= hoaDonRows.size()) {
            return null;
        }

        return hoaDonRows.get(modelRow);
    }

    /** Tính số tiền được giảm theo khuyến mãi của hóa đơn. */
    private static double tienGiam(HoaDon h) {
        if (h.getMaKhuyenMai() == null) return 0;

        return h.getMaKhuyenMai().tinhTienGiam(
                h.tinhTongTien()
        );
    }

    /** Thành tiền sau khi trừ khuyến mãi. */
    private static double thanhTien(HoaDon h) {
        return h.tinhTongTien() - tienGiam(h);
    }

    private void reload() {
        model.setRowCount(0);
        hoaDonRows.clear();

        List<HoaDon> ds = hdDao.getAll();

        for (HoaDon h : ds) {
            hoaDonRows.add(h);

            model.addRow(new Object[]{
                    h.getMaHoaDon(),
                    Fmt.dateTime(h.getNgayLap()),
                    h.getMaKhachHang(),
                    h.getMaNhanVien(),
                    h.getMaKhuyenMai() == null
                            ? ""
                            : h.getMaKhuyenMai().getTenKhuyenMai(),
                    h.getPhuongThucThanhToan(),
                    h.tinhTongTien(),
                    tienGiam(h),
                    thanhTien(h),
                    h.getTrangThai(),
                    h.getLyDoHuyHoaDon() == null
                            ? ""
                            : h.getLyDoHuyHoaDon(),
                    h.getMaPhieuDatHang() == null
                            ? ""
                            : h.getMaPhieuDatHang().getMaPhieuDatHang()
            });
        }

        detail.setRowCount(0);
        filter();
    }

    private void cancelInvoice() {
        HoaDon h = selected(true);
        if (h == null) return;

        if (h.getTrangThai() == TrangThaiHoaDon.DA_HUY) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hóa đơn này đã được hủy",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String reason = JOptionPane.showInputDialog(
                this,
                "Nhập lý do hủy hóa đơn " + h.getMaHoaDon() + ":",
                "Hủy hóa đơn",
                JOptionPane.QUESTION_MESSAGE
        );

        if (reason == null) return;

        if (reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Lý do hủy không được để trống",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Tính điểm cần thu hồi trước khi thay đổi trạng thái hóa đơn.
        int diemThuHoi = (int) (thanhTien(h) / 100_000);

        // Cập nhật trạng thái và lý do hủy trong entity.
        h.huyHoaDon(reason.trim());

        // Lưu trạng thái hóa đơn và trạng thái phiếu đặt hàng liên quan.
        if (!hdDao.capNhat(h)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể cập nhật hóa đơn trong cơ sở dữ liệu",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            reload();
            return;
        }

        // Hoàn lại số lượng tồn kho trong entity.
        for (ChiTietHoaDon c : h.getChiTietList()) {
            c.getMaSanPham().nhapKho(c.getSoLuong());

            // Cập nhật tồn kho vào SQL Server.
            if (!spDao.capNhat(c.getMaSanPham())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể cập nhật tồn kho sản phẩm "
                                + c.getMaSanPham().getMaSanPham(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        // Thu hồi điểm tích lũy và lưu lại khách hàng.
        KhachHang kh = h.getMaKhachHang();

        if (kh != null && diemThuHoi > 0) {
            kh.capNhatDiemTichLuy(-diemThuHoi);

            if (!khDao.capNhat(kh)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể cập nhật điểm tích lũy khách hàng",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        reload();
    }

    @Override
    public void refresh() {
        lap.refresh();
        reload();
    }

    /** Hộp thoại xem/in hóa đơn dạng văn bản. */
    public static void showReceipt(Component parent, HoaDon h) {
        StringBuilder sb = new StringBuilder();

        sb.append("        SIÊU THỊ ĐIỆN MÁY XIU\n");
        sb.append("        HÓA ĐƠN BÁN HÀNG\n");
        sb.append("--------------------------------------------\n");

        sb.append("Số HĐ      : ")
                .append(h.getMaHoaDon()).append('\n');

        sb.append("Ngày lập   : ")
                .append(Fmt.dateTime(h.getNgayLap())).append('\n');

        sb.append("Khách hàng : ")
                .append(h.getMaKhachHang()).append('\n');

        sb.append("Nhân viên  : ")
                .append(h.getMaNhanVien().getHoTenNhanVien()).append('\n');

        sb.append("Thanh toán : ")
                .append(h.getPhuongThucThanhToan()).append('\n');

        sb.append("--------------------------------------------\n");

        for (ChiTietHoaDon c : h.getChiTietList()) {
            sb.append(String.format(
                    "%s%n   %d x %s = %s%n",
                    c.getMaSanPham().getTenSanPham(),
                    c.getSoLuong(),
                    Fmt.money(c.getDonGia()),
                    Fmt.money(c.thanhTien())
            ));
        }

        sb.append("--------------------------------------------\n");

        sb.append(String.format(
                "Tổng tiền  : %s%n",
                Fmt.money(h.tinhTongTien())
        ));

        sb.append(String.format(
                "Giảm giá   : %s%n",
                Fmt.money(tienGiam(h))
        ));

        sb.append(String.format(
                "THÀNH TIỀN : %s%n",
                Fmt.money(thanhTien(h))
        ));

        sb.append("Trạng thái : ")
                .append(h.getTrangThai()).append('\n');

        if (h.getLyDoHuyHoaDon() != null
                && !h.getLyDoHuyHoaDon().isEmpty()) {
            sb.append("Lý do hủy  : ")
                    .append(h.getLyDoHuyHoaDon()).append('\n');
        }

        sb.append("\n        Cảm ơn quý khách!");

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        ta.setEditable(false);
        ta.setBorder(new EmptyBorder(12, 14, 12, 14));

        JDialog d = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                "Hóa đơn " + h.getMaHoaDon(),
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JButton print = Theme.primary("In");
        JButton close = Theme.ghost("Đóng");

        print.addActionListener(e -> {
            try {
                ta.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        d,
                        "Không thể in: " + ex.getMessage()
                );
            }
        });

        close.addActionListener(e -> d.dispose());

        JPanel bt = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 8)
        );

        bt.add(close);
        bt.add(print);

        d.add(new JScrollPane(ta));
        d.add(bt, BorderLayout.SOUTH);

        d.setSize(430, 520);
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }
}
