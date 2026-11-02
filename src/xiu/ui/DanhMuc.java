package xiu.ui;

import DAO.*;
import ENTITY.*;
import ENUM.*;
import xiu.common.*;
import xiu.ui.CrudPanel.F;
import xiu.ui.CrudPanel.Kind;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Các màn hình danh mục/cập nhật dạng CRUD, mỗi màn hình ứng với một lớp trong package ENTITY.
 * Dữ liệu đọc/ghi qua DAO; kiểm tra miền giá trị dùng các method của entity.
 */
public final class DanhMuc {
    private DanhMuc() {}

    private static final String LAM = "Đang làm", NGHI = "Nghỉ việc", HD = "Hoạt động", KHOA = "Tạm khóa", NGUNG = "Ngừng", AD = "Đang áp dụng";

    // ---------------- Nhân viên ----------------
    public static CrudPanel<NhanVien> nhanVien() {
        return new CrudPanel<>("Quản lý nhân viên", new NhanVienDAO(),
                new String[]{"Mã NV", "Họ tên", "Giới tính", "Chức vụ", "Số điện thoại", "Ngày sinh", "Trình độ học vấn", "Hệ số lương", "Lương cơ bản", "Trạng thái"},
                n -> new Object[]{n.getMaNhanVien(), n.getHoTenNhanVien(), n.isGioiTinh() ? "Nam" : "Nữ", n.getChucVu(), n.getSoDienThoai(), Fmt.date(n.getNgaySinh()),
                        n.getTrinhDoHocVan() == null ? "" : n.getTrinhDoHocVan(), n.heSoLuongDouble(), (double) n.getLuongCoBan(), n.trangThaiText()},
                List.of(F.of("Họ tên", Kind.NAME),
                        F.of("Giới tính", Kind.COMBO).opts("Nam", "Nữ"),
                        F.of("Chức vụ", Kind.COMBO).opts("Quản lý", "Bán hàng", "Thu ngân", "Thủ kho", "Chăm sóc khách hàng"),
                        F.of("Số điện thoại", Kind.PHONE),
                        F.of("Ngày sinh", Kind.DATE),
                        F.of("Trình độ học vấn", Kind.TEXT),
                        F.of("Hệ số lương", Kind.DECIMAL),
                        F.of("Lương cơ bản (đ)", Kind.MONEY),
                        F.of("Trạng thái", Kind.COMBO).opts(LAM, NGHI)),
                id -> { NhanVien n = new NhanVien(); n.setMaNhanVien(id); return n; },
                (n, v) -> {
                    n.setHoTenNhanVien((String) v[1]); n.setGioiTinh("Nam".equals(v[2])); n.setChucVu((String) v[3]); n.setSoDienThoai((String) v[4]);
                    n.setNgaySinh(Fmt.parseDate((String) v[5])); n.setTrinhDoHocVan((String) v[6]); n.setHeSoLuong(((Double) v[7]).floatValue());
                    n.setLuongCoBan(((Long) v[8]).intValue()); n.setTrangThai(LAM.equals(v[9]));
                }) {
            @Override protected String validateRow(Object[] v, NhanVien e) {
                NhanVien tmp = new NhanVien();
                tmp.setNgaySinh(Fmt.parseDate((String) v[5]));
                if (tmp.getNgaySinh().isAfter(LocalDate.now())) return "Ngày sinh không được lớn hơn ngày hiện tại";
                if (!tmp.hopLeNgaySinh()) return "Nhân viên phải đủ 18 tuổi";
                if (dup(4, v[4], e)) return "Số điện thoại đã tồn tại";
                return null;
            }
            @Override protected String checkDelete(NhanVien n) {
                for (TaiKhoanNhanVien t : new TaiKhoanNhanVienDAO().getAll())
                    if (t.getMaNhanVien().equals(n)) return "Nhân viên đã có tài khoản, hãy chuyển trạng thái sang \"" + NGHI + "\"";
                return null;
            }
        };
    }

