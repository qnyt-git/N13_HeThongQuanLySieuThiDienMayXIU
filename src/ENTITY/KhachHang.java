package ENTITY;


public class KhachHang {

	private String maKhachHang;
	private String hoTenKhachHang;
	private String soDienThoai;
	private String diaChi;
	private int diemTichLuy;
	private TrangThaiKhachHang trangThai;
	public KhachHang(String maKhachHang, String hoTenKhachHang, String soDienThoai, String diaChi, int diemTichLuy,
			TrangThaiKhachHang trangThai) {
		super();
		this.maKhachHang = maKhachHang;
		this.hoTenKhachHang = hoTenKhachHang;
		this.soDienThoai = soDienThoai;
		this.diaChi = diaChi;
		this.diemTichLuy = diemTichLuy;
		this.trangThai = trangThai;
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
	public String getDiaChi() {
		return diaChi;
	}
	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}
	public int getDiemTichLuy() {
		return diemTichLuy;
	}
	public void setDiemTichLuy(int diemTichLuy) {
		this.diemTichLuy = diemTichLuy;
	}
	public TrangThaiKhachHang getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(TrangThaiKhachHang trangThai) {
		this.trangThai = trangThai;
	}
	

	//	+KhachHang()
	public KhachHang() {}

	//	+capNhapThongTin() : void
	public void capNhatThongTin() {
	    //......
	}
	
	//	capNhatDiemTichLuy(): void
	public void capNhatDiemTichLuy(int diem) {
	    this.diemTichLuy += diem;
	}
	
	//	+xemLichSuMuaHang() : void
	public void xemLichSuMuaHang() {
//		List<HoaDon> danhSachHoaDon = ...;
	}

	
	@Override
	public String toString() {
		return "KhachHang [maKhachHang=" + maKhachHang + ", hoTenKhachHang=" + hoTenKhachHang + ", soDienThoai="
				+ soDienThoai + ", diaChi=" + diaChi + ", diemTichLuy=" + diemTichLuy + ", trangThai=" + trangThai
				+ "]";
	}
	
}
