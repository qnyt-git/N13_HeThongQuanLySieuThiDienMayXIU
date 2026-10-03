package ENTITY;

public class ChiTieuPhieuDatHang {

	private String maChiTietPDH;
	private PhieuDatHang maPhieuDatHang;
	private SanPham maSanPham;
	private int soLuong;
	private double donGia;
	public ChiTieuPhieuDatHang(String maChiTietPDH, PhieuDatHang maPhieuDatHang, SanPham maSanPham, int soLuong,
			double donGia) {
		super();
		this.maChiTietPDH = maChiTietPDH;
		this.maPhieuDatHang = maPhieuDatHang;
		this.maSanPham = maSanPham;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}
	public String getMaChiTietPDH() {
		return maChiTietPDH;
	}
	public void setMaChiTietPDH(String maChiTietPDH) {
		this.maChiTietPDH = maChiTietPDH;
	}
	public PhieuDatHang getMaPhieuDatHang() {
		return maPhieuDatHang;
	}
	public void setMaPhieuDatHang(PhieuDatHang maPhieuDatHang) {
		this.maPhieuDatHang = maPhieuDatHang;
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
		return "ChiTieuPhieuDatHang [maChiTietPDH=" + maChiTietPDH + ", maPhieuDatHang=" + maPhieuDatHang
				+ ", maSanPham=" + maSanPham + ", soLuong=" + soLuong + ", donGia=" + donGia + "]";
	}
	
}
