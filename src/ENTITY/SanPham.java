package ENTITY;

public class SanPham {
	
	private String maSanPham;
	private String tenSanPham;
	private String maVach;
	private double giaBan;
	private int soLuongTon;
	private String thoiHanBaoHanh;
	private String trangThai; //con ban; con` hang`; het hang`; vo hieu hoa(khong con` ban)
	
	public SanPham(String maSanPham, String tenSanPham, String maVach, double giaBan, int soLuongTon,
			String thoiHanBaoHanh, String trangThai) {
		super();
		this.maSanPham = maSanPham;
		this.tenSanPham = tenSanPham;
		this.maVach = maVach;
		this.giaBan = giaBan;
		this.soLuongTon = soLuongTon;
		this.thoiHanBaoHanh = thoiHanBaoHanh;
		this.trangThai = trangThai;
	}
	public String getMaSanPham() {
		return maSanPham;
	}
	public void setMaSanPham(String maSanPham) {
		this.maSanPham = maSanPham;
	}
	public String getTenSanPham() {
		return tenSanPham;
	}
	public void setTenSanPham(String tenSanPham) {
		this.tenSanPham = tenSanPham;
	}
	public String getMaVach() {
		return maVach;
	}
	public void setMaVach(String maVach) {
		this.maVach = maVach;
	}
	public double getGiaBan() {
		return giaBan;
	}
	public void setGiaBan(double giaBan) {
		this.giaBan = giaBan;
	}
	public int getSoLuongTon() {
		return soLuongTon;
	}
	public void setSoLuongTon(int soLuongTon) {
		this.soLuongTon = soLuongTon;
	}
	public String getThoiHanBaoHanh() {
		return thoiHanBaoHanh;
	}
	public void setThoiHanBaoHanh(String thoiHanBaoHanh) {
		this.thoiHanBaoHanh = thoiHanBaoHanh;
	}
	public String isTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
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
		return "SanPham [maSanPham=" + maSanPham + ", tenSanPham=" + tenSanPham + ", maVach=" + maVach + ", giaBan="
				+ giaBan + ", soLuongTon=" + soLuongTon + ", thoiHanBaoHanh=" + thoiHanBaoHanh + ", trangThai="
				+ trangThai + "]";
	}
	
	

	
	
	
	
}
