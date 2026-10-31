package DAO;

import ENTITY.SanPham;
import ENUM.TrangThaiSanPham;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO của SanPham. SQL lưu thoiHanBaoHanh là số tháng (INT) và trangThai là BIT, còn entity dùng chuỗi "24 tháng"
 * và enum 4 giá trị, nên quy đổi như sau:
 *   đọc : bit = 0 -> VO_HIEU_HOA ("Ngừng bán"); bit = 1 -> CON_BAN nếu còn tồn, HET_HANG nếu tồn = 0
 *   ghi : VO_HIEU_HOA -> 0; các trạng thái còn lại -> 1
 */
public class SanPhamDAO implements IDAO<SanPham> {

    private static SanPham mapSanPham(ResultSet rs) throws SQLException {
        int ton = rs.getInt("soLuongTon");
        TrangThaiSanPham tt;
        if (!rs.getBoolean("trangThai")) tt = TrangThaiSanPham.VO_HIEU_HOA;
        else if (ton > 0) tt = TrangThaiSanPham.CON_BAN;
        else tt = TrangThaiSanPham.HET_HANG;

        SanPham sp = new SanPham(
                rs.getString("maSanPham"),
                rs.getString("tenSanPham"),
                rs.getString("maVach"),
                rs.getDouble("giaBan"),
                ton,
                null,
                tt);
        sp.datBaoHanhThang(rs.getInt("thoiHanBaoHanh"));
        return sp;
    }

    private static boolean dangKinhDoanh(SanPham s) {
        return s.getTrangThai() != TrangThaiSanPham.VO_HIEU_HOA;
    }

    @Override
    public List<SanPham> getAll() {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT maSanPham, tenSanPham, maVach, giaBan, soLuongTon, thoiHanBaoHanh, trangThai "
                + "FROM SanPham ORDER BY maSanPham";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(mapSanPham(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public boolean them(SanPham s) {
        String sql = "INSERT INTO SanPham(maSanPham, tenSanPham, maVach, giaBan, soLuongTon, thoiHanBaoHanh, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getMaSanPham());
            ps.setString(2, s.getTenSanPham());
            ps.setString(3, s.getMaVach());
            ps.setDouble(4, s.getGiaBan());
            ps.setInt(5, s.getSoLuongTon());
            ps.setInt(6, s.baoHanhThang());
            ps.setBoolean(7, dangKinhDoanh(s));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(SanPham s) {
        String sql = "UPDATE SanPham SET tenSanPham = ?, maVach = ?, giaBan = ?, soLuongTon = ?, "
                + "thoiHanBaoHanh = ?, trangThai = ? WHERE maSanPham = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getTenSanPham());
            ps.setString(2, s.getMaVach());
            ps.setDouble(3, s.getGiaBan());
            ps.setInt(4, s.getSoLuongTon());
            ps.setInt(5, s.baoHanhThang());
            ps.setBoolean(6, dangKinhDoanh(s));
            ps.setString(7, s.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(SanPham s) {
        String sql = "DELETE FROM SanPham WHERE maSanPham = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maSanPham, 3, 10) AS INT)) FROM SanPham";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("SP%03d", max + 1);
    }
}
