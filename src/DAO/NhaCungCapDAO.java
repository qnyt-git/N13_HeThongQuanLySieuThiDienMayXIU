package DAO;

import ENTITY.NhaCungCap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** DAO của NhaCungCap. */
public class NhaCungCapDAO implements IDAO<NhaCungCap> {

    @Override
    public List<NhaCungCap> getAll() {
        List<NhaCungCap> ds = new ArrayList<>();
        String sql = "SELECT maNCC, tenNCC, soDienThoai, diaChi, email, trangThai FROM NhaCungCap ORDER BY maNCC";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(new NhaCungCap(
                        rs.getString("maNCC"),
                        rs.getString("tenNCC"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("email"),
                        rs.getBoolean("trangThai")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public boolean them(NhaCungCap n) {
        String sql = "INSERT INTO NhaCungCap(maNCC, tenNCC, soDienThoai, diaChi, email, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getMaNCC());
            ps.setString(2, n.getTenNCC());
            ps.setString(3, n.getSoDienThoai());
            ps.setString(4, n.getDiaChi());
            ps.setString(5, n.getEmail());
            ps.setBoolean(6, n.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(NhaCungCap n) {
        String sql = "UPDATE NhaCungCap SET tenNCC = ?, soDienThoai = ?, diaChi = ?, email = ?, trangThai = ? "
                + "WHERE maNCC = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getTenNCC());
            ps.setString(2, n.getSoDienThoai());
            ps.setString(3, n.getDiaChi());
            ps.setString(4, n.getEmail());
            ps.setBoolean(5, n.isTrangThai());
            ps.setString(6, n.getMaNCC());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(NhaCungCap n) {
        String sql = "DELETE FROM NhaCungCap WHERE maNCC = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getMaNCC());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maNCC, 4, 10) AS INT)) FROM NhaCungCap";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("NCC%03d", max + 1);
    }
}
