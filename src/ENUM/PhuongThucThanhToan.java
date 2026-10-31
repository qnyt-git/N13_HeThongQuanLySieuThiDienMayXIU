package ENUM;

/** Phương thức thanh toán. */
public enum PhuongThucThanhToan {
    TIEN_MAT("Tiền mặt"),
    CHUYEN_KHOAN("Chuyển khoản"),
    QR("QR");

    private final String nhan;

    PhuongThucThanhToan(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
