package ENTITY;

import java.time.LocalDate;

import ENUM.*;

public class PhanCa {
    private String maPhanCa;
    private NhanVien maNhanVien;
    private CaLamViec maCa;
    private LocalDate ngayLam;
    private TrangThaiPhanCa trangThai;

    public PhanCa() {}

    public PhanCa(String maPhanCa, NhanVien maNhanVien, CaLamViec maCa,
                  LocalDate ngayLam, TrangThaiPhanCa trangThai) {
        this.maPhanCa = maPhanCa;
        this.maNhanVien = maNhanVien;
        this.maCa = maCa;
        this.ngayLam = ngayLam;
        this.trangThai = trangThai;
    }

    public String getMaPhanCa() { return maPhanCa; }
    public void setMaPhanCa(String maPhanCa) { this.maPhanCa = maPhanCa; }
    public NhanVien getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(NhanVien maNhanVien) { this.maNhanVien = maNhanVien; }
    public CaLamViec getMaCa() { return maCa; }
    public void setMaCa(CaLamViec maCa) { this.maCa = maCa; }
    public LocalDate getNgayLam() { return ngayLam; }
    public void setNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }
    public TrangThaiPhanCa getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhanCa trangThai) { this.trangThai = trangThai; }

    // ===== METHOD BỔ SUNG =====
    public void danhDauDaLam() { this.trangThai = TrangThaiPhanCa.DA_LAM; }
    public void danhDauNghi() { this.trangThai = TrangThaiPhanCa.NGHI; }

    public boolean laNgayHomNay() {
        return ngayLam != null && ngayLam.equals(LocalDate.now());
    }

    /** Kiểm tra tổ hợp (NV + Ngày + Ca) trùng */
    public boolean trungVoi(PhanCa other) {
        if (other == null) return false;
        return this.maNhanVien.getMaNhanVien().equals(other.maNhanVien.getMaNhanVien())
            && this.ngayLam.equals(other.ngayLam)
            && this.maCa.getMaCa().equals(other.maCa.getMaCa());
    }

    @Override
    public String toString() {
        return maNhanVien.getHoTenNhanVien() + " - " + maCa.getTenCa() + " - " + ngayLam;
    }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    public boolean daNghi() { return trangThai == TrangThaiPhanCa.NGHI; }

    /** Ngày làm nằm trong tuần bắt đầu từ thuHai (thứ Hai). */
    public boolean thuocTuan(LocalDate thuHai) {
        return ngayLam != null && !ngayLam.isBefore(thuHai) && !ngayLam.isAfter(thuHai.plusDays(6));
    }
}