    // ---------------- Tài khoản ----------------
    public static CrudPanel<TaiKhoanNhanVien> taiKhoan() {
        return new CrudPanel<>("Quản lý tài khoản nhân viên", new TaiKhoanNhanVienDAO(),
                new String[]{"Mã tài khoản", "Tên đăng nhập", "Mật khẩu", "Loại quyền", "Trạng thái", "Nhân viên"},
                t -> new Object[]{t.getMaTaiKhoan(), t.getTenDangNhap(), t.getMatKhau(), t.getLoaiQuyen(), t.trangThaiText(), t.getMaNhanVien()},
                List.of(F.of("Tên đăng nhập", Kind.TEXT).regex("^\\S+$", "Tên đăng nhập không được chứa khoảng trắng"),
                        F.of("Mật khẩu", Kind.PASSWORD),
                        F.of("Loại quyền", Kind.COMBO).opts((Object[]) LoaiQuyen.values()),
                        F.of("Trạng thái", Kind.COMBO).opts(HD, KHOA),
                        F.of("Nhân viên", Kind.COMBO).opts(() -> new NhanVienDAO().getAll().stream().filter(NhanVien::conLamViec).toArray())),
                id -> { TaiKhoanNhanVien t = new TaiKhoanNhanVien(); t.setMaTaiKhoan(id); return t; },
                (t, v) -> {
                    t.setTenDangNhap((String) v[1]); t.setMatKhau((String) v[2]); t.setLoaiQuyen((LoaiQuyen) v[3]);
                    t.setTrangThai(HD.equals(v[4])); t.setMaNhanVien((NhanVien) v[5]);
                }) {
            @Override protected String validateRow(Object[] v, TaiKhoanNhanVien e) {
                if (dup(1, v[1], e)) return "Tên đăng nhập đã tồn tại";
                for (TaiKhoanNhanVien t : repo)
                    if (t != e && t.getMaNhanVien().equals(v[5])) return "Mỗi nhân viên chỉ có tối đa một tài khoản";
                return null;
            }
            @Override protected String checkDelete(TaiKhoanNhanVien t) {
                return t.equals(PhienLamViec.taiKhoan()) ? "Không thể xóa tài khoản đang đăng nhập" : null;
            }
        };
    }

    // ---------------- Ca làm việc ----------------
    public static CrudPanel<CaLamViec> caLam() {
        return new CrudPanel<>("Quản lý ca làm việc", new CaLamViecDAO(),
                new String[]{"Mã ca", "Tên ca", "Giờ bắt đầu", "Giờ kết thúc"},
                c -> new Object[]{c.getMaCa(), c.getTenCa(), c.gioBatDauText(), c.gioKetThucText()},
                List.of(F.of("Tên ca", Kind.NAME), F.of("Giờ bắt đầu", Kind.TIME), F.of("Giờ kết thúc", Kind.TIME)),
                id -> { CaLamViec c = new CaLamViec(); c.setMaCa(id); return c; },
                (c, v) -> { c.setTenCa((String) v[1]); c.datGio(LocalTime.parse((String) v[2]), LocalTime.parse((String) v[3])); }) {
            @Override protected String validateRow(Object[] v, CaLamViec e) {
                CaLamViec tmp = new CaLamViec();
                tmp.datGio(LocalTime.parse((String) v[2]), LocalTime.parse((String) v[3]));
                if (!tmp.trongKhungGioChoPhep()) return "Ca làm phải nằm trong khung giờ 06:00 - 23:00";
                if (!tmp.hopLe()) return "Giờ kết thúc phải lớn hơn giờ bắt đầu";
                return null;
            }
            @Override protected String checkDelete(CaLamViec c) {
                for (PhanCa p : new PhanCaDAO().getAll()) if (p.getMaCa().equals(c)) return "Ca làm việc đã được phân cho nhân viên";
                return null;
            }
        };
    }

    // ---------------- Sản phẩm ----------------
    public static CrudPanel<SanPham> sanPham() {
        return new CrudPanel<>("Quản lý sản phẩm", new SanPhamDAO(),
                new String[]{"Mã SP", "Tên sản phẩm", "Giá bán", "Số lượng tồn", "Bảo hành (tháng)", "Trạng thái"},
                s -> new Object[]{s.getMaSanPham(), s.getTenSanPham(), s.getGiaBan(), s.getSoLuongTon(), s.baoHanhThang(), s.getTrangThai()},
                List.of(F.of("Tên sản phẩm", Kind.TEXT).regex("^[\\p{L}\\d .,\\-/()]+$", "Tên sản phẩm chứa ký tự không hợp lệ"),
                        F.of("Giá bán (đ)", Kind.MONEY),
                        F.of("Số lượng tồn", Kind.INT_NONNEG),
                        F.of("Bảo hành (tháng)", Kind.INT_NONNEG),
                        F.of("Trạng thái", Kind.COMBO).opts((Object[]) TrangThaiSanPham.values())),
                id -> { SanPham s = new SanPham(); s.setMaSanPham(id); return s; },
                (s, v) -> {
                    s.setTenSanPham((String) v[1]); s.setGiaBan((Long) v[2]);
                    s.setSoLuongTon((Integer) v[3]); s.datBaoHanhThang((Integer) v[4]); s.setTrangThai((TrangThaiSanPham) v[5]);
                }) {
            @Override protected String validateRow(Object[] v, SanPham e) {
                return dup(2, v[2], e) ? "Mã vạch đã tồn tại" : null;
            }
            @Override protected String checkDelete(SanPham s) {
                for (HoaDon h : new HoaDonDAO().getAll())
                    for (ChiTietHoaDon c : h.getChiTietList())
                        if (c.getMaSanPham().equals(s)) return "Sản phẩm đã phát sinh hóa đơn, hãy chuyển sang \"Ngừng bán\"";
                return null;
            }
        };
    }

