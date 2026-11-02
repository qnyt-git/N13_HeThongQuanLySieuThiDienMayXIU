package ENUM;

/** Trạng thái sản phẩm. */
public enum TrangThaiSanPham {
    CON_BAN("Đang bán"),
    TAM_NGUNG("Tạm ngưng"),
    HET_HANG("Hết hàng"),
	VO_HIEU_HOA("Vô hiệu hóa");

    private final String nhan;

    TrangThaiSanPham(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
