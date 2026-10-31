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
                rs.getString("chucVu"),
                rs.getString("soDienThoai"),
                ns == null ? null : ns.toLocalDate(),
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
        String sql = "SELECT maNhanVien, hoTenNhanVien, chucVu, soDienThoai, ngaySinh, heSoLuong, luongCoBan, trangThai "
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
    public NhanVien timTheoMa(String maNhanVien) {
        String sql = "SELECT maNhanVien, hoTenNhanVien, chucVu, soDienThoai, ngaySinh, heSoLuong, luongCoBan, trangThai "
                + "FROM NhanVien WHERE maNhanVien = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapNhanVien(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean them(NhanVien n) {
        String sql = "INSERT INTO NhanVien(maNhanVien, hoTenNhanVien, chucVu, soDienThoai, ngaySinh, heSoLuong, "
                + "luongCoBan, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getMaNhanVien());
            ps.setString(2, n.getHoTenNhanVien());
            ps.setString(3, n.getChucVu());
            ps.setString(4, n.getSoDienThoai());
            ps.setDate(5, toSqlDate(n));
            ps.setDouble(6, n.getHeSoLuong());
            ps.setInt(7, n.getLuongCoBan());
            ps.setBoolean(8, n.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(NhanVien n) {
        String sql = "UPDATE NhanVien SET hoTenNhanVien = ?, chucVu = ?, soDienThoai = ?, ngaySinh = ?, "
                + "heSoLuong = ?, luongCoBan = ?, trangThai = ? WHERE maNhanVien = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getHoTenNhanVien());
            ps.setString(2, n.getChucVu());
            ps.setString(3, n.getSoDienThoai());
            ps.setDate(4, toSqlDate(n));
            ps.setDouble(5, n.getHeSoLuong());
            ps.setInt(6, n.getLuongCoBan());
            ps.setBoolean(7, n.isTrangThai());
            ps.setString(8, n.getMaNhanVien());
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
