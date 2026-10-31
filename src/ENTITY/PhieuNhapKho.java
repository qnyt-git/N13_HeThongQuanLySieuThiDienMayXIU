package ENTITY;
import ENUM.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapKho {
    private String maPhieuNhapKho;
    private LocalDateTime ngayNhap;
    private NhanVien maNhanVien;
    private NhaCungCap maNCC;
    private String lyDoNhap; 
    private TrangThaiPhieuNhapKho trangThai; // chờ duyệt / đã xác nhận / đã hủy
    private List<ChiTietPhieuNhapKho> chiTietList = new ArrayList<>();

    public PhieuNhapKho() {}

    public PhieuNhapKho(String maPhieuNhapKho, LocalDateTime ngayNhap, NhanVien maNhanVien,
                        NhaCungCap maNCC, String lyDoNhap, TrangThaiPhieuNhapKho trangThai) {
        this.maPhieuNhapKho = maPhieuNhapKho;
        this.ngayNhap = ngayNhap;
        this.maNhanVien = maNhanVien;
        this.maNCC = maNCC;
        this.lyDoNhap = lyDoNhap;
        this.trangThai = trangThai;
    }

    // Getters/Setters
    public String getMaPhieuNhapKho() { return maPhieuNhapKho; }
    public void setMaPhieuNhapKho(String maPhieuNhapKho) { this.maPhieuNhapKho = maPhieuNhapKho; }
    public LocalDateTime getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDateTime ngayNhap) { this.ngayNhap = ngayNhap; }
    public NhanVien getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(NhanVien maNhanVien) { this.maNhanVien = maNhanVien; }
    public NhaCungCap getMaNCC() { return maNCC; }
    public void setMaNCC(NhaCungCap maNCC) { this.maNCC = maNCC; }
    public String getLyDoNhap() { return lyDoNhap; }
    public void setLyDoNhap(String lyDoNhap) { this.lyDoNhap = lyDoNhap; }
    public TrangThaiPhieuNhapKho getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhieuNhapKho trangThai) { this.trangThai = trangThai; }
    public List<ChiTietPhieuNhapKho> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietPhieuNhapKho> l) { this.chiTietList = l; }

    // ===== METHOD BỔ SUNG =====
    public void themChiTiet(ChiTietPhieuNhapKho ct) { chiTietList.add(ct); }

    /** Tổng tiền nhập */
    public double tinhTongTien() {
        double tong = 0;
        for (ChiTietPhieuNhapKho ct : chiTietList) {
            tong += ct.getSoLuong() * ct.getDonGia();
        }
        return tong;
    }

    /** Xác nhận phiếu nhập - cập nhật tồn kho sản phẩm */
    public void xacNhan() {
        if (this.trangThai != TrangThaiPhieuNhapKho.CHO_DUYET) {
            throw new IllegalStateException("Chỉ xác nhận được phiếu đang chờ duyệt!");
        }
        for (ChiTietPhieuNhapKho ct : chiTietList) {
            ct.getMaSanPham().nhapKho(ct.getSoLuong());
        }
        this.trangThai = TrangThaiPhieuNhapKho.DA_XAC_NHAN;
    }

    public void huyPhieu() {
        if (this.trangThai == TrangThaiPhieuNhapKho.DA_XAC_NHAN) {
            throw new IllegalStateException("Không thể hủy phiếu đã xác nhận!");
        }
        this.trangThai = TrangThaiPhieuNhapKho.DA_HUY;
    }

    @Override
    public String toString() { return maPhieuNhapKho; }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    public ChiTietPhieuNhapKho taoChiTiet(SanPham sp, int soLuong, double donGia) {
        ChiTietPhieuNhapKho ct = new ChiTietPhieuNhapKho(maPhieuNhapKho + "-" + sp.getMaSanPham(), this, sp, soLuong, donGia);
        themChiTiet(ct);
        return ct;
    }
}
