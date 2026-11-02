package DAO;

import ENTITY.ChiTietHoaDon;
import ENTITY.HoaDon;
import ENTITY.KhachHang;
import ENTITY.KhuyenMai;
import ENTITY.NhanVien;
import ENTITY.PhieuDatHang;
import ENTITY.SanPham;
import ENUM.PhuongThucThanhToan;
import ENUM.TrangThaiHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO của HoaDon (+ ChiTietHoaDon).
 * them(): lưu hóa đơn + chi tiết trong một giao dịch; nếu lập từ phiếu đặt hàng thì phiếu chuyển sang "Đã giao".
 * capNhat(): lưu trạng thái / lý do hủy; hủy hóa đơn thì phiếu đặt hàng gắn kèm quay về "Đã xác nhận".
 * Tồn kho và điểm tích lũy do màn hình gọi SanPhamDAO / KhachHangDAO.capNhat riêng.
 */
public class HoaDonDAO implements IDAO<HoaDon> {

    private static PhuongThucThanhToan docPhuongThuc(String s) {
        String x = s == null ? "" : s.trim();
        for (PhuongThucThanhToan t : PhuongThucThanhToan.values()) {
            if (t.name().equalsIgnoreCase(x) || t.toString().equalsIgnoreCase(x)) return t;
        }
        return PhuongThucThanhToan.TIEN_MAT;
    }

    private static TrangThaiHoaDon docTrangThai(String s) {
        String x = s == null ? "" : s.trim();
        for (TrangThaiHoaDon t : TrangThaiHoaDon.values()) {
            if (t.name().equalsIgnoreCase(x) || t.toString().equalsIgnoreCase(x)) return t;
        }
        return TrangThaiHoaDon.DA_THANH_TOAN;
    }

    private static LocalDateTime toLdt(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }

    private static Timestamp toTs(LocalDateTime t) {
        return t == null ? null : Timestamp.valueOf(t);
    }

    private static String maPhieu(PhieuDatHang p) {
        return p == null ? null : p.getMaPhieuDatHang();
    }

    private static String maKM(KhuyenMai k) {
        return k == null ? null : k.getMaKhuyenMai();
    }

    // ===================== ĐỌC =====================

    @Override
    public List<HoaDon> getAll() {
        return docHoaDon(null, null);
    }

    /** Hóa đơn (chưa hủy) đã lập từ phiếu đặt hàng này; null nếu chưa có. */
    public HoaDon timTheoPhieuDat(PhieuDatHang p) {
        if (p == null) return null;
        List<HoaDon> ds = docHoaDon("WHERE maPhieuDatHang = ? AND trangThai <> N'Đã hủy'", p.getMaPhieuDatHang());
        return ds.isEmpty() ? null : ds.get(0);
    }

    /**
     * Đọc hóa đơn (kèm chi tiết). dieuKien = null thì lấy tất cả; ngược lại là mệnh đề WHERE có đúng một tham số.
     */
    private List<HoaDon> docHoaDon(String dieuKien, String thamSo) {
        List<HoaDon> ds = new ArrayList<>();
        String loc = dieuKien == null ? "" : dieuKien + " ";

        // không có dòng nào khớp thì khỏi nạp các bảng tra cứu
        if (dieuKien != null) {
            String sqlKiemTra = "SELECT 1 FROM HoaDon " + dieuKien;
            try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlKiemTra)) {
                ps.setString(1, thamSo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return ds;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ds;
            }
        }

        Map<String, KhachHang> kh = new HashMap<>();
        for (KhachHang k : new KhachHangDAO().getAll()) kh.put(k.getMaKhachHang(), k);
        Map<String, NhanVien> nv = new HashMap<>();
        for (NhanVien n : new NhanVienDAO().getAll()) nv.put(n.getMaNhanVien(), n);
        Map<String, KhuyenMai> km = new HashMap<>();
        for (KhuyenMai k : new KhuyenMaiDAO().getAll()) km.put(k.getMaKhuyenMai(), k);
        Map<String, SanPham> sp = new HashMap<>();
        for (SanPham s : new SanPhamDAO().getAll()) sp.put(s.getMaSanPham(), s);
        Map<String, PhieuDatHang> pdh = new HashMap<>();
        for (PhieuDatHang p : new PhieuDatHangDAO().getAll(kh, nv, sp)) pdh.put(p.getMaPhieuDatHang(), p);

