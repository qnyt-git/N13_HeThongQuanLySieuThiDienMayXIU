package DAO;

import ENTITY.ChiTietPhieuDatHang;
import ENTITY.KhachHang;
import ENTITY.NhanVien;
import ENTITY.PhieuDatHang;
import ENTITY.SanPham;
import ENUM.TrangThaiPhieuDatHang;

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
 * DAO của PhieuDatHang (+ ChiTietPhieuDatHang). SQL có 4 trạng thái (Chờ xử lý / Đã xác nhận / Đã giao / Đã hủy),
 * entity chỉ có cờ boolean "đã xác nhận":
 *   đọc : "Đã xác nhận" và "Đã giao" -> true; "Chờ xử lý" -> false; phiếu "Đã hủy" không được nạp lên.
 *   ghi : true -> "Đã xác nhận" (nếu phiếu đang "Đã giao" thì giữ nguyên); false -> "Chờ xử lý".
 *   xoa(): không xóa dòng mà đổi sang "Đã hủy" để giữ lịch sử và không vướng khóa ngoại.
 * "Đã giao" do HoaDonDAO đặt khi lập hóa đơn từ phiếu này.
 */
public class PhieuDatHangDAO implements IDAO<PhieuDatHang> {

    private static LocalDateTime toLdt(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }

    private static Timestamp toTs(LocalDateTime t) {
        return t == null ? null : Timestamp.valueOf(t);
    }

    private static TrangThaiPhieuDatHang docTrangThai(String s) {
        String x = s == null ? "" : s.trim();

        for (TrangThaiPhieuDatHang t : TrangThaiPhieuDatHang.values()) {
            if (t.toString().equalsIgnoreCase(x)
                    || t.name().equalsIgnoreCase(x)) {
                return t;
            }
        }

        return TrangThaiPhieuDatHang.CHO_XU_LY;
    }
    
    @Override
    public List<PhieuDatHang> getAll() {
        Map<String, KhachHang> kh = new HashMap<>();
        for (KhachHang k : new KhachHangDAO().getAll()) kh.put(k.getMaKhachHang(), k);
        Map<String, NhanVien> nv = new HashMap<>();
        for (NhanVien n : new NhanVienDAO().getAll()) nv.put(n.getMaNhanVien(), n);
        Map<String, SanPham> sp = new HashMap<>();
        for (SanPham s : new SanPhamDAO().getAll()) sp.put(s.getMaSanPham(), s);
        return getAll(kh, nv, sp);
    }

    /** Nạp phiếu đặt hàng khi đã có sẵn các bảng tra cứu (để HoaDonDAO không phải truy vấn lặp lại). */
    List<PhieuDatHang> getAll(Map<String, KhachHang> kh, Map<String, NhanVien> nv, Map<String, SanPham> sp) {
        List<PhieuDatHang> ds = new ArrayList<>();
        String sql = "SELECT maPhieuDatHang, maKhachHang, maNhanVien, ngayDatHang, trangThai, ghiChu "
                + "FROM PhieuDatHang WHERE trangThai <> N'Đã hủy' ORDER BY maPhieuDatHang";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

        	while (rs.next()) {
        	    ds.add(new PhieuDatHang(
        	            rs.getString("maPhieuDatHang"),
        	            kh.get(rs.getString("maKhachHang")),
        	            nv.get(rs.getString("maNhanVien")),
        	            toLdt(rs.getTimestamp("ngayDatHang")),
        	            docTrangThai(rs.getString("trangThai")),
        	            rs.getString("ghiChu")
        	    ));
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, PhieuDatHang> idx = new HashMap<>();
        for (PhieuDatHang p : ds) idx.put(p.getMaPhieuDatHang(), p);

        String sqlCt = "SELECT maPhieuDatHang, maSanPham, soLuong, donGia "
                + "FROM ChiTietPhieuDatHang ORDER BY maPhieuDatHang, maSanPham";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlCt);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhieuDatHang p = idx.get(rs.getString("maPhieuDatHang"));
                SanPham s = sp.get(rs.getString("maSanPham"));
                if (p != null && s != null) {
                    p.themChiTiet(new ChiTietPhieuDatHang(
                            p, s,
                            rs.getInt("soLuong"), rs.getDouble("donGia")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    private boolean themPhieu(Connection con, PhieuDatHang p) {
    	String sql = "INSERT INTO PhieuDatHang(maPhieuDatHang, maKhachHang, maNhanVien, ngayDatHang, trangThai, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getMaPhieuDatHang());
            ps.setString(2, p.getMaKhachHang().getMaKhachHang());
            ps.setString(3, p.getMaNhanVien().getMaNhanVien());
            ps.setTimestamp(4, toTs(p.getNgayDatHang()));
            ps.setString(5, p.daXacNhan() ? "Đã xác nhận" : "Chờ xử lý");
            ps.setString(6, p.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean themChiTiet(Connection con, PhieuDatHang p) {
        String sql = "INSERT INTO ChiTietPhieuDatHang(maPhieuDatHang, maSanPham, soLuong, donGia) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (ChiTietPhieuDatHang ct : p.getChiTietList()) {
                ps.setString(1, p.getMaPhieuDatHang());
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
    public boolean them(PhieuDatHang p) {
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

    /** Chỉ cập nhật phần đầu phiếu (trạng thái, ghi chú); dòng chi tiết không đổi sau khi lập. */
    @Override
    public boolean capNhat(PhieuDatHang p) {
        String sql = "UPDATE PhieuDatHang SET ghiChu = ?, trangThai = CASE WHEN ? = 1 THEN "
                + "(CASE WHEN trangThai = N'Đã hoàn thành' THEN trangThai ELSE N'Đã xác nhận' END) ELSE N'Chờ xử lý' END "
                + "WHERE maPhieuDatHang = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getGhiChu());
            ps.setInt(2, p.daXacNhan() ? 1 : 0);
            ps.setString(3, p.getMaPhieuDatHang());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(PhieuDatHang p) {
        String sql = "UPDATE PhieuDatHang SET trangThai = N'Đã hủy' WHERE maPhieuDatHang = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getMaPhieuDatHang());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maPhieuDatHang, 4, 10) AS INT)) FROM PhieuDatHang";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("PDH%03d", max + 1);
    }
}
