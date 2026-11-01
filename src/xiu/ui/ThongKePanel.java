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
import java.util.*;
import java.util.List;

/**
 * Thống kê số liệu (nhóm Báo biểu). Các tab:
 *  1. Số lượng sản phẩm (đã bán / tồn kho)      6. Khuyến mãi
 *  2. Doanh thu theo tháng                      7. Bảo hành
 *  3. Doanh thu theo nhân viên                  8. Thu - chi theo tháng (bán hàng so với nhập kho)
 *  4. Phương thức thanh toán                    9. Cảnh báo tồn kho (sắp hết / bán chậm)
 *  5. Khách hàng mua nhiều
 * Doanh thu chỉ tính hóa đơn "Đã thanh toán"; chi phí nhập chỉ tính phiếu nhập "Đã xác nhận".
 */
public class ThongKePanel extends JPanel implements Refreshable {
    private final DefaultTableModel spModel = Theme.model("Mã SP", "Tên sản phẩm", "Đã bán", "Tồn kho", "Doanh thu");
    private final DefaultTableModel dtModel = Theme.model("Tháng", "Số hóa đơn", "Doanh thu");
    private final DefaultTableModel nvModel = Theme.model("Mã NV", "Nhân viên", "Số hóa đơn", "Doanh thu", "Tỷ trọng");
    private final DefaultTableModel ptModel = Theme.model("Phương thức", "Số hóa đơn", "Doanh thu", "Tỷ trọng");
    private final DefaultTableModel khModel = Theme.model("Mã KH", "Khách hàng", "Số đơn", "Tổng chi tiêu", "Điểm tích lũy");
    private final DefaultTableModel kmModel = Theme.model("Mã KM", "Chương trình", "Số hóa đơn", "Tổng tiền giảm", "Doanh thu sau giảm");
    private final DefaultTableModel bhModel = Theme.model("Mã SP", "Sản phẩm", "Số lần bảo hành");
    private final DefaultTableModel tcModel = Theme.model("Tháng", "Doanh thu", "Chi nhập kho", "Chênh lệch");
    private final DefaultTableModel tkModel = Theme.model("Mã SP", "Tên sản phẩm", "Tồn kho", "Bán 30 ngày qua", "Cảnh báo");

    private final Charts.Bar barSp = new Charts.Bar(), barDt = new Charts.Bar(), barNv = new Charts.Bar(), barKh = new Charts.Bar(),
            barKm = new Charts.Bar(), barThu = new Charts.Bar(), barChi = new Charts.Bar(), barTk = new Charts.Bar();
    private final Charts.Pie piePt = new Charts.Pie(), pieBh = new Charts.Pie();
    private final JComboBox<Integer> cbYear = new JComboBox<>();
    private final JLabel lbTotal = new JLabel(), lbThuChi = new JLabel();

    private List<HoaDon> hoaDons = new ArrayList<>();
    private List<PhieuNhapKho> phieuNhaps = new ArrayList<>();

