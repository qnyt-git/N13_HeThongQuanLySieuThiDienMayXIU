package xiu.common;

import ENTITY.*;

import javax.swing.table.DefaultTableModel;

import ENUM.*;

import java.time.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Kho dữ liệu trong bộ nhớ dùng chung cho các màn hình, làm việc trực tiếp với các lớp trong package ENTITY.
 * Khi nối SQL Server, thay phần nạp dữ liệu mẫu bên dưới bằng DAO và giữ nguyên các danh sách này làm "cache".
 */
public final class Store {
    private Store() {}

    public static final List<NhanVien> nhanVien = new ArrayList<>();
    public static final List<TaiKhoanNhanVien> taiKhoan = new ArrayList<>();
    public static final List<CaLamViec> caLam = new ArrayList<>();
    public static final List<PhanCa> phanCa = new ArrayList<>();
    public static final List<SanPham> sanPham = new ArrayList<>();
    public static final List<KhachHang> khachHang = new ArrayList<>();
    public static final List<NhaCungCap> nhaCungCap = new ArrayList<>();
    public static final List<KhuyenMai> khuyenMai = new ArrayList<>();
    public static final List<HoaDon> hoaDon = new ArrayList<>();
    public static final List<PhieuDatHang> phieuDat = new ArrayList<>();
    public static final List<PhieuNhapKho> phieuNhap = new ArrayList<>();
    public static final List<PhieuBaoHanh> phieuBH = new ArrayList<>();

    // ---------------- Phiên đăng nhập ----------------
    public static TaiKhoanNhanVien current;

    /** Trả về thông báo lỗi, hoặc null nếu đăng nhập thành công. */
    public static String login(String u, String p) {
        for (TaiKhoanNhanVien t : taiKhoan) {
            if (!t.getTenDangNhap().equals(u)) continue;
            if (!t.isTrangThai()) return "Tài khoản đang bị khóa";
            if (!t.dangNhap(u, p)) return "Mật khẩu không đúng";
            current = t;
            return null;
        }
        return "Tên đăng nhập không tồn tại";
    }

    public static void logout() { current = null; }
    public static NhanVien staff() { return current.getMaNhanVien(); }
    public static String role() { return current == null ? "" : current.getLoaiQuyen().name(); }

