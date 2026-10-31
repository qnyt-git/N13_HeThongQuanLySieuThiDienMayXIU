package ENTITY;

import java.util.Objects;
import ENUM.*;
public class KhachHang {
    private String maKhachHang;
    private String hoTenKhachHang;
    private String soDienThoai;
    private String diaChi;
    private int diemTichLuy;
    private TrangThaiKhachHang trangThai;

    public KhachHang() {}

    public KhachHang(String maKhachHang, String hoTenKhachHang, String soDienThoai,
                     String diaChi, int diemTichLuy, TrangThaiKhachHang trangThai) {
        this.maKhachHang = maKhachHang;
        this.hoTenKhachHang = hoTenKhachHang;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
        this.trangThai = trangThai;
    }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getHoTenKhachHang() { return hoTenKhachHang; }
    public void setHoTenKhachHang(String hoTenKhachHang) { this.hoTenKhachHang = hoTenKhachHang; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }
    public TrangThaiKhachHang getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiKhachHang trangThai) { this.trangThai = trangThai; }

    // ===== METHOD BỔ SUNG =====
    public void capNhatThongTin(String hoTen, String sdt, String diaChi) {
        if (hoTen != null) this.hoTenKhachHang = hoTen;
        if (sdt != null) this.soDienThoai = sdt;
        if (diaChi != null) this.diaChi = diaChi;
    }

    public void capNhatDiemTichLuy(int diem) {
        if (this.diemTichLuy + diem < 0) this.diemTichLuy = 0;
        else this.diemTichLuy += diem;
    }

    /** Xếp hạng theo điểm tích lũy */
    public void capNhatTrangThaiTuDong() {
        if (this.diemTichLuy >= 1000) this.trangThai = TrangThaiKhachHang.TOT;
        else if (this.diemTichLuy >= 100) this.trangThai = TrangThaiKhachHang.KH_MOI;
    }

    public boolean laKhachHangTot() {
        return trangThai == TrangThaiKhachHang.TOT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KhachHang)) return false;
        return Objects.equals(maKhachHang, ((KhachHang) o).maKhachHang);
    }

    @Override
    public int hashCode() { return Objects.hash(maKhachHang); }

    @Override
    public String toString() {
        return hoTenKhachHang + " - " + soDienThoai;
    }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    /** Cộng điểm tích lũy theo hóa đơn: 100.000đ = 1 điểm, rồi cập nhật hạng. */
    public void congDiemTuHoaDon(double thanhTien) {
        capNhatDiemTichLuy((int) (thanhTien / 100_000));
        capNhatTrangThaiTuDong();
    }

    /** Thu hồi điểm đã cộng khi hủy hóa đơn. */
    public void thuHoiDiemTuHoaDon(double thanhTien) {
        capNhatDiemTichLuy(-(int) (thanhTien / 100_000));
    }
}
