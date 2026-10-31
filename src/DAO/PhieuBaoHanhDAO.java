package DAO;

import ENTITY.ChiTietPhieuBaoHanh;
import ENTITY.KhachHang;
import ENTITY.NhanVien;
import ENTITY.PhieuBaoHanh;
import ENTITY.SanPham;
import ENUM.TrangThaiPhieuBaoHanh;

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
 * DAO của PhieuBaoHanh (+ ChiTietPhieuBaoHanh). Trạng thái "Tiếp nhận" trong SQL được xem như "Đang xử lý".
 * them() lưu phiếu + chi tiết trong một giao dịch; capNhat() chỉ cập nhật phần đầu phiếu.
 */
public class PhieuBaoHanhDAO implements IDAO<PhieuBaoHanh> {

    private static TrangThaiPhieuBaoHanh docTrangThai(String s) {
        String x = s == null ? "" : s.trim();
        for (TrangThaiPhieuBaoHanh t : TrangThaiPhieuBaoHanh.values()) {
            if (t.name().equalsIgnoreCase(x) || t.toString().equalsIgnoreCase(x)) return t;
        }
        return TrangThaiPhieuBaoHanh.DANG_XU_LY;
    }

    private static LocalDateTime toLdt(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }

    private static Timestamp toTs(LocalDateTime t) {
        return t == null ? null : Timestamp.valueOf(t);
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    @Override
    public List<PhieuBaoHanh> getAll() {
        List<PhieuBaoHanh> ds = new ArrayList<>();

        Map<String, KhachHang> kh = new HashMap<>();
        for (KhachHang k : new KhachHangDAO().getAll()) kh.put(k.getMaKhachHang(), k);
        Map<String, NhanVien> nv = new HashMap<>();
        for (NhanVien n : new NhanVienDAO().getAll()) nv.put(n.getMaNhanVien(), n);
        Map<String, SanPham> sp = new HashMap<>();
        for (SanPham s : new SanPhamDAO().getAll()) sp.put(s.getMaSanPham(), s);

        String sql = "SELECT maPhieubaoHanh, maKhachHang, maNhanVien, ngayTiepNhan, ngayHenTra, trangThai, ghiChu "
                + "FROM PhieuBaoHanh ORDER BY ngayTiepNhan, maPhieubaoHanh";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(new PhieuBaoHanh(
                        rs.getString("maPhieubaoHanh"),
                        kh.get(rs.getString("maKhachHang")),
                        nv.get(rs.getString("maNhanVien")),
                        toLdt(rs.getTimestamp("ngayTiepNhan")),
                        toLdt(rs.getTimestamp("ngayHenTra")),
                        docTrangThai(rs.getString("trangThai")),
                        rs.getString("ghiChu")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, PhieuBaoHanh> idx = new HashMap<>();
        for (PhieuBaoHanh p : ds) idx.put(p.getMaPhieubaoHanh(), p);

        String sqlCt = "SELECT maChiTietPBH, maPhieuBaoHanh, maSanPham, tinhTrangSanPham, noiDungBaoHanh "
                + "FROM ChiTietPhieuBaoHanh ORDER BY maChiTietPBH";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlCt);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhieuBaoHanh p = idx.get(rs.getString("maPhieuBaoHanh"));
                SanPham s = sp.get(rs.getString("maSanPham"));
                if (p != null && s != null) {
                    p.themChiTiet(new ChiTietPhieuBaoHanh(
                            rs.getString("maChiTietPBH"), p, s,
                            rs.getString("tinhTrangSanPham"), rs.getString("noiDungBaoHanh")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    private boolean themPhieu(Connection con, PhieuBaoHanh p) {
        String sql = "INSERT INTO PhieuBaoHanh(maPhieubaoHanh, maKhachHang, maNhanVien, ngayTiepNhan, ngayHenTra, "
                + "trangThai, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getMaPhieubaoHanh());
            ps.setString(2, p.getMaKhachHang().getMaKhachHang());
            ps.setString(3, p.getMaNhanVien().getMaNhanVien());
            ps.setTimestamp(4, toTs(p.getNgayTiepNhan()));
            ps.setTimestamp(5, toTs(p.getNgayHenTra()));
            ps.setString(6, p.getTrangThai().toString());
            ps.setString(7, p.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Mã dòng chi tiết do entity tạo dài hơn VARCHAR(10), nên DAO cấp mã ngắn CTPBH00001... */
    private int soChiTietLonNhat(Connection con) throws Exception {
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maChiTietPBH, 6, 10) AS INT)) FROM ChiTietPhieuBaoHanh";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private boolean themChiTiet(Connection con, PhieuBaoHanh p) {
        String sql = "INSERT INTO ChiTietPhieuBaoHanh(maChiTietPBH, maPhieuBaoHanh, maSanPham, tinhTrangSanPham, "
                + "noiDungBaoHanh) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int so = soChiTietLonNhat(con);
            for (ChiTietPhieuBaoHanh ct : p.getChiTietList()) {
                ct.setMaChiTietPBH(String.format("CTPBH%05d", ++so));
                ps.setString(1, ct.getMaChiTietPBH());
                ps.setString(2, p.getMaPhieubaoHanh());
                ps.setString(3, ct.getMaSanPham().getMaSanPham());
                ps.setString(4, nz(ct.getTinhTrangSanPham()));
                ps.setString(5, nz(ct.getNoiDungBaoHanh()));
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean them(PhieuBaoHanh p) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            if (!themPhieu(con, p)) {
                con.rollback();
                return false;
            }
            if (!themChiTiet(con, p)) {
                con.rollback();
                return false;
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

    /** Chỉ cập nhật phần đầu phiếu (ngày hẹn trả, trạng thái, ghi chú). */
    @Override
    public boolean capNhat(PhieuBaoHanh p) {
        String sql = "UPDATE PhieuBaoHanh SET ngayHenTra = ?, trangThai = ?, ghiChu = ? WHERE maPhieubaoHanh = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, toTs(p.getNgayHenTra()));
            ps.setString(2, p.getTrangThai().toString());
            ps.setString(3, p.getGhiChu());
            ps.setString(4, p.getMaPhieubaoHanh());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(PhieuBaoHanh p) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM ChiTietPhieuBaoHanh WHERE maPhieuBaoHanh = ?")) {
                ps.setString(1, p.getMaPhieubaoHanh());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM PhieuBaoHanh WHERE maPhieubaoHanh = ?")) {
                ps.setString(1, p.getMaPhieubaoHanh());
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
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maPhieubaoHanh, 4, 10) AS INT)) FROM PhieuBaoHanh";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("PBH%03d", max + 1);
    }
}
