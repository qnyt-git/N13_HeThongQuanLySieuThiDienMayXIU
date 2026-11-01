package ENUM;

/** Trạng thái phiếu bảo hành. */
public enum TrangThaiPhieuBaoHanh {
    DA_XAC_NHAN("Đã Xác Nhận"),
    DA_HOAN_THANH("Đã hoàn thành"),
    DA_HUY("Đã hủy");

    private final String nhan;

    TrangThaiPhieuBaoHanh(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
