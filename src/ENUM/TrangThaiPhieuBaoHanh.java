package ENUM;

public enum TrangThaiPhieuBaoHanh {

    DA_XAC_NHAN("Đã xác nhận"),
    DA_HOAN_THANH("Đã trả"),
    DA_HUY("Đã hủy");

    private final String nhan;

    TrangThaiPhieuBaoHanh(String nhan) {
        this.nhan = nhan;
    }

    @Override
    public String toString() {
        return nhan;
    }
}