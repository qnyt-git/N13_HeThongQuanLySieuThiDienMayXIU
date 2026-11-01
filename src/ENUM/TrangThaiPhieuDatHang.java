package ENUM;

public enum TrangThaiPhieuDatHang {
	DA_XAC_NHAN("Đã xác nhận"),
	DA_HOAN_THANH("Đã hoàn thành"),
	DA_HUY("Đã hủy");


	private final String nhan;

	    TrangThaiPhieuDatHang(String nhan) { this.nhan = nhan; }
	    @Override public String toString() { return nhan; }
}
