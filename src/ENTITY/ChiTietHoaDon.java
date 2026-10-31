package ENTITY;

import java.util.Objects;

public class ChiTietHoaDon {
    private String maChiTietHD;
    private HoaDon maHoaDon;
    private SanPham maSanPham;
    private int soLuong;
    private double donGia;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(String maChiTietHD, HoaDon maHoaDon, SanPham maSanPham,
                         int soLuong, double donGia) {
        this.maChiTietHD = maChiTietHD;
        this.maHoaDon = maHoaDon;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaChiTietHD() { return maChiTietHD; }
    public void setMaChiTietHD(String maChiTietHD) { this.maChiTietHD = maChiTietHD; }
    public HoaDon getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(HoaDon maHoaDon) { this.maHoaDon = maHoaDon; }
    public SanPham getMaSanPham() { return maSanPham; }
    public void setMaSanPham(SanPham maSanPham) { this.maSanPham = maSanPham; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    // ===== METHOD BỔ SUNG =====
    public double thanhTien() {
        return soLuong * donGia;
    }

    /** Kiểm tra hợp lệ */
    public boolean hopLe() {
        return maSanPham != null && soLuong > 0 && donGia > 0
            && maSanPham.getSoLuongTon() >= soLuong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietHoaDon)) return false;
        return Objects.equals(maChiTietHD, ((ChiTietHoaDon) o).maChiTietHD);
    }

    @Override
    public int hashCode() { return Objects.hash(maChiTietHD); }
}