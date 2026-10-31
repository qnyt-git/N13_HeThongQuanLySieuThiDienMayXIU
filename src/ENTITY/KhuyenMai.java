package ENTITY;

import java.time.LocalDateTime;
import java.util.Objects;

public class KhuyenMai {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private double giaTriGiam;
    private boolean trangThai;
    private String ghiChu;

    public KhuyenMai() {}

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, LocalDateTime thoiGianBatDau,
                     LocalDateTime thoiGianKetThuc, double giaTriGiam, boolean trangThai, String ghiChu) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.giaTriGiam = giaTriGiam;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }
    public String getTenKhuyenMai() { return tenKhuyenMai; }
    public void setTenKhuyenMai(String tenKhuyenMai) { this.tenKhuyenMai = tenKhuyenMai; }
    public LocalDateTime getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }
    public LocalDateTime getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
    public double getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(double giaTriGiam) { this.giaTriGiam = giaTriGiam; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    // ===== METHOD BỔ SUNG =====
    /** Kiểm tra khuyến mãi còn hiệu lực */
    public boolean conHieuLuc() {
        if (!trangThai) return false;
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(thoiGianBatDau) && now.isBefore(thoiGianKetThuc);
    }

    /** Tính tiền giảm từ tổng tiền */
    public double tinhTienGiam(double tongTien) {
        if (!conHieuLuc()) return 0;
        return tongTien * (giaTriGiam / 100.0);
    }

    /** Áp dụng giảm giá */
    public double apDung(double tongTien) {
        return tongTien - tinhTienGiam(tongTien);
    }

    /** Gia hạn */
    public void giaHan(LocalDateTime ngayKetThucMoi) {
        if (ngayKetThucMoi.isAfter(thoiGianBatDau)) {
            this.thoiGianKetThuc = ngayKetThucMoi;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KhuyenMai)) return false;
        return Objects.equals(maKhuyenMai, ((KhuyenMai) o).maKhuyenMai);
    }

    @Override
    public int hashCode() { return Objects.hash(maKhuyenMai); }

    @Override
    public String toString() {
        return tenKhuyenMai + " (" + giaTriGiam + "%)";
    }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    public String trangThaiText() { return trangThai ? "Đang áp dụng" : "Ngừng"; }

    /** Đặt thời gian theo ngày: từ 00:00 ngày bắt đầu đến 23:59:59 ngày kết thúc. */
    public void datThoiGian(java.time.LocalDate batDau, java.time.LocalDate ketThuc) {
        this.thoiGianBatDau = batDau.atStartOfDay();
        this.thoiGianKetThuc = ketThuc.atTime(23, 59, 59);
    }

    /** Miền giá trị: 0 < giá trị giảm <= 100 và ngày kết thúc không trước ngày bắt đầu. */
    public boolean hopLe() {
        return giaTriGiam > 0 && giaTriGiam <= 100 && thoiGianBatDau != null && thoiGianKetThuc != null
            && !thoiGianKetThuc.isBefore(thoiGianBatDau);
    }

    /** Tiền giảm theo % đã áp dụng lúc lập hóa đơn (không phụ thuộc hạn hiện tại của khuyến mãi). */
    public double tinhTienGiamDaApDung(double tongTien) {
        return tongTien * (giaTriGiam / 100.0);
    }
}
