package ENTITY;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ENUM.PhuongThucThanhToan;
import ENUM.TrangThaiHoaDon;

public class HoaDon {
    private String maHoaDon;
    private PhieuDatHang maPhieuDatHang;
    private KhachHang maKhachHang;
    private NhanVien maNhanVien;
    private KhuyenMai maKhuyenMai;
    private PhuongThucThanhToan phuongThucThanhToan;
    private LocalDateTime ngayLap;
    private TrangThaiHoaDon trangThai;
    private String lyDoHuyHoaDon;
    private String ghiChu;
    private List<ChiTietHoaDon> chiTietList = new ArrayList<>();

    public HoaDon() {}

    public HoaDon(String maHoaDon, PhieuDatHang maPhieuDatHang, KhachHang maKhachHang,
                  NhanVien maNhanVien, KhuyenMai maKhuyenMai,
                  PhuongThucThanhToan phuongThucThanhToan, LocalDateTime ngayLap,
                  TrangThaiHoaDon trangThai, String lyDoHuyHoaDon, String ghiChu) {
        this.maHoaDon = maHoaDon;
        this.maPhieuDatHang = maPhieuDatHang;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.maKhuyenMai = maKhuyenMai;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.ngayLap = ngayLap;
        this.trangThai = trangThai;
        this.lyDoHuyHoaDon = lyDoHuyHoaDon;
        this.ghiChu = ghiChu;
    }

    // Getters/Setters
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public PhieuDatHang getMaPhieuDatHang() { return maPhieuDatHang; }
    public void setMaPhieuDatHang(PhieuDatHang maPhieuDatHang) { this.maPhieuDatHang = maPhieuDatHang; }
    public KhachHang getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(KhachHang maKhachHang) { this.maKhachHang = maKhachHang; }
    public NhanVien getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(NhanVien maNhanVien) { this.maNhanVien = maNhanVien; }
    public KhuyenMai getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(KhuyenMai maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }
    public PhuongThucThanhToan getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(PhuongThucThanhToan pttt) { this.phuongThucThanhToan = pttt; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public TrangThaiHoaDon getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiHoaDon trangThai) { this.trangThai = trangThai; }
    public String getLyDoHuyHoaDon() { return lyDoHuyHoaDon; }
    public void setLyDoHuyHoaDon(String lyDoHuyHoaDon) { this.lyDoHuyHoaDon = lyDoHuyHoaDon; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public List<ChiTietHoaDon> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietHoaDon> chiTietList) { this.chiTietList = chiTietList; }

    // ===== METHOD BỔ SUNG =====
    /** Thêm chi tiết hóa đơn */
    public void themChiTiet(ChiTietHoaDon ct) {
        if (ct == null) return;
        // kiểm tra trùng sản phẩm
        for (ChiTietHoaDon existing : chiTietList) {
            if (existing.getMaSanPham().getMaSanPham()
                .equals(ct.getMaSanPham().getMaSanPham())) {
                existing.setSoLuong(existing.getSoLuong() + ct.getSoLuong());
                return;
            }
        }
        chiTietList.add(ct);
    }

    /** Tính tổng tiền trước giảm giá */
    public double tinhTongTien() {
        double tong = 0;
        for (ChiTietHoaDon ct : chiTietList) {
            tong += ct.getSoLuong() * ct.getDonGia();
        }
        return tong;
    }

    /** Tính tiền sau khi giảm giá */
    public double tinhThanhTien() {
        double tong = tinhTongTien();
        if (maKhuyenMai != null && maKhuyenMai.conHieuLuc()) {
            tong = maKhuyenMai.apDung(tong);
        }
        return tong;
    }

    /** Hủy hóa đơn */
    public void huyHoaDon(String lyDo) {
        if (lyDo == null || lyDo.trim().isEmpty()) {
            throw new IllegalArgumentException("Phải có lý do hủy!");
        }
        this.lyDoHuyHoaDon = lyDo;
        this.trangThai = TrangThaiHoaDon.DA_HUY;
    }

    /** Xác nhận thanh toán */
    public void thanhToan() {
        if (this.trangThai == TrangThaiHoaDon.DA_HUY) {
            throw new IllegalStateException("Hóa đơn đã bị hủy, không thể thanh toán!");
        }
        this.trangThai = TrangThaiHoaDon.DA_THANH_TOAN;
    }

    public boolean daThanhToan() {
        return trangThai == TrangThaiHoaDon.DA_THANH_TOAN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoaDon)) return false;
        return Objects.equals(maHoaDon, ((HoaDon) o).maHoaDon);
    }

    @Override
    public int hashCode() { return Objects.hash(maHoaDon); }

    @Override
    public String toString() { return maHoaDon; }

    // ===== METHOD BỔ SUNG CHO GIAO DIỆN / DAO =====

    /** Tạo và thêm dòng chi tiết (mã dòng = mã hóa đơn + mã sản phẩm). */
    public ChiTietHoaDon taoChiTiet(SanPham sp, int soLuong, double donGia) {
        ChiTietHoaDon ct = new ChiTietHoaDon(this, sp, soLuong, donGia);
        themChiTiet(ct);
        return ct;
    }

    public int tongSoLuong() {
        int n = 0;
        for (ChiTietHoaDon ct : chiTietList) n += ct.getSoLuong();
        return n;
    }

    /** Tiền giảm của hóa đơn (theo % khuyến mãi đã chọn lúc lập). */
    public double tienGiam() {
        return maKhuyenMai == null ? 0 : maKhuyenMai.tinhTienGiamDaApDung(tinhTongTien());
    }

    /** Thành tiền của hóa đơn đã lập (ổn định, không đổi khi khuyến mãi hết hạn). */
    public double thanhTienDaLap() { return tinhTongTien() - tienGiam(); }

    /**
     * Lập hóa đơn: kiểm tra tồn kho, xuất kho toàn bộ sản phẩm, cộng điểm khách hàng, chuyển sang "Đã thanh toán".
     * @return false nếu giỏ rỗng hoặc có sản phẩm không đủ tồn (khi đó không thay đổi gì).
     */
    public boolean lapHoaDon() {
        if (chiTietList.isEmpty() || maKhachHang == null) return false;
        for (ChiTietHoaDon ct : chiTietList)
            if (!ct.getMaSanPham().coTheBanSoLuong(ct.getSoLuong())) return false;
        for (ChiTietHoaDon ct : chiTietList) ct.getMaSanPham().xuatKho(ct.getSoLuong());
        maKhachHang.congDiemTuHoaDon(thanhTienDaLap());
        this.trangThai = TrangThaiHoaDon.DA_THANH_TOAN;
        return true;
    }

    /** Hủy hóa đơn kèm lý do; nếu đã thanh toán thì hoàn lại tồn kho và thu hồi điểm tích lũy. */
    public void huyVaHoanKho(String lyDo) {
        if (this.trangThai == TrangThaiHoaDon.DA_HUY) throw new IllegalStateException("Hóa đơn này đã được hủy!");
        boolean daThanhToan = daThanhToan();
        huyHoaDon(lyDo);
        if (daThanhToan) {
            for (ChiTietHoaDon ct : chiTietList) ct.getMaSanPham().nhapKho(ct.getSoLuong());
            if (maKhachHang != null) maKhachHang.thuHoiDiemTuHoaDon(thanhTienDaLap());
        }
    }
}
