package ENUM;

/** Trạng thái sản phẩm. */
public enum TrangThaiSanPham {
    CON_BAN("Đang bán"),
    HET_HANG("Hết hàng"),

    private final String nhan;

    TrangThaiSanPham(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
