package ENTITY;

import java.time.LocalDateTime;

public class CaLamViec {

	private String maCa;
	private String tenCa;
	private PhanCa maPhanCa;
	private NhanVien maNhanVien;
	private LocalDateTime gioBatDau;
	private LocalDateTime gioKetThuc;
	public CaLamViec(String maCa, String tenCa, PhanCa maPhanCa, NhanVien maNhanVien, LocalDateTime gioBatDau,
			LocalDateTime gioKetThuc) {
		super();
		this.maCa = maCa;
		this.tenCa = tenCa;
		this.maPhanCa = maPhanCa;
		this.maNhanVien = maNhanVien;
		this.gioBatDau = gioBatDau;
		this.gioKetThuc = gioKetThuc;
	}
	public String getMaCa() {
		return maCa;
	}
	public void setMaCa(String maCa) {
		this.maCa = maCa;
	}
	public String getTenCa() {
		return tenCa;
	}
	public void setTenCa(String tenCa) {
		this.tenCa = tenCa;
	}
	public PhanCa getMaPhanCa() {
		return maPhanCa;
	}
	public void setMaPhanCa(PhanCa maPhanCa) {
		this.maPhanCa = maPhanCa;
	}
	public NhanVien getMaNhanVien() {
		return maNhanVien;
	}
	public void setMaNhanVien(NhanVien maNhanVien) {
		this.maNhanVien = maNhanVien;
	}
	public LocalDateTime getGioBatDau() {
		return gioBatDau;
	}
	public void setGioBatDau(LocalDateTime gioBatDau) {
		this.gioBatDau = gioBatDau;
	}
	public LocalDateTime getGioKetThuc() {
		return gioKetThuc;
	}
	public void setGioKetThuc(LocalDateTime gioKetThuc) {
		this.gioKetThuc = gioKetThuc;
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
		return "CaLamViec [maCa=" + maCa + ", tenCa=" + tenCa + ", maPhanCa=" + maPhanCa + ", maNhanVien=" + maNhanVien
				+ ", gioBatDau=" + gioBatDau + ", gioKetThuc=" + gioKetThuc + "]";
	}
	

}
