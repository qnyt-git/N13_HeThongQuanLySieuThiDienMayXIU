package ENTITY;

import java.util.Date;

public class PhanCa {
	
	private String maPhanCa;
	private NhanVien maNhanVien;
	private CaLamViec maCa;
	private Date ngayLam;
	private String trangThai; //chưa hình dung ra
	
	public PhanCa(String maPhanCa, NhanVien maNhanVien, CaLamViec maCa, Date ngayLam, String trangThai) {
		super();
		this.maPhanCa = maPhanCa;
		this.maNhanVien = maNhanVien;
		this.maCa = maCa;
		this.ngayLam = ngayLam;
		this.trangThai = trangThai;
	}
	public String getMaPhanCa() {
		return maPhanCa;
	}
	public void setMaPhanCa(String maPhanCa) {
		this.maPhanCa = maPhanCa;
	}
	public NhanVien getMaNhanVien() {
		return maNhanVien;
	}
	public void setMaNhanVien(NhanVien maNhanVien) {
		this.maNhanVien = maNhanVien;
	}
	public CaLamViec getMaCa() {
		return maCa;
	}
	public void setMaCa(CaLamViec maCa) {
		this.maCa = maCa;
	}
	public Date getNgayLam() {
		return ngayLam;
	}
	public void setNgayLam(Date ngayLam) {
		this.ngayLam = ngayLam;
	}
	public String getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
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
		return "PhanCa [maPhanCa=" + maPhanCa + ", maNhanVien=" + maNhanVien + ", maCa=" + maCa + ", ngayLam=" + ngayLam
				+ ", trangThai=" + trangThai + "]";
	}
	
	
	
}	
