package ENTITY;

import java.time.LocalDateTime;

public class PhieuBaoHanh {

	private String maPhieubaoHanh;
	private KhachHang maKhachHang;
	private NhanVien maNhanVien;
	private LocalDateTime ngayTiepNhan;
	private LocalDateTime ngayHenTra;
	private boolean trangThai; //............
	private String ghiChu;
	public PhieuBaoHanh(String maPhieubaoHanh, KhachHang maKhachHang, NhanVien maNhanVien, LocalDateTime ngayTiepNhan,
			LocalDateTime ngayHenTra, boolean trangThai, String ghiChu) {
		super();
		this.maPhieubaoHanh = maPhieubaoHanh;
		this.maKhachHang = maKhachHang;
		this.maNhanVien = maNhanVien;
		this.ngayTiepNhan = ngayTiepNhan;
		this.ngayHenTra = ngayHenTra;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
	}
	public String getMaPhieubaoHanh() {
		return maPhieubaoHanh;
	}
	public void setMaPhieubaoHanh(String maPhieubaoHanh) {
		this.maPhieubaoHanh = maPhieubaoHanh;
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
	public LocalDateTime getNgayTiepNhan() {
		return ngayTiepNhan;
	}
	public void setNgayTiepNhan(LocalDateTime ngayTiepNhan) {
		this.ngayTiepNhan = ngayTiepNhan;
	}
	public LocalDateTime getNgayHenTra() {
		return ngayHenTra;
	}
	public void setNgayHenTra(LocalDateTime ngayHenTra) {
		this.ngayHenTra = ngayHenTra;
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
		return "PhieuBaoHanh [maPhieubaoHanh=" + maPhieubaoHanh + ", maKhachHang=" + maKhachHang + ", maNhanVien="
				+ maNhanVien + ", ngayTiepNhan=" + ngayTiepNhan + ", ngayHenTra=" + ngayHenTra + ", trangThai="
				+ trangThai + ", ghiChu=" + ghiChu + "]";
	}
	
	
	

}
