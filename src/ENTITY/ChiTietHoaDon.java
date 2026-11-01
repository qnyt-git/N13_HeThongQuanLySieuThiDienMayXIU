package ENTITY;

import java.util.Objects;

public class ChiTietHoaDon {
    private HoaDon maHoaDon;
    private SanPham maSanPham;
    private int soLuong;
    private double donGia;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(HoaDon maHoaDon, SanPham maSanPham,
                         int soLuong, double donGia) {
        this.maHoaDon = maHoaDon;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

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
	public String toString() {
		return "ChiTietHoaDon [maHoaDon=" + maHoaDon + ", maSanPham=" + maSanPham + ", soLuong=" + soLuong + ", donGia="
				+ donGia + "]";
	}

    
}
