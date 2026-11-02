package ENTITY;

public class ChiTietPhieuBaoHanh {
	
	private PhieuBaoHanh maPhieuBaoHanh;
	private SanPham maSanPham;
	private String tinhTrangSanPham; 
	private String noiDungBaoHanh;
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
	
	
	public boolean hopLe() {
	    return maSanPham != null && tinhTrangSanPham != null && !tinhTrangSanPham.isEmpty()
	        && noiDungBaoHanh != null && !noiDungBaoHanh.isEmpty();
	}
	public void CapNhatTinhTrang() {}
	public void CapNhatNoiDungBaoHanh() {}
//	public boolean KiemTraBaoHanh() {}
	
	@Override
	public String toString() {
		return "ChiTietPhieuBaoHanh [maPhieuBaoHanh=" + maPhieuBaoHanh
				+ ", maSanPham=" + maSanPham + ", tinhTrangSanPham=" + tinhTrangSanPham + ", noiDungBaoHanh="
				+ noiDungBaoHanh + "]";
	}
	
	

}