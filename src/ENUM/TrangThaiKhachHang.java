package ENUM;

/** Trạng thái / xếp hạng khách hàng (theo điểm tích lũy). */
public enum TrangThaiKhachHang {
    KH_MOI("Khách mới"),
    TOT("Khách tốt"),
    NOXAU("Nợ xấu"),
    DANHSACHDEN("Danh sách đen");

    private final String nhan;

    TrangThaiKhachHang(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
