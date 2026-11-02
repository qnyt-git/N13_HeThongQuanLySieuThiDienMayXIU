package DAO;

import ENTITY.ChiTietPhieuNhapKho;
import ENTITY.NhaCungCap;
import ENTITY.NhanVien;
import ENTITY.PhieuNhapKho;
import ENTITY.SanPham;
import ENUM.TrangThaiPhieuNhapKho;

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
 * DAO của PhieuNhapKho (+ ChiTietPhieuNhapKho). them() lưu phiếu + chi tiết trong một giao dịch;
 * capNhat() chỉ cập nhật phần đầu phiếu. Tồn kho do màn hình gọi SanPhamDAO.capNhat sau khi xác nhận phiếu.
 */
public class PhieuNhapKhoDAO implements IDAO<PhieuNhapKho> {

    private static TrangThaiPhieuNhapKho docTrangThai(String s) {
        String x = s == null ? "" : s.trim();
        for (TrangThaiPhieuNhapKho t : TrangThaiPhieuNhapKho.values()) {
            if (t.name().equalsIgnoreCase(x) || t.toString().equalsIgnoreCase(x)) return t;
        }
        return TrangThaiPhieuNhapKho.CHO_DUYET;
    }

    private static LocalDateTime toLdt(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }

    private static Timestamp toTs(LocalDateTime t) {
        return t == null ? null : Timestamp.valueOf(t);
    }

    @Override
    public List<PhieuNhapKho> getAll() {
        List<PhieuNhapKho> ds = new ArrayList<>();

        Map<String, NhanVien> nv = new HashMap<>();
        for (NhanVien n : new NhanVienDAO().getAll()) nv.put(n.getMaNhanVien(), n);
        Map<String, NhaCungCap> ncc = new HashMap<>();
        for (NhaCungCap n : new NhaCungCapDAO().getAll()) ncc.put(n.getMaNCC(), n);
        Map<String, SanPham> sp = new HashMap<>();
        for (SanPham s : new SanPhamDAO().getAll()) sp.put(s.getMaSanPham(), s);

        String sql = "SELECT maPhieuNhapKho, ngayNhap, maNhanVien, maNCC, lyDoNhap, trangThai "
                + "FROM PhieuNhapKho ORDER BY ngayNhap, maPhieuNhapKho";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
            	ds.add(new PhieuNhapKho(
            	        rs.getString("maPhieuNhapKho"),
            	        toLdt(rs.getTimestamp("ngayNhap")),
            	        nv.get(rs.getString("maNhanVien")),
            	        ncc.get(rs.getString("maNCC")),
            	        rs.getString("lyDoNhap"),
            	        docTrangThai(rs.getString("trangThai"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, PhieuNhapKho> idx = new HashMap<>();
        for (PhieuNhapKho p : ds) idx.put(p.getMaPhieuNhapKho(), p);

        String sqlCt = "SELECT maPhieuNhapKho, maSanPham, soLuong, donGia "
                + "FROM ChiTietPhieuNhapKho ORDER BY maPhieuNhapKho, maSanPham";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlCt);
             ResultSet rs = ps.executeQuery()) {

        	while (rs.next()) {
        	    PhieuNhapKho p = idx.get(rs.getString("maPhieuNhapKho"));
        	    SanPham s = sp.get(rs.getString("maSanPham"));

        	    if (p != null && s != null) {
        	        p.themChiTiet(new ChiTietPhieuNhapKho(
        	                p, s,
        	                rs.getInt("soLuong"),
        	                rs.getDouble("donGia")));
        	    }
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    private boolean themPhieu(Connection con, PhieuNhapKho p) {
        String sql = "INSERT INTO PhieuNhapKho(maPhieuNhapKho, ngayNhap, maNhanVien, maNCC, lyDoNhap, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getMaPhieuNhapKho());
            ps.setTimestamp(2, toTs(p.getNgayNhap()));
            ps.setString(3, p.getMaNhanVien().getMaNhanVien());
            ps.setString(4, p.getMaNCC().getMaNCC());
            ps.setString(5, p.getLyDoNhap());
            ps.setString(6, p.getTrangThai().toString());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Mã dòng chi tiết do entity tạo ("PNK001-SP001") dài hơn VARCHAR(10), nên DAO cấp mã ngắn CTPNK00001... */

    private boolean themChiTiet(Connection con, PhieuNhapKho p) {
        String sql = "INSERT INTO ChiTietPhieuNhapKho("
                + "maPhieuNhapKho, maSanPham, soLuong, donGia) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (ChiTietPhieuNhapKho ct : p.getChiTietList()) {
                ps.setString(1, p.getMaPhieuNhapKho());
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

    @Override
    public boolean them(PhieuNhapKho p) {
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

    /** Chỉ cập nhật phần đầu phiếu (trạng thái, lý do, ghi chú). */
    @Override
    public boolean capNhat(PhieuNhapKho p) {
        String sql = "UPDATE PhieuNhapKho SET lyDoNhap = ?, trangThai = ?WHERE maPhieuNhapKho = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getLyDoNhap());
            ps.setString(2, p.getTrangThai().toString());
            ps.setString(3, p.getMaPhieuNhapKho());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(PhieuNhapKho p) {
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM ChiTietPhieuNhapKho WHERE maPhieuNhapKho = ?")) {
                ps.setString(1, p.getMaPhieuNhapKho());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM PhieuNhapKho WHERE maPhieuNhapKho = ?")) {
                ps.setString(1, p.getMaPhieuNhapKho());
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
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maPhieuNhapKho, 4, 10) AS INT)) FROM PhieuNhapKho";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("PNK%03d", max + 1);
    }
}