        String sql = "SELECT maHoaDon, maPhieuDatHang, maKhachHang, maNhanVien, maKhuyenMai, phuongThucThanhToan, "
                + "ngayLap, trangThai, lyDoHuyHoaDon, ghiChu FROM HoaDon " + loc + "ORDER BY ngayLap, maHoaDon";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            if (dieuKien != null) ps.setString(1, thamSo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new HoaDon(
                            rs.getString("maHoaDon"),
                            pdh.get(rs.getString("maPhieuDatHang")),
                            kh.get(rs.getString("maKhachHang")),
                            nv.get(rs.getString("maNhanVien")),
                            km.get(rs.getString("maKhuyenMai")),
                            docPhuongThuc(rs.getString("phuongThucThanhToan")),
                            toLdt(rs.getTimestamp("ngayLap")),
                            docTrangThai(rs.getString("trangThai")),
                            rs.getString("lyDoHuyHoaDon"),
                            rs.getString("ghiChu")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, HoaDon> idx = new HashMap<>();
        for (HoaDon h : ds) idx.put(h.getMaHoaDon(), h);

        String sqlCt = "SELECT maHoaDon, maSanPham, soLuong, donGia FROM ChiTietHoaDon ";
        if (dieuKien != null) {
            sqlCt += "WHERE maHoaDon IN (SELECT maHoaDon FROM HoaDon " + dieuKien + ") ";
        }

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlCt)) {
            if (dieuKien != null) ps.setString(1, thamSo);
            try (ResultSet rs = ps.executeQuery()) {
            	while (rs.next()) {
            	    HoaDon h = idx.get(rs.getString("maHoaDon"));
            	    SanPham s = sp.get(rs.getString("maSanPham"));

            	    if (h != null && s != null) {
            	        h.themChiTiet(new ChiTietHoaDon(
            	                h,
            	                s,
            	                rs.getInt("soLuong"),
            	                rs.getDouble("donGia")
            	        ));
            	    }
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ===================== THÊM (giao dịch) =====================

    private boolean themHoaDon(Connection con, HoaDon h) {
        String sql = "INSERT INTO HoaDon(maHoaDon, maPhieuDatHang, maKhachHang, maNhanVien, maKhuyenMai, "
                + "phuongThucThanhToan, ngayLap, trangThai, lyDoHuyHoaDon, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, h.getMaHoaDon());
            ps.setString(2, maPhieu(h.getMaPhieuDatHang()));
            ps.setString(3, h.getMaKhachHang().getMaKhachHang());
            ps.setString(4, h.getMaNhanVien().getMaNhanVien());
            ps.setString(5, maKM(h.getMaKhuyenMai()));
            ps.setString(6, h.getPhuongThucThanhToan().toString());
            ps.setTimestamp(7, toTs(h.getNgayLap()));
            ps.setString(8, h.getTrangThai().toString());
            ps.setString(9, h.getLyDoHuyHoaDon());
            ps.setString(10, h.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean themChiTiet(Connection con, HoaDon h) {
        String sql = "INSERT INTO ChiTietHoaDon(maHoaDon, maSanPham, soLuong, donGia) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (ChiTietHoaDon ct : h.getChiTietList()) {
                ps.setString(1, h.getMaHoaDon());
                ps.setString(2, ct.getMaSanPham().getMaSanPham());
                ps.setInt(3, ct.getSoLuong());
                ps.setDouble(4, ct.getDonGia());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean capNhatPhieuDatHang(Connection con, String maPhieuDatHang, String trangThaiMoi, String dieuKienTrangThai) {
        String sql = "UPDATE PhieuDatHang SET trangThai = ? WHERE maPhieuDatHang = ? AND " + dieuKienTrangThai;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setString(2, maPhieuDatHang);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean them(HoaDon h) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            if (!themHoaDon(con, h)) {
                con.rollback();
                return false;
            }
            if (!themChiTiet(con, h)) {
                con.rollback();
                return false;
            }
            if (h.getMaPhieuDatHang() != null) {
                if (!capNhatPhieuDatHang(con, maPhieu(h.getMaPhieuDatHang()), "Đã giao", "trangThai <> N'Đã hủy'")) {
                    con.rollback();
                    return false;
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ===================== CẬP NHẬT / XÓA =====================

    @Override
    public boolean capNhat(HoaDon h) {
        Connection con = null;
        String sql = "UPDATE HoaDon SET phuongThucThanhToan = ?, trangThai = ?, lyDoHuyHoaDon = ?, ghiChu = ? "
                + "WHERE maHoaDon = ?";

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, h.getPhuongThucThanhToan().toString());
                ps.setString(2, h.getTrangThai().toString());
                ps.setString(3, h.getLyDoHuyHoaDon());
                ps.setString(4, h.getGhiChu());
                ps.setString(5, h.getMaHoaDon());
                if (ps.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }
            }

            // hủy hóa đơn: phiếu đặt hàng gắn kèm quay về "Đã xác nhận"
            if (h.getTrangThai() == TrangThaiHoaDon.DA_HUY && h.getMaPhieuDatHang() != null) {
                if (!capNhatPhieuDatHang(con, maPhieu(h.getMaPhieuDatHang()), "Đã xác nhận", "trangThai = N'Đã giao'")) {
                    con.rollback();
                    return false;
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean xoa(HoaDon h) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM ChiTietHoaDon WHERE maHoaDon = ?")) {
                ps.setString(1, h.getMaHoaDon());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM HoaDon WHERE maHoaDon = ?")) {
                ps.setString(1, h.getMaHoaDon());
                if (ps.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maHoaDon, 3, 10) AS INT)) FROM HoaDon";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("HD%03d", max + 1);
    }
}