    public ThongKePanel() {
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        JPanel head = Theme.transparent(new BorderLayout());
        head.add(Theme.title("Thống kê số liệu"), BorderLayout.WEST);
        JPanel yr = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        int y = LocalDate.now().getYear();
        for (int i = y; i >= y - 2; i--) cbYear.addItem(i);
        cbYear.setFont(Theme.BASE);
        cbYear.addActionListener(e -> { loadRevenue(); loadThuChi(); });
        yr.add(Theme.label("Năm (doanh thu tháng, thu - chi):")); yr.add(cbYear);
        head.add(yr, BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(Theme.BOLD);
        tabs.addTab("Sản phẩm (đã bán / tồn)", tabBang(spModel, "Số lượng đã bán theo sản phẩm", barSp));

        JPanel p2 = Theme.transparent(new BorderLayout(0, 10)); p2.setBorder(new EmptyBorder(12, 0, 0, 0));
        lbTotal.setFont(Theme.BOLD); lbTotal.setForeground(Theme.BLUE);
        p2.add(lbTotal, BorderLayout.NORTH);
        p2.add(bangVaBieuDo(dtModel, "Doanh thu theo tháng (triệu đồng)", barDt), BorderLayout.CENTER);
        tabs.addTab("Doanh thu theo tháng", p2);

        tabs.addTab("Theo nhân viên", tabBang(nvModel, "Doanh thu theo nhân viên (triệu đồng)", barNv));
        tabs.addTab("Phương thức thanh toán", tabBang(ptModel, "Cơ cấu doanh thu theo phương thức", piePt));
        tabs.addTab("Khách hàng mua nhiều", tabBang(khModel, "Tổng chi tiêu - top khách hàng (triệu đồng)", barKh));
        tabs.addTab("Khuyến mãi", tabBang(kmModel, "Tổng tiền giảm theo chương trình (triệu đồng)", barKm));
        tabs.addTab("Bảo hành", tabBang(bhModel, "Phiếu bảo hành theo trạng thái", pieBh));

        JPanel tc = Theme.transparent(new BorderLayout(0, 10)); tc.setBorder(new EmptyBorder(12, 0, 0, 0));
        lbThuChi.setFont(Theme.BOLD); lbThuChi.setForeground(Theme.BLUE);
        tc.add(lbThuChi, BorderLayout.NORTH);
        JPanel two = Theme.transparent(new GridLayout(2, 1, 0, 10));
        JPanel c1 = Theme.card(); c1.add(Theme.label("Doanh thu bán hàng theo tháng (triệu đồng)"), BorderLayout.NORTH); c1.add(barThu);
        JPanel c2 = Theme.card(); c2.add(Theme.label("Chi phí nhập kho theo tháng (triệu đồng)"), BorderLayout.NORTH); c2.add(barChi);
        two.add(c1); two.add(c2);
        JPanel body = Theme.transparent(new GridLayout(1, 2, 14, 0));
        JPanel t1 = Theme.card(); t1.add(Theme.scroll(Theme.table(tcModel)));
        body.add(t1); body.add(two);
        tc.add(body, BorderLayout.CENTER);
        tabs.addTab("Thu - chi theo tháng", tc);

        tabs.addTab("Cảnh báo tồn kho", tabBang(tkModel, "Tồn kho của sản phẩm cần chú ý", barTk));
        add(tabs, BorderLayout.CENTER);
        refresh();
    }

    // ---------- khung hiển thị ----------
    private JPanel bangVaBieuDo(DefaultTableModel m, String tieuDe, JComponent chart) {
        JPanel p = Theme.transparent(new GridLayout(1, 2, 14, 0));
        JPanel c1 = Theme.card(); c1.add(Theme.scroll(Theme.table(m)));
        JPanel c2 = Theme.card(); c2.add(Theme.label(tieuDe), BorderLayout.NORTH); c2.add(chart);
        p.add(c1); p.add(c2);
        return p;
    }

    private JPanel tabBang(DefaultTableModel m, String tieuDe, JComponent chart) {
        JPanel p = bangVaBieuDo(m, tieuDe, chart);
        p.setBorder(new EmptyBorder(12, 0, 0, 0));
        return p;
    }

    @Override public void refresh() {
        hoaDons = new HoaDonDAO().getAll();
        phieuNhaps = new PhieuNhapKhoDAO().getAll();
        loadProducts(); loadRevenue(); loadNhanVien(); loadThanhToan(); loadKhachHang();
        loadKhuyenMai(); loadBaoHanh(); loadThuChi(); loadTonKho();
    }

    private static String pct(double part, double total) { return total == 0 ? "0%" : String.format("%.1f%%", part * 100 / total); }

    /** Đưa tối đa n dòng đầu (đã sắp xếp) lên biểu đồ cột; cột nhãn = labelCol, cột giá trị = valCol (chia scale). */
    private static void duaLenCot(Charts.Bar bar, List<Object[]> rows, int labelCol, int valCol, double scale, String unit, int n) {
        int k = Math.min(n, rows.size());
        String[] lb = new String[k]; double[] v = new double[k];
        for (int i = 0; i < k; i++) { lb[i] = rows.get(i)[labelCol].toString(); v[i] = ((Number) rows.get(i)[valCol]).doubleValue() / scale; }
        bar.setData(lb, v, unit);
    }

    // ---------- 1. Sản phẩm ----------
    private void loadProducts() {
        Map<String, double[]> sold = new HashMap<>();
        for (HoaDon h : hoaDons) {
            if (!h.daThanhToan()) continue;
            for (ChiTietHoaDon c : h.getChiTietList()) {
                double[] a = sold.computeIfAbsent(c.getMaSanPham().getMaSanPham(), k -> new double[2]);
                a[0] += c.getSoLuong(); a[1] += c.thanhTien();
            }
        }
        List<Object[]> rows = new ArrayList<>();
        for (SanPham s : new SanPhamDAO().getAll()) {
            double[] a = sold.getOrDefault(s.getMaSanPham(), new double[2]);
            rows.add(new Object[]{s.getMaSanPham(), s.getTenSanPham(), (int) a[0], s.getSoLuongTon(), a[1]});
        }
        rows.sort((x, y) -> (Integer) y[2] - (Integer) x[2]);
        spModel.setRowCount(0);
        rows.forEach(spModel::addRow);
        duaLenCot(barSp, rows, 0, 2, 1, "Số lượng", 8);
    }

    // ---------- 2. Doanh thu theo tháng ----------
    private void loadRevenue() {
        if (cbYear.getSelectedItem() == null) return;
        int year = (Integer) cbYear.getSelectedItem();
        double[] rev = new double[12]; int[] cnt = new int[12];
        for (HoaDon h : hoaDons) {
            if (!h.daThanhToan() || h.getNgayLap().getYear() != year) continue;
            int m = h.getNgayLap().getMonthValue() - 1;
            rev[m] += h.thanhTienDaLap(); cnt[m]++;
        }
        dtModel.setRowCount(0);
        String[] lb = new String[12]; double[] v = new double[12]; double sum = 0;
        for (int m = 0; m < 12; m++) {
            dtModel.addRow(new Object[]{"Tháng " + (m + 1), cnt[m], rev[m]});
            lb[m] = "T" + (m + 1); v[m] = rev[m] / 1_000_000.0; sum += rev[m];
        }
        barDt.setData(lb, v, "triệu đồng");
        lbTotal.setText("Tổng doanh thu năm " + year + ": " + Fmt.money(sum));
    }

    // ---------- 3. Theo nhân viên ----------
    private void loadNhanVien() {
        Map<String, Object[]> map = new LinkedHashMap<>();
        double tong = 0;
        for (HoaDon h : hoaDons) {
            if (!h.daThanhToan()) continue;
            NhanVien nv = h.getMaNhanVien();
            Object[] a = map.computeIfAbsent(nv.getMaNhanVien(), k -> new Object[]{nv.getMaNhanVien(), nv.getHoTenNhanVien(), 0, 0.0});
            a[2] = (Integer) a[2] + 1; a[3] = (Double) a[3] + h.thanhTienDaLap();
            tong += h.thanhTienDaLap();
        }
        List<Object[]> rows = new ArrayList<>(map.values());
        rows.sort((x, y) -> Double.compare((Double) y[3], (Double) x[3]));
        nvModel.setRowCount(0);
        for (Object[] r : rows) nvModel.addRow(new Object[]{r[0], r[1], r[2], r[3], pct((Double) r[3], tong)});
        duaLenCot(barNv, rows, 1, 3, 1_000_000.0, "triệu đồng", 8);
    }

    // ---------- 4. Phương thức thanh toán ----------
    private void loadThanhToan() {
        Map<PhuongThucThanhToan, double[]> map = new EnumMap<>(PhuongThucThanhToan.class);
        double tong = 0;
        for (HoaDon h : hoaDons) {
            if (!h.daThanhToan()) continue;
            double[] a = map.computeIfAbsent(h.getPhuongThucThanhToan(), k -> new double[2]);
            a[0]++; a[1] += h.thanhTienDaLap(); tong += h.thanhTienDaLap();
        }
        ptModel.setRowCount(0);
        List<String> lb = new ArrayList<>(); List<Double> vl = new ArrayList<>();
        for (PhuongThucThanhToan p : PhuongThucThanhToan.values()) {
            double[] a = map.getOrDefault(p, new double[2]);
            ptModel.addRow(new Object[]{p, (int) a[0], a[1], pct(a[1], tong)});
            lb.add(p.toString()); vl.add(a[1]);
        }
        piePt.setData(lb.toArray(new String[0]), vl.stream().mapToDouble(Double::doubleValue).toArray());
    }

    // ---------- 5. Khách hàng ----------
    private void loadKhachHang() {
        Map<String, Object[]> map = new LinkedHashMap<>();
        for (HoaDon h : hoaDons) {
            if (!h.daThanhToan()) continue;
            KhachHang k = h.getMaKhachHang();
            Object[] a = map.computeIfAbsent(k.getMaKhachHang(), x -> new Object[]{k.getMaKhachHang(), k.getHoTenKhachHang(), 0, 0.0, k.getDiemTichLuy()});
            a[2] = (Integer) a[2] + 1; a[3] = (Double) a[3] + h.thanhTienDaLap();
        }
        List<Object[]> rows = new ArrayList<>(map.values());
        rows.sort((x, y) -> Double.compare((Double) y[3], (Double) x[3]));
        khModel.setRowCount(0);
        rows.forEach(khModel::addRow);
        duaLenCot(barKh, rows, 1, 3, 1_000_000.0, "triệu đồng", 8);
    }

    // ---------- 6. Khuyến mãi ----------
    private void loadKhuyenMai() {
        Map<String, Object[]> map = new LinkedHashMap<>();
        for (HoaDon h : hoaDons) {
            if (!h.daThanhToan() || h.getMaKhuyenMai() == null) continue;
            KhuyenMai k = h.getMaKhuyenMai();
            Object[] a = map.computeIfAbsent(k.getMaKhuyenMai(), x -> new Object[]{k.getMaKhuyenMai(), k.getTenKhuyenMai(), 0, 0.0, 0.0});
            a[2] = (Integer) a[2] + 1; a[3] = (Double) a[3] + h.tienGiam(); a[4] = (Double) a[4] + h.thanhTienDaLap();
        }
        List<Object[]> rows = new ArrayList<>(map.values());
        rows.sort((x, y) -> Double.compare((Double) y[3], (Double) x[3]));
        kmModel.setRowCount(0);
        rows.forEach(kmModel::addRow);
        duaLenCot(barKm, rows, 0, 3, 1_000_000.0, "triệu đồng", 8);
    }

    // ---------- 7. Bảo hành ----------
    private void loadBaoHanh() {
        List<PhieuBaoHanh> ds = new PhieuBaoHanhDAO().getAll();
        Map<TrangThaiPhieuBaoHanh, Integer> theoTt = new EnumMap<>(TrangThaiPhieuBaoHanh.class);
        Map<String, Object[]> theoSp = new LinkedHashMap<>();
        for (PhieuBaoHanh p : ds) {
            theoTt.merge(p.getTrangThai(), 1, Integer::sum);
            for (ChiTietPhieuBaoHanh c : p.getChiTietList()) {
                SanPham s = c.getMaSanPham();
                Object[] a = theoSp.computeIfAbsent(s.getMaSanPham(), k -> new Object[]{s.getMaSanPham(), s.getTenSanPham(), 0});
                a[2] = (Integer) a[2] + 1;
            }
        }
        List<Object[]> rows = new ArrayList<>(theoSp.values());
        rows.sort((x, y) -> (Integer) y[2] - (Integer) x[2]);
        bhModel.setRowCount(0);
        rows.forEach(bhModel::addRow);
        TrangThaiPhieuBaoHanh[] all = TrangThaiPhieuBaoHanh.values();
        String[] lb = new String[all.length]; double[] v = new double[all.length];
        for (int i = 0; i < all.length; i++) { lb[i] = all[i].toString(); v[i] = theoTt.getOrDefault(all[i], 0); }
        pieBh.setData(lb, v);
    }

    // ---------- 8. Thu - chi ----------
    private void loadThuChi() {
        if (cbYear.getSelectedItem() == null) return;
        int year = (Integer) cbYear.getSelectedItem();
        double[] thu = new double[12], chi = new double[12];
        for (HoaDon h : hoaDons)
            if (h.daThanhToan() && h.getNgayLap().getYear() == year) thu[h.getNgayLap().getMonthValue() - 1] += h.thanhTienDaLap();
        for (PhieuNhapKho p : phieuNhaps)
            if (p.getTrangThai() == TrangThaiPhieuNhapKho.DA_XAC_NHAN && p.getNgayNhap().getYear() == year)
                chi[p.getNgayNhap().getMonthValue() - 1] += p.tinhTongTien();
        tcModel.setRowCount(0);
        String[] lb = new String[12]; double[] vt = new double[12], vc = new double[12]; double st = 0, sc = 0;
        for (int m = 0; m < 12; m++) {
            tcModel.addRow(new Object[]{"Tháng " + (m + 1), thu[m], chi[m], thu[m] - chi[m]});
            lb[m] = "T" + (m + 1); vt[m] = thu[m] / 1_000_000.0; vc[m] = chi[m] / 1_000_000.0; st += thu[m]; sc += chi[m];
        }
        barThu.setData(lb, vt, "triệu đồng"); barChi.setData(lb, vc, "triệu đồng");
        lbThuChi.setText("Năm " + year + ": thu " + Fmt.money(st) + "  |  chi nhập kho " + Fmt.money(sc) + "  |  chênh lệch " + Fmt.money(st - sc));
    }

    // ---------- 9. Cảnh báo tồn kho ----------
    private void loadTonKho() {
        LocalDateTime tu = LocalDateTime.now().minusDays(30);
        Map<String, Integer> ban30 = new HashMap<>();
        for (HoaDon h : hoaDons) {
            if (!h.daThanhToan() || h.getNgayLap().isBefore(tu)) continue;
            for (ChiTietHoaDon c : h.getChiTietList()) ban30.merge(c.getMaSanPham().getMaSanPham(), c.getSoLuong(), Integer::sum);
        }
        List<Object[]> rows = new ArrayList<>();
        for (SanPham s : new SanPhamDAO().getAll()) {
            if (s.getTrangThai() == TrangThaiSanPham.VO_HIEU_HOA) continue;
            int b = ban30.getOrDefault(s.getMaSanPham(), 0);
            String canhBao = s.getSoLuongTon() == 0 ? "Hết hàng" : s.sapHetHang(10) ? "Sắp hết hàng (nên nhập thêm)" : b == 0 ? "Bán chậm (30 ngày chưa bán)" : null;
            if (canhBao != null) rows.add(new Object[]{s.getMaSanPham(), s.getTenSanPham(), s.getSoLuongTon(), b, canhBao});
        }
        rows.sort((x, y) -> (Integer) x[2] - (Integer) y[2]);
        tkModel.setRowCount(0);
        rows.forEach(tkModel::addRow);
        duaLenCot(barTk, rows, 0, 2, 1, "Tồn kho", 8);
    }
}
