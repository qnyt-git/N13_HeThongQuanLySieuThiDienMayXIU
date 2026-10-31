package ENTITY;

import java.util.Objects;

public class NhaCungCap {
    private String maNCC;
    private String tenNCC;
    private String soDienThoai;
    private String diaChi;
    private String email;
    private boolean trangThai; // còn hợp tác - tạm dừng

    public NhaCungCap() {}

    public NhaCungCap(String maNCC, String tenNCC, String soDienThoai,
                      String diaChi, String email, boolean trangThai) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.email = email;
        this.trangThai = trangThai;
    }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }
    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    // ===== METHOD BỔ SUNG =====
    public void capNhatThongTin(String ten, String sdt, String diaChi, String email) {
        if (ten != null) this.tenNCC = ten;
        if (sdt != null) this.soDienThoai = sdt;
        if (diaChi != null) this.diaChi = diaChi;
        if (email != null) this.email = email;
    }

    public void dungHopTac() { this.trangThai = false; }
    public void tiepTucHopTac() { this.trangThai = true; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhaCungCap)) return false;
        return Objects.equals(maNCC, ((NhaCungCap) o).maNCC);
    }

    @Override
    public int hashCode() { return Objects.hash(maNCC); }

    @Override
    public String toString() { return tenNCC; }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    public String trangThaiText() { return trangThai ? "Hợp tác" : "Tạm ngừng"; }
}
