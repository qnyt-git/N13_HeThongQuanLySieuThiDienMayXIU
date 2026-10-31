package ENUM;

/** Trạng thái phân ca. */
public enum TrangThaiPhanCa {
    DA_PHAN("Đã phân"),
    DA_LAM("Đã làm"),
    NGHI("Nghỉ");

    private final String nhan;

    TrangThaiPhanCa(String nhan) { this.nhan = nhan; }

    @Override public String toString() { return nhan; }
}