    // ---------------- Tiện ích ----------------
    /** Model bảng chỉ đọc; kiểu cột lấy theo giá trị đầu tiên để sắp xếp số đúng. */
    public static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                for (int r = 0; r < getRowCount(); r++) { Object v = getValueAt(r, c); if (v != null) return v.getClass(); }
                return Object.class;
            }
        };
    }

    /** Sinh mã kế tiếp theo tiền tố, ví dụ NV008 -> NV009. */
    public static String nextId(String prefix, Stream<String> codes) {
        int max = codes.mapToInt(c -> {
            try { return Integer.parseInt(c.substring(prefix.length())); } catch (Exception e) { return 0; }
        }).max().orElse(0);
        return String.format("%s%03d", prefix, max + 1);
    }

    private static int ctSeq = 0;
    /** Mã dòng chi tiết (CTHD0001...). */
    public static String nextCt(String prefix) { return String.format("%s%04d", prefix, ++ctSeq); }

    public static <T> T find(List<T> l, Predicate<T> p) {
        for (T t : l) if (p.test(t)) return t;
        return null;
    }

    /** "24 tháng" -> 24. */
    public static int baoHanhThang(SanPham sp) {
        String s = sp.getThoiHanBaoHanh() == null ? "" : sp.getThoiHanBaoHanh().replaceAll("\\D", "");
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    /** Tiền giảm của hóa đơn đã lập (theo % của khuyến mãi áp dụng lúc lập, không phụ thuộc hạn hiện tại). */
    public static double tienGiam(HoaDon hd) {
        return hd.getMaKhuyenMai() == null ? 0 : hd.tinhTongTien() * hd.getMaKhuyenMai().getGiaTriGiam() / 100.0;
    }

    public static double thanhTien(HoaDon hd) { return hd.tinhTongTien() - tienGiam(hd); }

    public static HoaDon hoaDonCuaPhieu(PhieuDatHang p) {
        return find(hoaDon, h -> h.getMaPhieuDatHang() != null && h.getMaPhieuDatHang().equals(p) && h.getTrangThai() != TrangThaiHoaDon.DA_HUY);
    }

    /** Nhãn trạng thái phiếu đặt hàng (entity chỉ có boolean nên bổ sung "Đã giao" khi đã lập hóa đơn). */
    public static String trangThaiPhieuDat(PhieuDatHang p) {
        if (hoaDonCuaPhieu(p) != null) return "Đã giao";
        return p.isTrangThai() ? "Đã xác nhận" : "Chờ xử lý";
    }

    public static boolean isToday(LocalDateTime d) { return d.toLocalDate().equals(LocalDate.now()); }

    // ---------------- Dữ liệu mẫu ----------------
    private static NhanVien nv(String ma) { return find(nhanVien, n -> n.getMaNhanVien().equals(ma)); }
    private static KhachHang kh(String ma) { return find(khachHang, n -> n.getMaKhachHang().equals(ma)); }
    private static SanPham sp(String ma) { return find(sanPham, n -> n.getMaSanPham().equals(ma)); }
    private static CaLamViec ca(String ma) { return find(caLam, n -> n.getMaCa().equals(ma)); }
    public static KhuyenMai km(String ma) { return find(khuyenMai, n -> n.getMaKhuyenMai().equals(ma)); }

    static {
        LocalDate today = LocalDate.now();
        Object[][] nvs = {
            {"NV001", "Nguyễn Văn An", "Quản lý", "0901234567", LocalDate.of(1990, 3, 15), 3.0f, 12000000, true},
            {"NV002", "Trần Thị Bình", "Bán hàng", "0912345678", LocalDate.of(1995, 7, 22), 2.0f, 9000000, true},
            {"NV003", "Lê Minh Châu", "Bán hàng", "0923456789", LocalDate.of(1997, 11, 5), 2.0f, 9000000, true},
            {"NV004", "Phạm Quốc Dũng", "Thủ kho", "0934567890", LocalDate.of(1992, 1, 30), 2.2f, 9500000, true},
            {"NV005", "Võ Thị Hà", "Chăm sóc khách hàng", "0945678901", LocalDate.of(1996, 9, 12), 2.4f, 10000000, true},
            {"NV006", "Đặng Hoàng Long", "Bán hàng", "0956789012", LocalDate.of(1994, 4, 18), 2.2f, 9500000, true},
            {"NV007", "Bùi Thanh Mai", "Thu ngân", "0967890123", LocalDate.of(1998, 12, 9), 2.0f, 9000000, true},
            {"NV008", "Ngô Gia Huy", "Thủ kho", "0978901234", LocalDate.of(1993, 6, 27), 2.0f, 9000000, false}};
        for (Object[] a : nvs)
            nhanVien.add(new NhanVien((String) a[0], (String) a[1], (String) a[2], (String) a[3], (LocalDate) a[4], (Float) a[5], (Integer) a[6], (Boolean) a[7]));

        Object[][] tks = {
            {"TK001", "admin", "NV001", LoaiQuyen.QUAN_LY, true}, {"TK002", "banhang01", "NV002", LoaiQuyen.BAN_HANG, true},
            {"TK003", "banhang02", "NV003", LoaiQuyen.BAN_HANG, true}, {"TK004", "kho01", "NV004", LoaiQuyen.KHO, true},
            {"TK005", "cskh01", "NV005", LoaiQuyen.CSKH, true}, {"TK006", "thungan01", "NV007", LoaiQuyen.BAN_HANG, false}};
        for (Object[] a : tks)
            taiKhoan.add(new TaiKhoanNhanVien((String) a[0], (String) a[1], nv((String) a[2]), "123456", (LoaiQuyen) a[3], (Boolean) a[4]));

        LocalDate d0 = LocalDate.now();
        caLam.add(new CaLamViec("CA001", "Ca sáng", null, d0.atTime(8, 0), d0.atTime(12, 0)));
        caLam.add(new CaLamViec("CA002", "Ca chiều", null, d0.atTime(13, 0), d0.atTime(17, 0)));
        caLam.add(new CaLamViec("CA003", "Ca tối", null, d0.atTime(17, 0), d0.atTime(22, 0)));

        Object[][] sps = {
            {"SP001", "Tivi Samsung 55 inch 4K", "8934001000011", 12900000.0, 15, 24},
            {"SP002", "Tivi LG OLED 65 inch", "8934001000028", 32900000.0, 6, 24},
            {"SP003", "Tủ lạnh Panasonic 322 lít", "8934001000035", 11500000.0, 10, 24},
            {"SP004", "Tủ lạnh Samsung Inverter 380 lít", "8934001000042", 14900000.0, 8, 24},
            {"SP005", "Máy giặt LG Inverter 9kg", "8934001000059", 8900000.0, 12, 24},
            {"SP006", "Máy giặt Electrolux 10kg", "8934001000066", 10500000.0, 7, 24},
            {"SP007", "Điều hòa Daikin 1 HP", "8934001000073", 9700000.0, 20, 12},
            {"SP008", "Điều hòa Panasonic 1.5 HP", "8934001000080", 12300000.0, 14, 12},
            {"SP009", "Nồi cơm điện Sharp 1.8 lít", "8934001000097", 1250000.0, 40, 12},
            {"SP010", "Máy lọc không khí Xiaomi", "8934001000103", 2890000.0, 18, 12},
            {"SP011", "Lò vi sóng Toshiba 20 lít", "8934001000110", 1850000.0, 25, 12},
            {"SP012", "Quạt điều hòa Kangaroo", "8934001000127", 2350000.0, 0, 12}};
        for (Object[] a : sps) {
            int ton = (Integer) a[4];
            sanPham.add(new SanPham((String) a[0], (String) a[1], (String) a[2], (Double) a[3], ton, a[5] + " tháng",
                    ton == 0 ? TrangThaiSanPham.HET_HANG : TrangThaiSanPham.CON_BAN));
        }

        Object[][] khs = {
            {"KH001", "Nguyễn Thị Lan", "0881112221", "12 Lê Lợi, Q.1, TP.HCM", 120},
            {"KH002", "Trần Văn Hùng", "0881112222", "45 Nguyễn Huệ, Q.1, TP.HCM", 80},
            {"KH003", "Lê Thị Mỹ", "0881112223", "88 Cách Mạng Tháng 8, Q.3, TP.HCM", 35},
            {"KH004", "Phạm Đức Thành", "0881112224", "7 Phan Văn Trị, Gò Vấp, TP.HCM", 1210},
            {"KH005", "Hoàng Thị Thu", "0881112225", "21 Quang Trung, Gò Vấp, TP.HCM", 0},
            {"KH006", "Vũ Minh Khang", "0881112226", "103 Lê Văn Việt, TP. Thủ Đức", 15},
            {"KH007", "Đỗ Ngọc Anh", "0881112227", "56 Tân Sơn Nhì, Tân Phú, TP.HCM", 60},
            {"KH008", "Bùi Quang Vinh", "0881112228", "9 Nguyễn Thái Sơn, Gò Vấp, TP.HCM", 5}};
        for (Object[] a : khs) {
            int d = (Integer) a[4];
            khachHang.add(new KhachHang((String) a[0], (String) a[1], (String) a[2], (String) a[3], d, d >= 1000 ? TrangThaiKhachHang.TOT : TrangThaiKhachHang.KH_MOI));
        }

        nhaCungCap.add(new NhaCungCap("NCC001", "Samsung Vina", "0281234001", "Quận 7, TP.HCM", "contact@samsungvina.vn", true));
        nhaCungCap.add(new NhaCungCap("NCC002", "Panasonic Vietnam", "0281234002", "Thủ Đức, TP.HCM", "sales@panasonic.vn", true));
        nhaCungCap.add(new NhaCungCap("NCC003", "LG Electronics Vietnam", "0281234003", "Quận 1, TP.HCM", "b2b@lg.vn", true));
        nhaCungCap.add(new NhaCungCap("NCC004", "Daikin Vietnam", "0281234004", "Biên Hòa, Đồng Nai", "info@daikin.vn", true));
        nhaCungCap.add(new NhaCungCap("NCC005", "Điện máy Hòa Phát", "0281234005", "Bình Tân, TP.HCM", "hp@hoaphat.vn", false));

        khuyenMai.add(new KhuyenMai("KM001", "Khai trương mùa thu", today.minusMonths(1).atStartOfDay(), today.plusMonths(2).atTime(23, 59), 10, true, "Áp dụng toàn bộ sản phẩm"));
        khuyenMai.add(new KhuyenMai("KM002", "Giảm giá tivi", today.minusDays(10).atStartOfDay(), today.plusDays(20).atTime(23, 59), 7, true, "Hóa đơn có tivi"));
        khuyenMai.add(new KhuyenMai("KM003", "Khách hàng thân thiết", today.minusMonths(6).atStartOfDay(), today.plusMonths(6).atTime(23, 59), 5, true, ""));
        khuyenMai.add(new KhuyenMai("KM004", "Tết đoàn viên", today.plusMonths(3).atStartOfDay(), today.plusMonths(4).atTime(23, 59), 15, false, "Chưa bắt đầu"));

        // Phân ca tuần hiện tại
        LocalDate mon = today.with(DayOfWeek.MONDAY);
        String[] staff = {"NV002", "NV003", "NV004", "NV005", "NV006", "NV007"};
        int k = 1;
        for (int d = 0; d < 7; d++)
            for (int c = 0; c < 3; c++) {
                LocalDate day = mon.plusDays(d);
                phanCa.add(new PhanCa(String.format("PC%03d", k++), nv(staff[(d + c * 2) % staff.length]), caLam.get(c), day,
                        day.isBefore(today) ? TrangThaiPhanCa.DA_LAM : TrangThaiPhanCa.DA_PHAN));
            }

        // Hóa đơn mẫu trong năm hiện tại
        Random rnd = new Random(13);
        String[] sellers = {"NV002", "NV003", "NV006", "NV007"};
        PhuongThucThanhToan[] pt = PhuongThucThanhToan.values();
        List<LocalDateTime> times = new ArrayList<>();
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.of(today.getYear(), 1, 1), today);
        for (int i = 0; i < 48; i++) {
            LocalDate d = i < 5 ? today : LocalDate.of(today.getYear(), 1, 1).plusDays(rnd.nextInt(Math.max(1, days + 1)));
            times.add(LocalDateTime.of(d, LocalTime.of(8 + rnd.nextInt(9), rnd.nextInt(60))));
        }
        Collections.sort(times);
        for (LocalDateTime t : times) {
            boolean huy = rnd.nextInt(14) == 0;
            KhachHang kh = khachHang.get(rnd.nextInt(khachHang.size()));
            HoaDon hd = new HoaDon(nextId("HD", hoaDon.stream().map(HoaDon::getMaHoaDon)), null, kh, nv(sellers[rnd.nextInt(sellers.length)]),
                    rnd.nextInt(4) == 0 ? km("KM003") : null, pt[rnd.nextInt(pt.length)], t,
                    huy ? TrangThaiHoaDon.DA_HUY : TrangThaiHoaDon.DA_THANH_TOAN, huy ? "Khách đổi ý" : "", "");
            int items = 1 + rnd.nextInt(3);
            Set<Integer> used = new HashSet<>();
            for (int j = 0; j < items; j++) {
                int s = rnd.nextInt(11);
                if (!used.add(s)) continue;
                SanPham p = sanPham.get(s);
                hd.themChiTiet(new ChiTietHoaDon(nextCt("CTHD"), hd, p, 1 + rnd.nextInt(2), p.getGiaBan()));
            }
            hoaDon.add(hd);
        }

        // Phiếu đặt hàng
        phieuDat.add(mkPhieuDat("PDH001", today.minusDays(2), "KH002", "NV002", false, "Giao buổi chiều", "SP001", "SP002"));
        phieuDat.add(mkPhieuDat("PDH002", today.minusDays(1), "KH004", "NV003", true, "", "SP005", "SP007"));
        PhieuDatHang p3 = mkPhieuDat("PDH003", today.minusDays(5), "KH001", "NV002", true, "", "SP009");
        phieuDat.add(p3);
        HoaDon linked = new HoaDon(nextId("HD", hoaDon.stream().map(HoaDon::getMaHoaDon)), p3, kh("KH001"), nv("NV002"), null,
                PhuongThucThanhToan.TIEN_MAT, today.minusDays(4).atTime(10, 15), TrangThaiHoaDon.DA_THANH_TOAN, "", "");
        linked.themChiTiet(new ChiTietHoaDon(nextCt("CTHD"), linked, sp("SP009"), 1, sp("SP009").getGiaBan()));
        hoaDon.add(linked);

        // Phiếu nhập kho
        phieuNhap.add(mkPhieuNhap("PNK001", today.minusDays(20), "NCC001", "Nhập bổ sung tivi", TrangThaiPhieuNhapKho.DA_XAC_NHAN, "SP001", "SP002"));
        phieuNhap.add(mkPhieuNhap("PNK002", today.minusDays(7), "NCC004", "Nhập điều hòa mùa nóng", TrangThaiPhieuNhapKho.DA_XAC_NHAN, "SP007", "SP008"));
        phieuNhap.add(mkPhieuNhap("PNK003", today.minusDays(1), "NCC003", "Nhập máy giặt", TrangThaiPhieuNhapKho.CHO_DUYET, "SP005", "SP006"));

        // Phiếu bảo hành
        PhieuBaoHanh b1 = new PhieuBaoHanh("PBH001", kh("KH001"), nv("NV005"), today.minusDays(6).atTime(9, 0), today.plusDays(4).atTime(17, 0), TrangThaiPhieuBaoHanh.DANG_XU_LY, "");
        b1.themChiTiet(new ChiTietPhieuBaoHanh("CTBH0001", b1, sp("SP003"), "Không lạnh", "Kiểm tra máy nén"));
        PhieuBaoHanh b2 = new PhieuBaoHanh("PBH002", kh("KH004"), nv("NV005"), today.minusDays(15).atTime(9, 0), today.minusDays(5).atTime(17, 0), TrangThaiPhieuBaoHanh.DA_TRA, "");
        b2.themChiTiet(new ChiTietPhieuBaoHanh("CTBH0002", b2, sp("SP005"), "Rung mạnh khi vắt", "Cân chỉnh lại thùng"));
        phieuBH.add(b1); phieuBH.add(b2);
    }

    private static PhieuDatHang mkPhieuDat(String ma, LocalDate d, String khc, String nvc, boolean tt, String note, String... sps) {
        PhieuDatHang p = new PhieuDatHang(ma, kh(khc), nv(nvc), d.atTime(9, 30), tt, note);
        for (String s : sps) p.themChiTiet(new ChiTietPhieuDatHang(nextCt("CTPDH"), p, sp(s), 1, sp(s).getGiaBan()));
        return p;
    }

    private static PhieuNhapKho mkPhieuNhap(String ma, LocalDate d, String ncc, String lyDo, TrangThaiPhieuNhapKho tt, String... sps) {
        PhieuNhapKho p = new PhieuNhapKho(ma, d.atTime(14, 0), nv("NV004"), find(nhaCungCap, n -> n.getMaNCC().equals(ncc)), lyDo, tt, "");
        for (String s : sps) p.themChiTiet(new ChiTietPhieuNhapKho(nextCt("CTPNK"), p, sp(s), 5, Math.round(sp(s).getGiaBan() * 0.8)));
        return p;
    }
}
