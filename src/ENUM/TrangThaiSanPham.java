package ENUM;

/** Trạng thái sản phẩm. */
public enum TrangThaiSanPham {
    CON_BAN("Đang bán"),
    CON_HANG("Còn hàng"),
    HET_HANG("Hết hàng"),
    VO_HIEU_HOA("Ngừng bán");

    private final String nhan;

    TrangThaiSanPham(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
