package ENTITY;

import java.time.DateTimeException;
import java.util.Date;

public class NhanVien {
	private String maNhanVien;
	private String hoTenNhanVien;
	private String chucVu;
	private String soDienThoai;
	private Date ngaySinh;
	private float heSoLuong;
	private int luongCoBan;
	private boolean trangThai; //còn làm/nghỉ việc
	public NhanVien(String maNhanVien, String hoTenNhanVien, String chucVu, String soDienThoai, Date ngaySinh,
			float heSoLuong, int luongCoBan, boolean trangThai) {
		super();
		this.maNhanVien = maNhanVien;
		this.hoTenNhanVien = hoTenNhanVien;
		this.chucVu = chucVu;
		this.soDienThoai = soDienThoai;
		this.ngaySinh = ngaySinh;
		this.heSoLuong = heSoLuong;
		this.luongCoBan = luongCoBan;
		this.trangThai = trangThai;
	}
	public String getMaNhanVien() {
		return maNhanVien;
	}
	public void setMaNhanVien(String maNhanVien) {
		this.maNhanVien = maNhanVien;
	}
	public String getHoTenNhanVien() {
		return hoTenNhanVien;
	}
	public void setHoTenNhanVien(String hoTenNhanVien) {
		this.hoTenNhanVien = hoTenNhanVien;
	}
	public String getChucVu() {
		return chucVu;
	}
	public void setChucVu(String chucVu) {
		this.chucVu = chucVu;
	}
	public String getSoDienThoai() {
		return soDienThoai;
	}
	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}
	public Date getNgaySinh() {
		return ngaySinh;
	}
	public void setNgaySinh(Date ngaySinh) {
		this.ngaySinh = ngaySinh;
	}
	public float getHeSoLuong() {
		return heSoLuong;
	}
	public void setHeSoLuong(float heSoLuong) {
		this.heSoLuong = heSoLuong;
	}
	public int getLuongCoBan() {
		return luongCoBan;
	}
	public void setLuongCoBan(int luongCoBan) {
		this.luongCoBan = luongCoBan;
	}
	public boolean isTrangThai() {
		return trangThai;
	}
	public void setTrangThai(boolean trangThai) {
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
		return "NhanVien [maNhanVien=" + maNhanVien + ", hoTenNhanVien=" + hoTenNhanVien + ", chucVu=" + chucVu
				+ ", soDienThoai=" + soDienThoai + ", ngaySinh=" + ngaySinh + ", heSoLuong=" + heSoLuong
				+ ", luongCoBan=" + luongCoBan + ", trangThai=" + trangThai + "]";
	}
	
	
	
}
