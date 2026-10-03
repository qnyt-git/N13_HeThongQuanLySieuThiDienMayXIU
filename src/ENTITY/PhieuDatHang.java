package ENTITY;

import java.time.LocalDateTime;

public class PhieuDatHang {

	private String maPhieuDatHang;
	private KhachHang maKhachHang;
	private NhanVien maNhanVien;
	private LocalDateTime ngayDatHang;
	private boolean trangThai; // da xac nhan/da huy dat
	private String ghiChu;
	public PhieuDatHang(String maPhieuDatHang, KhachHang maKhachHang, NhanVien maNhanVien, LocalDateTime ngayDatHang,
			boolean trangThai, String ghiChu) {
		super();
		this.maPhieuDatHang = maPhieuDatHang;
		this.maKhachHang = maKhachHang;
		this.maNhanVien = maNhanVien;
		this.ngayDatHang = ngayDatHang;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
	}
	public String getMaPhieuDatHang() {
		return maPhieuDatHang;
	}
	public void setMaPhieuDatHang(String maPhieuDatHang) {
		this.maPhieuDatHang = maPhieuDatHang;
	}
	public KhachHang getMaKhachHang() {
		return maKhachHang;
	}
	public void setMaKhachHang(KhachHang maKhachHang) {
		this.maKhachHang = maKhachHang;
	}
	public NhanVien getMaNhanVien() {
		return maNhanVien;
	}
	public void setMaNhanVien(NhanVien maNhanVien) {
		this.maNhanVien = maNhanVien;
	}
	public LocalDateTime getNgayDatHang() {
		return ngayDatHang;
	}
	public void setNgayDatHang(LocalDateTime ngayDatHang) {
		this.ngayDatHang = ngayDatHang;
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
		return "PhieuDatHang [maPhieuDatHang=" + maPhieuDatHang + ", maKhachHang=" + maKhachHang + ", maNhanVien="
				+ maNhanVien + ", ngayDatHang=" + ngayDatHang + ", trangThai=" + trangThai + ", ghiChu=" + ghiChu + "]";
	}
	
	
}
