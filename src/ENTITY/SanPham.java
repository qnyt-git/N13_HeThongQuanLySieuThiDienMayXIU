package ENTITY;

import java.util.Objects;
import ENUM.*;
public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private double giaBan;
    private int soLuongTon;
    private String thoiHanBaoHanh; // 1 năm/12 tháng; 2 năm......
    private TrangThaiSanPham trangThai; // đang bán/tạm ngưng/hết hàng

    public SanPham() {}

    public SanPham(String maSanPham, String tenSanPham, String maVach, double giaBan,
                   int soLuongTon, String thoiHanBaoHanh, TrangThaiSanPham trangThai) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.thoiHanBaoHanh = thoiHanBaoHanh;
        this.trangThai = trangThai;
    }

    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }
    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getThoiHanBaoHanh() { return thoiHanBaoHanh; }
    public void setThoiHanBaoHanh(String thoiHanBaoHanh) { this.thoiHanBaoHanh = thoiHanBaoHanh; }
    public TrangThaiSanPham getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiSanPham trangThai) { this.trangThai = trangThai; }

    // ===== METHOD BỔ SUNG =====
    /** Kiểm tra còn hàng để bán */
    public boolean conHang() {
        return soLuongTon > 0
            && trangThai != TrangThaiSanPham.HET_HANG
            && trangThai != TrangThaiSanPham.VO_HIEU_HOA;
    }

    /** Cập nhật tồn kho (nhập thêm) */
    public void nhapKho(int soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng nhập phải > 0");
        this.soLuongTon += soLuong;
        if (this.trangThai == TrangThaiSanPham.HET_HANG) {
            this.trangThai = TrangThaiSanPham.CON_HANG;
        }
    }

    /** Xuất kho (bán) */
    public boolean xuatKho(int soLuong) {
        if (soLuong <= 0) return false;
        if (soLuong > this.soLuongTon) return false;
        this.soLuongTon -= soLuong;
        if (this.soLuongTon == 0) {
            this.trangThai = TrangThaiSanPham.HET_HANG;
        }
        return true;
    }

    /** Kiểm tra có đang bán được không */
    public boolean coTheBan() {
        return trangThai == TrangThaiSanPham.CON_BAN
            || trangThai == TrangThaiSanPham.CON_HANG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SanPham)) return false;
        return Objects.equals(maSanPham, ((SanPham) o).maSanPham);
    }

    @Override
    public int hashCode() { return Objects.hash(maSanPham); }

    @Override
    public String toString() {
        return tenSanPham; // Tiện cho JComboBox
    }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    /** "24 tháng" -> 24. */
    public int baoHanhThang() {
        String s = thoiHanBaoHanh == null ? "" : thoiHanBaoHanh.replaceAll("\\D", "");
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    public void datBaoHanhThang(int thang) { this.thoiHanBaoHanh = thang + " tháng"; }

    /** Bán được với số lượng sl (đang bán và đủ tồn kho). */
    public boolean coTheBanSoLuong(int sl) {
        return coTheBan() && sl > 0 && sl <= soLuongTon;
    }

    /** Đang bán nhưng tồn dưới ngưỡng cảnh báo. */
    public boolean sapHetHang(int nguong) { return coTheBan() && soLuongTon < nguong; }

    /** Còn trong thời hạn bảo hành kể từ ngày mua. */
    public boolean conBaoHanh(java.time.LocalDateTime ngayMua) {
        return ngayMua != null && !ngayMua.plusMonths(baoHanhThang()).isBefore(java.time.LocalDateTime.now());
    }
}
