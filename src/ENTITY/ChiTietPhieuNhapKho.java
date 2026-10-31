package ENTITY;

public class ChiTietPhieuNhapKho {
	
	private PhieuNhapKho maPhieuNhapKho;
	private SanPham maSanPham;
	private int soLuong;
	private double donGia;
	public ChiTietPhieuNhapKho( PhieuNhapKho maPhieuNhapKho, SanPham maSanPham, int soLuong,
			double donGia) {
		super();
		this.maPhieuNhapKho = maPhieuNhapKho;
		this.maSanPham = maSanPham;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}
	
	public PhieuNhapKho getMaPhieuNhapKho() {
		return maPhieuNhapKho;
	}
	public void setMaPhieuNhapKho(PhieuNhapKho maPhieuNhapKho) {
		this.maPhieuNhapKho = maPhieuNhapKho;
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
	public void CapNhatSoLuong () {} // thiếu code 
	
	public double thanhTien() { 
		return soLuong * donGia; 
		}
	public boolean hopLe() { 
		return maSanPham != null && soLuong > 0 && donGia > 0; 
		}
	
	@Override
	public String toString() {
		return "ChiTietPhieuNhapKho [maPhieuNhapKho=" + maPhieuNhapKho
				+ ", maSanPham=" + maSanPham + ", soLuong=" + soLuong + ", donGia=" + donGia + "]";
	}
	
}
