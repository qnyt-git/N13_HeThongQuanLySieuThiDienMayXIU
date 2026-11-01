package DAO;

import ENTITY.NhanVien;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO của NhanVien. Cột gioiTinh, trinhDoHocVan chưa có trong entity nên thêm mới để NULL,
 * khi sửa thì không đụng tới hai cột này.
 */
public class NhanVienDAO implements IDAO<NhanVien> {

    private static NhanVien mapNhanVien(ResultSet rs) throws SQLException {
        java.sql.Date ns = rs.getDate("ngaySinh");
        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("hoTenNhanVien"),
                rs.getBoolean("gioiTinh"),
                rs.getString("chucVu"),
                rs.getString("soDienThoai"),
                ns == null ? null : ns.toLocalDate(),
                rs.getString("trinhDoHocVan"),
                rs.getFloat("heSoLuong"),
                rs.getInt("luongCoBan"),
                rs.getBoolean("trangThai"));
    }

    private static java.sql.Date toSqlDate(NhanVien n) {
        return n.getNgaySinh() == null ? null : java.sql.Date.valueOf(n.getNgaySinh());
    }

    @Override
    public List<NhanVien> getAll() {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT maNhanVien, hoTenNhanVien, gioiTinh, chucVu, soDienThoai, ngaySinh, trinhDoHocVan, heSoLuong, luongCoBan, trangThai "
                + "FROM NhanVien ORDER BY maNhanVien";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(mapNhanVien(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Tìm nhân viên theo mã; null nếu không có. */
    public NhanVien timTheoMa(String maNhanVien) throws SQLException {
        String sql = "SELECT maNhanVien, hoTenNhanVien, gioiTinh, chucVu, soDienThoai, ngaySinh, trinhDoHocVan, heSoLuong, luongCoBan, trangThai "
                + "FROM NhanVien WHERE maNhanVien = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapNhanVien(rs);
                }
                return null;
            }
        }
    }

    @Override
    public boolean them(NhanVien n) {
        String sql = "INSERT INTO NhanVien(maNhanVien, hoTenNhanVien, gioiTinh, chucVu, soDienThoai, ngaySinh, trinhDoHocVan, heSoLuong, "
                + "luongCoBan, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getMaNhanVien());
            ps.setString(2, n.getHoTenNhanVien());
            ps.setBoolean(3, n.isGioiTinh());
            ps.setString(4, n.getChucVu());
            ps.setString(5, n.getSoDienThoai());
            ps.setDate(6, toSqlDate(n));
            ps.setString(7, n.getTrinhDoHocVan());
            ps.setDouble(8, n.getHeSoLuong());
            ps.setInt(9, n.getLuongCoBan());
            ps.setBoolean(10, n.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(NhanVien n) {
        String sql = "UPDATE NhanVien SET hoTenNhanVien = ?, gioiTinh = ?, chucVu = ?, soDienThoai = ?, ngaySinh = ?, "
                + "trinhDoHocVan = ?, heSoLuong = ?, luongCoBan = ?, trangThai = ? WHERE maNhanVien = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
        	ps.setString(1, n.getHoTenNhanVien());
            ps.setBoolean(2, n.isGioiTinh());
            ps.setString(3, n.getChucVu());
            ps.setString(4, n.getSoDienThoai());
            ps.setDate(5, toSqlDate(n));
            ps.setString(6, n.getTrinhDoHocVan());
            ps.setDouble(7, n.getHeSoLuong());
            ps.setInt(8, n.getLuongCoBan());
            ps.setBoolean(9, n.isTrangThai());
            ps.setString(10, n.getMaNhanVien());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(NhanVien n) {
        String sql = "DELETE FROM NhanVien WHERE maNhanVien = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getMaNhanVien());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maNhanVien, 3, 10) AS INT)) FROM NhanVien";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("NV%03d", max + 1);
    }
}
