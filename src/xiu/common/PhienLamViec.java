package xiu.common;

import ENTITY.NhanVien;
import ENTITY.TaiKhoanNhanVien;

/** Phiên làm việc hiện tại: tài khoản đang đăng nhập. */
public final class PhienLamViec {
    private PhienLamViec() {}

    private static TaiKhoanNhanVien taiKhoan;

    public static void dangNhap(TaiKhoanNhanVien tk) { taiKhoan = tk; }
    public static void dangXuat() { taiKhoan = null; }
    public static TaiKhoanNhanVien taiKhoan() { return taiKhoan; }
    public static NhanVien nhanVien() { return taiKhoan.getMaNhanVien(); }
    public static String quyen() { return taiKhoan == null ? "" : taiKhoan.getLoaiQuyen().name(); }
}
