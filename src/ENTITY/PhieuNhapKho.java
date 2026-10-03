package ENTITY;

import java.time.LocalDateTime;

public class PhieuNhapKho {
	
	private String maPhieuNhapKho;
	private LocalDateTime ngayNhap;
	private NhanVien maNhanVien;
	private NhaCungCap maNCC;
	private String lyDoNhap;
	private String trangThai; //dang cho duyet phieu; phieu da xac nhan ; phieu da bi huy; 
	private String ghiChu;
	public PhieuNhapKho(String maPhieuNhapKho, LocalDateTime ngayNhap, NhanVien maNhanVien, NhaCungCap maNCC,
			String lyDoNhap, String trangThai, String ghiChu) {
		super();
		this.maPhieuNhapKho = maPhieuNhapKho;
		this.ngayNhap = ngayNhap;
		this.maNhanVien = maNhanVien;
		this.maNCC = maNCC;
		this.lyDoNhap = lyDoNhap;
		this.trangThai = trangThai;
		this.ghiChu = ghiChu;
	}
	public String getMaPhieuNhapKho() {
		return maPhieuNhapKho;
	}
	public void setMaPhieuNhapKho(String maPhieuNhapKho) {
		this.maPhieuNhapKho = maPhieuNhapKho;
	}
	public LocalDateTime getNgayNhap() {
		return ngayNhap;
	}
	public void setNgayNhap(LocalDateTime ngayNhap) {
		this.ngayNhap = ngayNhap;
	}
	public NhanVien getMaNhanVien() {
		return maNhanVien;
	}
	public void setMaNhanVien(NhanVien maNhanVien) {
		this.maNhanVien = maNhanVien;
	}
	public NhaCungCap getMaNCC() {
		return maNCC;
	}
	public void setMaNCC(NhaCungCap maNCC) {
		this.maNCC = maNCC;
	}
	public String getLyDoNhap() {
		return lyDoNhap;
	}
	public void setLyDoNhap(String lyDoNhap) {
		this.lyDoNhap = lyDoNhap;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
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
		return "PhieuNhapKho [maPhieuNhapKho=" + maPhieuNhapKho + ", ngayNhap=" + ngayNhap + ", maNhanVien="
				+ maNhanVien + ", maNCC=" + maNCC + ", lyDoNhap=" + lyDoNhap + ", trangThai=" + trangThai + ", ghiChu="
				+ ghiChu + "]";
	}

}
