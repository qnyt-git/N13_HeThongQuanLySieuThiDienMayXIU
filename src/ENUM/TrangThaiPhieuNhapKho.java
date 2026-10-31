package ENUM;

/** Trạng thái phiếu nhập kho. */
public enum TrangThaiPhieuNhapKho {
    CHO_DUYET("Chờ duyệt"),
    DA_XAC_NHAN("Đã xác nhận"),
    DA_HUY("Đã hủy");

    private final String nhan;

    TrangThaiPhieuNhapKho(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
