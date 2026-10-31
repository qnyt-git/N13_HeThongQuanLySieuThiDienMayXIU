package ENTITY;
import ENUM.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuBaoHanh {
    private String maPhieubaoHanh;
    private KhachHang maKhachHang;
    private NhanVien maNhanVien;
    private LocalDateTime ngayTiepNhan;
    private LocalDateTime ngayHenTra;
    private TrangThaiPhieuBaoHanh trangThai;
    private String ghiChu;
    private List<ChiTietPhieuBaoHanh> chiTietList = new ArrayList<>();

    public PhieuBaoHanh() {}

    public PhieuBaoHanh(String maPhieubaoHanh, KhachHang maKhachHang, NhanVien maNhanVien,
                        LocalDateTime ngayTiepNhan, LocalDateTime ngayHenTra,
                        TrangThaiPhieuBaoHanh trangThai, String ghiChu) {
        this.maPhieubaoHanh = maPhieubaoHanh;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.ngayTiepNhan = ngayTiepNhan;
        this.ngayHenTra = ngayHenTra;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public String getMaPhieubaoHanh() { return maPhieubaoHanh; }
    public void setMaPhieubaoHanh(String maPhieubaoHanh) { this.maPhieubaoHanh = maPhieubaoHanh; }
    public KhachHang getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(KhachHang maKhachHang) { this.maKhachHang = maKhachHang; }
    public NhanVien getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(NhanVien maNhanVien) { this.maNhanVien = maNhanVien; }
    public LocalDateTime getNgayTiepNhan() { return ngayTiepNhan; }
    public void setNgayTiepNhan(LocalDateTime ngayTiepNhan) { this.ngayTiepNhan = ngayTiepNhan; }
    public LocalDateTime getNgayHenTra() { return ngayHenTra; }
    public void setNgayHenTra(LocalDateTime ngayHenTra) { this.ngayHenTra = ngayHenTra; }
    public TrangThaiPhieuBaoHanh getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhieuBaoHanh trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public List<ChiTietPhieuBaoHanh> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietPhieuBaoHanh> l) { this.chiTietList = l; }

    // ===== METHOD BỔ SUNG =====
    public void themChiTiet(ChiTietPhieuBaoHanh ct) { chiTietList.add(ct); }

    public void capNhatTrangThai(TrangThaiPhieuBaoHanh tt) { this.trangThai = tt; }

    public void traSanPham() {
        this.trangThai = TrangThaiPhieuBaoHanh.DA_TRA;
    }

    public void huyPhieu() {
        this.trangThai = TrangThaiPhieuBaoHanh.DA_HUY;
    }

    public boolean quaHan() {
        return LocalDateTime.now().isAfter(this.ngayHenTra)
            && this.trangThai != TrangThaiPhieuBaoHanh.DA_TRA;
    }

    @Override
    public String toString() { return maPhieubaoHanh; }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    public ChiTietPhieuBaoHanh taoChiTiet(SanPham sp, String tinhTrang, String noiDung) {
        ChiTietPhieuBaoHanh ct = new ChiTietPhieuBaoHanh(maPhieubaoHanh + "-" + sp.getMaSanPham(), this, sp, tinhTrang, noiDung);
        themChiTiet(ct);
        return ct;
    }

    public boolean dangXuLy() { return trangThai == TrangThaiPhieuBaoHanh.DANG_XU_LY; }

    /** Nhãn hiển thị: "Quá hạn" nếu đã quá ngày hẹn trả mà chưa trả. */
    public String trangThaiHienThi() { return quaHan() ? "Quá hạn" : trangThai.toString(); }
}
