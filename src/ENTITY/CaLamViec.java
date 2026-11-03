package ENTITY;

import java.time.LocalDateTime;

public class CaLamViec {
    private String maCa;
    private String tenCa;
    private LocalDateTime gioBatDau;
    private LocalDateTime gioKetThuc;

    public CaLamViec() {}

    public CaLamViec(String maCa, String tenCa,
                     LocalDateTime gioBatDau, LocalDateTime gioKetThuc) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
    }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }
    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
    public LocalDateTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalDateTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public LocalDateTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalDateTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }

  

    public boolean hopLe() {
        return gioBatDau != null && gioKetThuc != null
            && gioKetThuc.isAfter(gioBatDau);
    }


    public long thoiLuongPhut() {
        if (!hopLe()) return 0;
        return java.time.Duration.between(gioBatDau, gioKetThuc).toMinutes();
    }

   
    public boolean trongCa(LocalDateTime thoiDiem) {
        return thoiDiem != null
            && !thoiDiem.isBefore(gioBatDau)
            && !thoiDiem.isAfter(gioKetThuc);
    }

    @Override
    public String toString() { return tenCa; }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    private static final java.time.format.DateTimeFormatter GIO = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

    public String gioBatDauText() { return gioBatDau == null ? "" : gioBatDau.format(GIO); }

    public String gioKetThucText() { return gioKetThuc == null ? "" : gioKetThuc.format(GIO); }

    // Đặt giờ bắt đầu/kết thúc theo ngày hôm nay (ca làm việc chỉ quan tâm giờ).
    public void datGio(java.time.LocalTime batDau, java.time.LocalTime ketThuc) {
        java.time.LocalDate ngay = java.time.LocalDate.now();
        this.gioBatDau = ngay.atTime(batDau);
        this.gioKetThuc = ngay.atTime(ketThuc);
    }

    // Ca làm phải nằm trong khung 06:00 - 23:00.
    public boolean trongKhungGioChoPhep() {
        return gioBatDau != null && gioKetThuc != null
            && !gioBatDau.toLocalTime().isBefore(java.time.LocalTime.of(6, 0))
            && !gioKetThuc.toLocalTime().isAfter(java.time.LocalTime.of(23, 0));
    }
}
