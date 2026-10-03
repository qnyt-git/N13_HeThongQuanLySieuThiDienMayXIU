package ENTITY;

public class ChiTietPhieuNhapKho {
	
	private String maChiTietPNK;
	private PhieuNhapKho maPhieuNhapKho;
	private SanPham maSanPham;
	private int soLuong;
	private double donGia;
	public ChiTietPhieuNhapKho(String maChiTietPNK, PhieuNhapKho maPhieuNhapKho, SanPham maSanPham, int soLuong,
			double donGia) {
		super();
		this.maChiTietPNK = maChiTietPNK;
		this.maPhieuNhapKho = maPhieuNhapKho;
		this.maSanPham = maSanPham;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}
	public String getMaChiTietPNK() {
		return maChiTietPNK;
	}
	public void setMaChiTietPNK(String maChiTietPNK) {
		this.maChiTietPNK = maChiTietPNK;
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
			//
			//
			//
			//
			//
			//
	
	
	@Override
	public String toString() {
		return "ChiTietPhieuNhapKho [maChiTietPNK=" + maChiTietPNK + ", maPhieuNhapKho=" + maPhieuNhapKho
				+ ", maSanPham=" + maSanPham + ", soLuong=" + soLuong + ", donGia=" + donGia + "]";
	}
	
}
