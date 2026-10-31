package ENTITY;

import java.time.LocalDate;
import java.util.Objects;

public class NhanVien {
    private String maNhanVien;
    private String hoTenNhanVien;
    private String chucVu;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private float heSoLuong;
    private int luongCoBan;
    private boolean trangThai;

    public NhanVien() {}

    public NhanVien(String maNhanVien, String hoTenNhanVien, String chucVu, String soDienThoai,
                    LocalDate ngaySinh, float heSoLuong, int luongCoBan, boolean trangThai) {
        this.maNhanVien = maNhanVien;
        this.hoTenNhanVien = hoTenNhanVien;
        this.chucVu = chucVu;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.heSoLuong = heSoLuong;
        this.luongCoBan = luongCoBan;
        this.trangThai = trangThai;
    }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getHoTenNhanVien() { return hoTenNhanVien; }
    public void setHoTenNhanVien(String hoTenNhanVien) { this.hoTenNhanVien = hoTenNhanVien; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public float getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(float heSoLuong) { this.heSoLuong = heSoLuong; }
    public int getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(int luongCoBan) { this.luongCoBan = luongCoBan; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    // ===== METHOD BỔ SUNG =====
    /** Tính lương */
    public double tinhLuong() {
        return heSoLuong * luongCoBan;
    }

    /** Kiểm tra còn làm việc */
    public boolean conLamViec() { return trangThai; }

    /** Nghỉ việc */
    public void nghiViec() { this.trangThai = false; }

    /** Cập nhật thông tin cá nhân (placeholder) */
    public void capNhatThongTin(String hoTen, String sdt, LocalDate ngaySinh) {
        if (hoTen != null && !hoTen.isEmpty()) this.hoTenNhanVien = hoTen;
        if (sdt != null && !sdt.isEmpty()) this.soDienThoai = sdt;
        if (ngaySinh != null) this.ngaySinh = ngaySinh;
    }

    /** Đăng ký ca (placeholder) */
    public void dangKyCaLam() {
        // sẽ được xử lý bởi PhanCa
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhanVien)) return false;
        return Objects.equals(maNhanVien, ((NhanVien) o).maNhanVien);
    }

    @Override
    public int hashCode() { return Objects.hash(maNhanVien); }

    @Override
    public String toString() {
        return hoTenNhanVien + " (" + maNhanVien + ")";
    }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    /** Tuổi tính đến hôm nay. */
    public int tuoi() {
        return ngaySinh == null ? 0 : java.time.Period.between(ngaySinh, LocalDate.now()).getYears();
    }

    /** Ngày sinh hợp lệ: không ở tương lai và đủ 18 tuổi. */
    public boolean hopLeNgaySinh() {
        return ngaySinh != null && !ngaySinh.isAfter(LocalDate.now()) && tuoi() >= 18;
    }

    public String trangThaiText() { return trangThai ? "Đang làm" : "Nghỉ việc"; }

    /** Hệ số lương dạng double (tránh sai số 2.2 -> 2.200000047 của float). */
    public double heSoLuongDouble() { return Double.parseDouble(Float.toString(heSoLuong)); }
}
