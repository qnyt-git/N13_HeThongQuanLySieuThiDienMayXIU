package ENTITY;

public class ChiTietPhieuBaoHanh {
	
	private PhieuBaoHanh maPhieuBaoHanh;
	private SanPham maSanPham;
	private String tinhTrangSanPham; // tình trạng hiện tại của sản phẩm
	private String noiDungBaoHanh; // vd: bảo hành dây điện, bảo hành do không thể khởi động sản phẩm, bảo hành do lỗi.....
	public ChiTietPhieuBaoHanh(PhieuBaoHanh maPhieuBaoHanh, SanPham maSanPham,
			String tinhTrangSanPham, String noiDungBaoHanh) {
		super();
		this.maPhieuBaoHanh = maPhieuBaoHanh;
		this.maSanPham = maSanPham;
		this.tinhTrangSanPham = tinhTrangSanPham;
		this.noiDungBaoHanh = noiDungBaoHanh;
	}

	public PhieuBaoHanh getMaPhieuBaoHanh() {
		return maPhieuBaoHanh;
	}
	public void setMaPhieuBaoHanh(PhieuBaoHanh maPhieuBaoHanh) {
		this.maPhieuBaoHanh = maPhieuBaoHanh;
	}
	public SanPham getMaSanPham() {
		return maSanPham;
	}
	public void setMaSanPham(SanPham maSanPham) {
		this.maSanPham = maSanPham;
	}
	public String getTinhTrangSanPham() {
		return tinhTrangSanPham;
	}
	public void setTinhTrangSanPham(String tinhTrangSanPham) {
		this.tinhTrangSanPham = tinhTrangSanPham;
	}
	public String getNoiDungBaoHanh() {
		return noiDungBaoHanh;
	}
	public void setNoiDungBaoHanh(String noiDungBaoHanh) {
		this.noiDungBaoHanh = noiDungBaoHanh;
	}
	
	
	//thiếu method
			//
			//
			//
			//
			//
			//
	public boolean hopLe() {
	    return maSanPham != null && tinhTrangSanPham != null && !tinhTrangSanPham.isEmpty()
	        && noiDungBaoHanh != null && !noiDungBaoHanh.isEmpty();
	}
	public void CapNhatTinhTrang() {}
	public void CapNhatNoiDungBaoHanh() {}
	public boolean KiemTraBaoHanh() {}
	
	@Override
	public String toString() {
		return "ChiTietPhieuBaoHanh [maPhieuBaoHanh=" + maPhieuBaoHanh
				+ ", maSanPham=" + maSanPham + ", tinhTrangSanPham=" + tinhTrangSanPham + ", noiDungBaoHanh="
				+ noiDungBaoHanh + "]";
	}
	
	

}