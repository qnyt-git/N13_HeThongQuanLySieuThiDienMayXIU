package ENTITY;

import java.time.LocalDateTime;

public class HoaDon {

	private String maHoaDon;
	private PhieuDatHang maPhieuDatHang;
	private KhachHang maKhachHang;
	private NhanVien maNhanVien;
	private KhuyenMai maKhuyenMai;
	private String phuongThucThanhToan;
	private LocalDateTime ngayLap;
	private String lyDoHuyHoaDon;
	private String ghiChu;
	public HoaDon(String maHoaDon, PhieuDatHang maPhieuDatHang, KhachHang maKhachHang, NhanVien maNhanVien,
			KhuyenMai maKhuyenMai, String phuongThucThanhToan, LocalDateTime ngayLap, String lyDoHuyHoaDon,
			String ghiChu) {
		super();
		this.maHoaDon = maHoaDon;
		this.maPhieuDatHang = maPhieuDatHang;
		this.maKhachHang = maKhachHang;
		this.maNhanVien = maNhanVien;
		this.maKhuyenMai = maKhuyenMai;
		this.phuongThucThanhToan = phuongThucThanhToan;
		this.ngayLap = ngayLap;
		this.lyDoHuyHoaDon = lyDoHuyHoaDon;
		this.ghiChu = ghiChu;
	}
	public String getMaHoaDon() {
		return maHoaDon;
	}
	public void setMaHoaDon(String maHoaDon) {
		this.maHoaDon = maHoaDon;
	}
	public PhieuDatHang getMaPhieuDatHang() {
		return maPhieuDatHang;
	}
	public void setMaPhieuDatHang(PhieuDatHang maPhieuDatHang) {
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
	public KhuyenMai getMaKhuyenMai() {
		return maKhuyenMai;
	}
	public void setMaKhuyenMai(KhuyenMai maKhuyenMai) {
		this.maKhuyenMai = maKhuyenMai;
	}
	public String getPhuongThucThanhToan() {
		return phuongThucThanhToan;
	}
	public void setPhuongThucThanhToan(String phuongThucThanhToan) {
		this.phuongThucThanhToan = phuongThucThanhToan;
	}
	public LocalDateTime getNgayLap() {
		return ngayLap;
	}
	public void setNgayLap(LocalDateTime ngayLap) {
		this.ngayLap = ngayLap;
	}
	public String getLyDoHuyHoaDon() {
		return lyDoHuyHoaDon;
	}
	public void setLyDoHuyHoaDon(String lyDoHuyHoaDon) {
		this.lyDoHuyHoaDon = lyDoHuyHoaDon;
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
		return "HoaDon [maHoaDon=" + maHoaDon + ", maPhieuDatHang=" + maPhieuDatHang + ", maKhachHang=" + maKhachHang
				+ ", maNhanVien=" + maNhanVien + ", maKhuyenMai=" + maKhuyenMai + ", phuongThucThanhToan="
				+ phuongThucThanhToan + ", ngayLap=" + ngayLap + ", lyDoHuyHoaDon=" + lyDoHuyHoaDon + ", ghiChu="
				+ ghiChu + "]";
	}
	
	
}
