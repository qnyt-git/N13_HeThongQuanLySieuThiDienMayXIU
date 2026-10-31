package ENUM;

/** Trạng thái hóa đơn. */
public enum TrangThaiHoaDon {
    CHUA_THANH_TOAN("Chưa thanh toán"),
    DA_THANH_TOAN("Đã thanh toán"),
    DA_HUY("Đã hủy");

    private final String nhan;

    TrangThaiHoaDon(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
