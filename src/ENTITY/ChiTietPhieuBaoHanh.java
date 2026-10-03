package ENTITY;

public class ChiTietPhieuBaoHanh {

	private String maChiTietPBH;
	private PhieuBaoHanh maPhieuBaoHanh;
	private SanPham maSanPham;
	private String tinhTrangSanPham; //tình trạng hiện tại của sản phẩm
	private String noiDungBaoHanh; // vd: bảo hành do hư dây diện, bảo hành do mất ốc vít, bảo hành do lỗi.....
	public ChiTietPhieuBaoHanh(String maChiTietPBH, PhieuBaoHanh maPhieuBaoHanh, SanPham maSanPham,
			String tinhTrangSanPham, String noiDungBaoHanh) {
		super();
		this.maChiTietPBH = maChiTietPBH;
		this.maPhieuBaoHanh = maPhieuBaoHanh;
		this.maSanPham = maSanPham;
		this.tinhTrangSanPham = tinhTrangSanPham;
		this.noiDungBaoHanh = noiDungBaoHanh;
	}
	public String getMaChiTietPBH() {
		return maChiTietPBH;
	}
	public void setMaChiTietPBH(String maChiTietPBH) {
		this.maChiTietPBH = maChiTietPBH;
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
	
	
	@Override
	public String toString() {
		return "ChiTietPhieuBaoHanh [maChiTietPBH=" + maChiTietPBH + ", maPhieuBaoHanh=" + maPhieuBaoHanh
				+ ", maSanPham=" + maSanPham + ", tinhTrangSanPham=" + tinhTrangSanPham + ", noiDungBaoHanh="
				+ noiDungBaoHanh + "]";
	}
	
	

}
