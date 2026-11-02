
package DAO;

import ENTITY.SanPham;
import ENUM.TrangThaiSanPham;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO implements IDAO<SanPham> {

    private static SanPham mapSanPham(ResultSet rs) throws SQLException {
        int ton = rs.getInt("soLuongTon");

        TrangThaiSanPham tt = null;
        // Doc BIT an toan: SQL Server tra ve Boolean, mot so driver/nguon du lieu co the tra Number.
        Object rawTrangThai = rs.getObject("trangThai");
        boolean dangHoatDong = rawTrangThai != null && (
                rawTrangThai instanceof Boolean ? (Boolean) rawTrangThai
                : rawTrangThai instanceof Number ? ((Number) rawTrangThai).intValue() != 0
                : "1".equals(rawTrangThai.toString())
                    || "true".equalsIgnoreCase(rawTrangThai.toString())
                    || "hoạt động".equalsIgnoreCase(rawTrangThai.toString())
        );

        String trangThai = rs.getString("trangThai");

        if ("Đang bán".equals(trangThai)) {
            tt = TrangThaiSanPham.CON_BAN;
        } else if ("Tạm ngưng".equals(trangThai)) {
            tt = TrangThaiSanPham.TAM_NGUNG;
        }else if ("Hết hàng".equals(trangThai)) {
            tt = TrangThaiSanPham.HET_HANG;
        }else if ("Vô hiệu hóa".equals(trangThai)) {
            tt = TrangThaiSanPham.VO_HIEU_HOA;
        }

        SanPham sp = new SanPham(
                rs.getString("maSanPham"),
                rs.getString("tenSanPham"),
                rs.getDouble("giaBan"),
                ton,
                null,
                tt
        );

        sp.datBaoHanhThang(rs.getInt("thoiHanBaoHanh"));
        return sp;
    }

    private static boolean dangKinhDoanh(SanPham s) {
        return s.getTrangThai() != TrangThaiSanPham.TAM_NGUNG;
    }

    @Override
    public List<SanPham> getAll() {
        List<SanPham> ds = new ArrayList<>();

        String sql = "SELECT maSanPham, tenSanPham, giaBan, soLuongTon, "
                + "thoiHanBaoHanh, trangThai "
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
        String sql = "INSERT INTO SanPham(maSanPham, tenSanPham, giaBan, "
                + "soLuongTon, thoiHanBaoHanh, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getMaSanPham());
            ps.setString(2, s.getTenSanPham());
            ps.setDouble(3, s.getGiaBan());
            ps.setInt(4, s.getSoLuongTon());
            ps.setInt(5, s.baoHanhThang());
            ps.setBoolean(6, dangKinhDoanh(s));

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean capNhat(SanPham s) {
        String sql = "UPDATE SanPham SET tenSanPham = ?, giaBan = ?, "
                + "soLuongTon = ?, thoiHanBaoHanh = ?, trangThai = ? "
                + "WHERE maSanPham = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getTenSanPham());
            ps.setDouble(2, s.getGiaBan());
            ps.setInt(3, s.getSoLuongTon());
            ps.setInt(4, s.baoHanhThang());
            ps.setBoolean(5, dangKinhDoanh(s));
            ps.setString(6, s.getMaSanPham());

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

        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maSanPham, 3, 10) AS INT)) "
                + "FROM SanPham";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                max = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format("SP%03d", max + 1);
    }
}