    // ---------------- Khách hàng ----------------
    public static CrudPanel<KhachHang> khachHang() {
        return new CrudPanel<>("Quản lý khách hàng", new KhachHangDAO(),
                new String[]{"Mã KH", "Họ tên", "Số điện thoại", "Địa chỉ", "Điểm tích lũy", "Loại khách hàng"},
                k -> new Object[]{k.getMaKhachHang(), k.getHoTenKhachHang(), k.getSoDienThoai(), k.getDiaChi(), k.getDiemTichLuy(), k.getLoaiKH()},
                List.of(F.of("Họ tên", Kind.NAME),
                        F.of("Số điện thoại", Kind.PHONE),
                        F.of("Địa chỉ", Kind.TEXT),
                        F.of("Điểm tích lũy", Kind.INT_NONNEG).def(() -> "0"),
                        F.of("Loại khách hàng", Kind.COMBO).opts((Object[]) LoaiKhachHang.values())),
                id -> { KhachHang k = new KhachHang(); k.setMaKhachHang(id); return k; },
                (k, v) -> {
                    k.setHoTenKhachHang((String) v[1]); k.setSoDienThoai((String) v[2]);
                    k.setDiaChi((String) v[3]);
                    k.setDiemTichLuy((Integer) v[4]); k.setLoaiKH((LoaiKhachHang) v[5]);
                }) {
            @Override protected String validateRow(Object[] v, KhachHang e) {
                return dup(2, v[2], e) ? "Số điện thoại đã tồn tại" : null;
            }
            @Override protected String checkDelete(KhachHang k) {
                for (HoaDon h : new HoaDonDAO().getAll()) if (h.getMaKhachHang().equals(k)) return "Khách hàng đã có hóa đơn, không thể xóa";
                return null;
            }
        };
    }

    // ---------------- Nhà cung cấp ----------------
    public static CrudPanel<NhaCungCap> nhaCungCap() {
        return new CrudPanel<>("Quản lý nhà cung cấp", new NhaCungCapDAO(),
                new String[]{"Mã NCC", "Tên nhà cung cấp", "Số điện thoại", "Địa chỉ", "Email", "Trạng thái"},
                n -> new Object[]{n.getMaNCC(), n.getTenNCC(), n.getSoDienThoai(), n.getDiaChi(), n.getEmail(), n.trangThaiText()},
                List.of(F.of("Tên nhà cung cấp", Kind.TEXT).regex("^[\\p{L} ]+$", "Tên nhà cung cấp chỉ chứa chữ cái và khoảng trắng"),
                        F.of("Số điện thoại", Kind.TEXT).regex("^0\\d{9,10}$", "Số điện thoại gồm 10-11 chữ số, bắt đầu bằng 0"),
                        F.of("Địa chỉ", Kind.TEXT),
                        F.of("Email", Kind.EMAIL),
                        F.of("Trạng thái", Kind.COMBO).opts(HD, NGUNG)),
                id -> { NhaCungCap n = new NhaCungCap(); n.setMaNCC(id); return n; },
                (n, v) -> {
                    n.setTenNCC((String) v[1]); n.setSoDienThoai((String) v[2]); n.setDiaChi((String) v[3]);
                    n.setEmail((String) v[4]); n.setTrangThai(HD.equals(v[5]));
                }) {
            @Override protected String validateRow(Object[] v, NhaCungCap e) {
                return dup(2, v[2], e) ? "Số điện thoại đã tồn tại" : null;
            }
            @Override protected String checkDelete(NhaCungCap n) {
                for (PhieuNhapKho p : new PhieuNhapKhoDAO().getAll()) if (p.getMaNCC().equals(n)) return "Nhà cung cấp đã có phiếu nhập kho";
                return null;
            }
        };
    }

