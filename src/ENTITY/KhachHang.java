package ENTITY;

import java.util.Objects;
import ENUM.*;
public class KhachHang {
    private String maKhachHang;
    private String hoTenKhachHang;
    private String soDienThoai;
    private int diemTichLuy;
    private LoaiKhachHang LoaiKH; // Khách mới/Khách vip
    
    public KhachHang() {}

	public KhachHang(String maKhachHang, String hoTenKhachHang, String soDienThoai, String diaChi, int diemTichLuy,
			LoaiKhachHang loaiKH) {
		super();
		this.maKhachHang = maKhachHang;
		this.hoTenKhachHang = hoTenKhachHang;
		this.soDienThoai = soDienThoai;
		this.diemTichLuy = diemTichLuy;
		LoaiKH = loaiKH;
	}

	public String getMaKhachHang() {
		return maKhachHang;
	}

	public void setMaKhachHang(String maKhachHang) {
		this.maKhachHang = maKhachHang;
	}

	public String getHoTenKhachHang() {
		return hoTenKhachHang;
	}

	public void setHoTenKhachHang(String hoTenKhachHang) {
		this.hoTenKhachHang = hoTenKhachHang;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}

	public int getDiemTichLuy() {
		return diemTichLuy;
	}

	public void setDiemTichLuy(int diemTichLuy) {
		this.diemTichLuy = diemTichLuy;
	}

	public LoaiKhachHang getLoaiKH() {
		return LoaiKH;
	}

	public void setLoaiKH(LoaiKhachHang loaiKH) {
		LoaiKH = loaiKH;
	}

	// thêm method
	public void CapNhatThongTin() {}
	public void CapNhatDiemTichLuy() {}
	public void XemLichSuMuaHang() {}
	
	
	
	
	
	
	@Override
	public String toString() {
		return "KhachHang [maKhachHang=" + maKhachHang + ", hoTenKhachHang=" + hoTenKhachHang + ", soDienThoai="
				+ soDienThoai + ", diemTichLuy=" + diemTichLuy + ", LoaiKH=" + LoaiKH + "]";
	}
    
	
    
    
}
