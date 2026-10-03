package ENTITY;

public class ChiTietHoaDon {

	private String maChiTietHD;
	private HoaDon maHoaDon;
	private SanPham maSanPham;
	private int soLuong;
	private double donGia;
	public ChiTietHoaDon(String maChiTietHD, HoaDon maHoaDon, SanPham maSanPham, int soLuong, double donGia) {
		super();
		this.maChiTietHD = maChiTietHD;
		this.maHoaDon = maHoaDon;
		this.maSanPham = maSanPham;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}
	public String getMaChiTietHD() {
		return maChiTietHD;
	}
	public void setMaChiTietHD(String maChiTietHD) {
		this.maChiTietHD = maChiTietHD;
	}
	public HoaDon getMaHoaDon() {
		return maHoaDon;
	}
	public void setMaHoaDon(HoaDon maHoaDon) {
		this.maHoaDon = maHoaDon;
	}
	public SanPham getMaSanPham() {
		return maSanPham;
	}
	public void setMaSanPham(SanPham maSanPham) {
		this.maSanPham = maSanPham;
	}
	public int getSoLuong() {
		return soLuong;
	}
	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
	public double getDonGia() {
		return donGia;
	}
	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}
	
	
	//thiếu method
			//
			//
			//
			//
			//
			//
	
	
	@Override
	public String toString() {
		return "ChiTietHoaDon [maChiTietHD=" + maChiTietHD + ", maHoaDon=" + maHoaDon + ", maSanPham=" + maSanPham
				+ ", soLuong=" + soLuong + ", donGia=" + donGia + "]";
	}
	
}
