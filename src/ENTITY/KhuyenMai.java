package ENTITY;

import java.time.LocalDateTime;

public class KhuyenMai {

	private String maKhuyenMai;
	private String tenKhuyenMai;
	private LocalDateTime thoiGianBatDau;
	private LocalDateTime thoiGianKetThuc;
	private double giaTriGiam;
	private boolean trangThai; // con hieu luc/het hieu luc
	private String ghiChu;
	public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, LocalDateTime thoiGianBatDau,
			LocalDateTime thoiGianKetThuc, double giaTriGiam, boolean trangThai, String ghiChu) {
		super();
		this.maKhuyenMai = maKhuyenMai;
		this.tenKhuyenMai = tenKhuyenMai;
		this.thoiGianBatDau = thoiGianBatDau;
		this.thoiGianKetThuc = thoiGianKetThuc;
		this.giaTriGiam = giaTriGiam;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
	}
	public String getMaKhuyenMai() {
		return maKhuyenMai;
	}
	public void setMaKhuyenMai(String maKhuyenMai) {
		this.maKhuyenMai = maKhuyenMai;
	}
	public String getTenKhuyenMai() {
		return tenKhuyenMai;
	}
	public void setTenKhuyenMai(String tenKhuyenMai) {
		this.tenKhuyenMai = tenKhuyenMai;
	}
	public LocalDateTime getThoiGianBatDau() {
		return thoiGianBatDau;
	}
	public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
		this.thoiGianBatDau = thoiGianBatDau;
	}
	public LocalDateTime getThoiGianKetThuc() {
		return thoiGianKetThuc;
	}
	public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
		this.thoiGianKetThuc = thoiGianKetThuc;
	}
	public double getGiaTriGiam() {
		return giaTriGiam;
	}
	public void setGiaTriGiam(double giaTriGiam) {
		this.giaTriGiam = giaTriGiam;
	}
	public boolean isTrangThai() {
		return trangThai;
	}
	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}
	public String getGhiChu() {
		return ghiChu;
	}
	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
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
		return "KhuyenMai [maKhuyenMai=" + maKhuyenMai + ", tenKhuyenMai=" + tenKhuyenMai + ", thoiGianBatDau="
				+ thoiGianBatDau + ", thoiGianKetThuc=" + thoiGianKetThuc + ", giaTriGiam=" + giaTriGiam
				+ ", trangThai=" + trangThai + ", ghiChu=" + ghiChu + "]";
	}
	
	
}
