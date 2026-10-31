package ENUM;

/** Trạng thái phiếu bảo hành. */
public enum TrangThaiPhieuBaoHanh {
    DANG_XU_LY("Đang xử lý"),
    DA_TRA("Đã trả"),
    DA_HUY("Đã hủy");

    private final String nhan;

    TrangThaiPhieuBaoHanh(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
