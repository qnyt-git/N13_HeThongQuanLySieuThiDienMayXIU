package DAO;

import ENTITY.KhachHang;
import ENUM.TrangThaiKhachHang;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** DAO của KhachHang. Cột trangThai lưu tên hằng enum (KH_MOI, TOT, NOXAU, DANHSACHDEN). */
public class KhachHangDAO implements IDAO<KhachHang> {

    private static TrangThaiKhachHang docTrangThai(String s) {
        String x = s == null ? "" : s.trim();
        for (TrangThaiKhachHang t : TrangThaiKhachHang.values()) {
            if (t.name().equalsIgnoreCase(x) || t.toString().equalsIgnoreCase(x)) return t;
        }
        return TrangThaiKhachHang.KH_MOI;
    }

    @Override
    public List<KhachHang> getAll() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT maKhachHang, hoTenKhachHang, soDienThoai, diaChi, diemTichLuy, trangThai "
                + "FROM KhachHang ORDER BY maKhachHang";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("hoTenKhachHang"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getInt("diemTichLuy"),
                        docTrangThai(rs.getString("trangThai"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public boolean them(KhachHang k) {
        String sql = "INSERT INTO KhachHang(maKhachHang, hoTenKhachHang, soDienThoai, diaChi, diemTichLuy, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getMaKhachHang());
            ps.setString(2, k.getHoTenKhachHang());
            ps.setString(3, k.getSoDienThoai());
            ps.setString(4, k.getDiaChi());
            ps.setInt(5, k.getDiemTichLuy());
            ps.setString(6, k.getTrangThai().name());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(KhachHang k) {
        String sql = "UPDATE KhachHang SET hoTenKhachHang = ?, soDienThoai = ?, diaChi = ?, diemTichLuy = ?, "
                + "trangThai = ? WHERE maKhachHang = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getHoTenKhachHang());
            ps.setString(2, k.getSoDienThoai());
            ps.setString(3, k.getDiaChi());
            ps.setInt(4, k.getDiemTichLuy());
            ps.setString(5, k.getTrangThai().name());
            ps.setString(6, k.getMaKhachHang());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(KhachHang k) {
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getMaKhachHang());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maKhachHang, 3, 10) AS INT)) FROM KhachHang";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("KH%03d", max + 1);
    }
}
