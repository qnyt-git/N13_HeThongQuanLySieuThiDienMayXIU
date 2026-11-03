package ENTITY;

import java.util.Objects;
import ENUM.*;
public class TaiKhoanNhanVien {
    private String maTaiKhoan;
    private String tenDangNhap;
    private NhanVien maNhanVien;
    private String matKhau;
    private LoaiQuyen loaiQuyen; // 5quyen: ql, bh, tn, kho, cskh
    private boolean trangThai; //hoạt động - vô hiệu hóa

    public TaiKhoanNhanVien() {}

    public TaiKhoanNhanVien(String maTaiKhoan, String tenDangNhap, NhanVien maNhanVien,
                            String matKhau, LoaiQuyen loaiQuyen, boolean trangThai) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.maNhanVien = maNhanVien;
        this.matKhau = matKhau;
        this.loaiQuyen = loaiQuyen;
        this.trangThai = trangThai;
    }

    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public NhanVien getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(NhanVien maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public LoaiQuyen getLoaiQuyen() { return loaiQuyen; }
    public void setLoaiQuyen(LoaiQuyen loaiQuyen) { this.loaiQuyen = loaiQuyen; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    // Đăng nhập - kiểm tra tài khoản/mật khẩu 
    public boolean dangNhap(String tenDangNhap, String matKhau) {
        if (!this.trangThai) return false;
        return this.tenDangNhap.equals(tenDangNhap) && this.matKhau.equals(matKhau);
    }

    // Đổi mật khẩu 
    public boolean doiMatKhau(String matKhauCu, String matKhauMoi) {
        if (!this.matKhau.equals(matKhauCu)) return false;
        if (matKhauMoi == null || matKhauMoi.length() < 6) return false;
        this.matKhau = matKhauMoi;
        return true;
    }

    // Kích hoạt / vô hiệu hóa tài khoản 
    public void doiTrangThai() {
        this.trangThai = !this.trangThai;
    }

    // Kiểm tra quyền có phải quản lý không 
    public boolean laQuanLy() {
        return this.loaiQuyen == LoaiQuyen.QUAN_LY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaiKhoanNhanVien)) return false;
        TaiKhoanNhanVien that = (TaiKhoanNhanVien) o;
        return Objects.equals(maTaiKhoan, that.maTaiKhoan);
    }

    @Override
    public int hashCode() { return Objects.hash(maTaiKhoan); }

    @Override
    public String toString() {
        return "TaiKhoanNhanVien [maTaiKhoan=" + maTaiKhoan + ", tenDangNhap=" + tenDangNhap +
               ", maNhanVien=" + (maNhanVien != null ? maNhanVien.getMaNhanVien() : null) +
               ", loaiQuyen=" + loaiQuyen + ", trangThai=" + trangThai + "]";
    }

    

    public String trangThaiText() { return trangThai ? "Hoạt động" : "Tạm khóa"; }


    public String kiemTraDangNhap(String tenDN, String mk) {
        if (this.tenDangNhap == null || !this.tenDangNhap.equals(tenDN)) return "Tên đăng nhập không tồn tại";
        if (!this.trangThai) return "Tài khoản đang bị khóa";
        if (this.matKhau == null || !this.matKhau.equals(mk)) return "Mật khẩu không đúng";
        return null;
    }

    
    public String datLaiMatKhau(String soDienThoai, String matKhauMoi) {
        if (maNhanVien == null || !maNhanVien.getSoDienThoai().equals(soDienThoai)) return "Số điện thoại không khớp với nhân viên";
        if (matKhauMoi == null || matKhauMoi.length() < 6) return "Mật khẩu mới tối thiểu 6 ký tự";
        this.matKhau = matKhauMoi;
        return null;
    }

    public boolean coQuyen(LoaiQuyen... ds) {
        for (LoaiQuyen q : ds) if (q == loaiQuyen) return true;
        return false;
    }
}
