package ENTITY;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ENUM.TrangThaiPhieuDatHang;

public class PhieuDatHang {
    private String maPhieuDatHang;
    private KhachHang maKhachHang;
    private NhanVien maNhanVien;
    private LocalDateTime ngayDatHang;
    private TrangThaiPhieuDatHang trangThai; // đã xác nhận/đã hoàn thành/đã hủy
    private String ghiChu;
    private KhuyenMai maKhuyenMai; // Khuyen mai da chon khi lap phieu dat hang
    private List<ChiTietPhieuDatHang> chiTietList = new ArrayList<>();

    public PhieuDatHang() {}

    public PhieuDatHang(String maPhieuDatHang, KhachHang maKhachHang, NhanVien maNhanVien,
                        LocalDateTime ngayDatHang, TrangThaiPhieuDatHang trangThai, String ghiChu) {
        this.maPhieuDatHang = maPhieuDatHang;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.ngayDatHang = ngayDatHang;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public String getMaPhieuDatHang() { return maPhieuDatHang; }
    public void setMaPhieuDatHang(String maPhieuDatHang) { this.maPhieuDatHang = maPhieuDatHang; }
    public KhachHang getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(KhachHang maKhachHang) { this.maKhachHang = maKhachHang; }
    public NhanVien getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(NhanVien maNhanVien) { this.maNhanVien = maNhanVien; }
    public LocalDateTime getNgayDatHang() { return ngayDatHang; }
    public void setNgayDatHang(LocalDateTime ngayDatHang) { this.ngayDatHang = ngayDatHang; }
    public TrangThaiPhieuDatHang isTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhieuDatHang trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public KhuyenMai getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(KhuyenMai maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }
    public List<ChiTietPhieuDatHang> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietPhieuDatHang> l) { this.chiTietList = l; }

    // ===== METHOD BỔ SUNG =====
    public void themChiTiet(ChiTietPhieuDatHang ct) { chiTietList.add(ct); }

    public double tinhTongTien() {
        double tong = 0;
        for (ChiTietPhieuDatHang ct : chiTietList) {
            tong += ct.getSoLuong() * ct.getDonGia();
        }
        return tong;
    }

    public void xacNhan() {
        if (this.trangThai == TrangThaiPhieuDatHang.CHO_XU_LY) {
            this.trangThai = TrangThaiPhieuDatHang.DA_XAC_NHAN;
        }
    }

    public void huyDat() {
        this.trangThai = TrangThaiPhieuDatHang.DA_HUY;
    }

    public boolean daXacNhan() {
        return this.trangThai == TrangThaiPhieuDatHang.DA_XAC_NHAN || this.trangThai == TrangThaiPhieuDatHang.DA_HOAN_THANH;
    }

    @Override
    public String toString() { return maPhieuDatHang; }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    public ChiTietPhieuDatHang taoChiTiet(
            SanPham sp, int soLuong, double donGia) {

        ChiTietPhieuDatHang ct = new ChiTietPhieuDatHang(
                this, sp, soLuong, donGia);

        themChiTiet(ct);
        return ct;
    }

    /** Nhãn trạng thái; entity chỉ có boolean nên "Đã giao" được suy ra từ việc đã lập hóa đơn. */
    public String trangThaiText() {
        return trangThai == null ? "" : trangThai.toString();
    }
}