    // ---------------- Khuyến mãi ----------------
    public static CrudPanel<KhuyenMai> khuyenMai() {
        return new CrudPanel<>("Chương trình khuyến mãi", new KhuyenMaiDAO(),
                new String[]{"Mã KM", "Tên khuyến mãi", "Giá trị giảm (%)", "Ngày bắt đầu", "Ngày kết thúc", "Ghi chú", "Trạng thái"},
                k -> new Object[]{k.getMaKhuyenMai(), k.getTenKhuyenMai(), (int) k.getGiaTriGiam(), Fmt.date(k.getThoiGianBatDau()),
                        Fmt.date(k.getThoiGianKetThuc()), k.trangThaiText()},
                List.of(F.of("Tên khuyến mãi", Kind.NAME),
                        F.of("Giá trị giảm (%)", Kind.INT_POS),
                        F.of("Ngày bắt đầu", Kind.DATE).def(() -> Fmt.date(LocalDate.now())),
                        F.of("Ngày kết thúc", Kind.DATE),
                        F.of("Ghi chú", Kind.AREA).optional(),
                        F.of("Trạng thái", Kind.COMBO).opts(AD, NGUNG)),
                id -> { KhuyenMai k = new KhuyenMai(); k.setMaKhuyenMai(id); return k; },
                (k, v) -> {
                    k.setTenKhuyenMai((String) v[1]); k.setGiaTriGiam((Integer) v[2]);
                    k.datThoiGian(Fmt.parseDate((String) v[3]), Fmt.parseDate((String) v[4]));
                    k.setTrangThai(AD.equals(v[5]));
                }) {
            @Override protected String validateRow(Object[] v, KhuyenMai e) {
                if ((Integer) v[2] > 100) return "Giá trị giảm không được vượt quá 100%";
                KhuyenMai tmp = new KhuyenMai();
                tmp.setGiaTriGiam((Integer) v[2]);
                tmp.datThoiGian(Fmt.parseDate((String) v[3]), Fmt.parseDate((String) v[4]));
                return tmp.hopLe() ? null : "Ngày kết thúc không được trước ngày bắt đầu";
            }
            @Override protected String checkDelete(KhuyenMai k) {
                for (HoaDon h : new HoaDonDAO().getAll()) if (k.equals(h.getMaKhuyenMai())) return "Khuyến mãi đã được áp dụng cho hóa đơn";
                return null;
            }
        };
    }

    // ---------------- Phân ca ----------------
    public static CrudPanel<PhanCa> phanCa() {
        return new CrudPanel<>("Phân ca làm việc", new PhanCaDAO(),
                new String[]{"Mã phân ca", "Nhân viên", "Ca làm việc", "Ngày làm", "Trạng thái"},
                p -> new Object[]{p.getMaPhanCa(), p.getMaNhanVien(), p.getMaCa(), Fmt.date(p.getNgayLam()), p.getTrangThai()},
                List.of(F.of("Nhân viên", Kind.COMBO).opts(() -> new NhanVienDAO().getAll().stream().filter(NhanVien::conLamViec).toArray()),
                        F.of("Ca làm việc", Kind.COMBO).opts(() -> new CaLamViecDAO().getAll().toArray()),
                        F.of("Ngày làm", Kind.DATE).def(() -> Fmt.date(LocalDate.now())),
                        F.of("Trạng thái", Kind.COMBO).opts((Object[]) TrangThaiPhanCa.values())),
                id -> { PhanCa p = new PhanCa(); p.setMaPhanCa(id); return p; },
                (p, v) -> {
                    p.setMaNhanVien((NhanVien) v[1]); p.setMaCa((CaLamViec) v[2]);
                    p.setNgayLam(Fmt.parseDate((String) v[3])); p.setTrangThai((TrangThaiPhanCa) v[4]);
                }) {
            @Override protected String validateRow(Object[] v, PhanCa e) {
                PhanCa moi = new PhanCa("", (NhanVien) v[1], (CaLamViec) v[2], Fmt.parseDate((String) v[3]), (TrangThaiPhanCa) v[4]);
                for (PhanCa p : repo)
                    if (p != e && p.trungVoi(moi)) return "Nhân viên đã được phân ca này trong ngày (trùng ca)";
                return null;
            }
        };
    }
}
