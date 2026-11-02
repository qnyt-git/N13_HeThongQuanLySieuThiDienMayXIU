package ENTITY;

public class ChiTietPhieuDatHang {
	private PhieuDatHang maPhieuDatHang;
	private SanPham maSanPham;
	private int soLuong;
	private double donGia;
	public ChiTietPhieuDatHang(PhieuDatHang maPhieuDatHang, SanPham maSanPham, int soLuong,
			double donGia) {
		super();
		this.maPhieuDatHang = maPhieuDatHang;
		this.maSanPham = maSanPham;
		this.soLuong = soLuong;
		this.donGia = donGia;
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
	
	
	public double thanhTien() {
		return soLuong * donGia; 
		}
	
	public void CapNhatSoLuong() {}
	
	@Override
	public String toString() {
		return "ChiTieuPhieuDatHang [maPhieuDatHang=" + maPhieuDatHang
				+ ", maSanPham=" + maSanPham + ", soLuong=" + soLuong + ", donGia=" + donGia + "]";
	}
	
}
