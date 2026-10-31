package DAO;

import ENTITY.CaLamViec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** DAO của CaLamViec. Bảng không có cột nhân viên nên CaLamViec.getMaNhanVien() luôn là null. */
public class CaLamViecDAO implements IDAO<CaLamViec> {

    private static LocalDateTime toLdt(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }

    private static Timestamp toTs(LocalDateTime t) {
        return t == null ? null : Timestamp.valueOf(t);
    }

    @Override
    public List<CaLamViec> getAll() {
        List<CaLamViec> ds = new ArrayList<>();
        String sql = "SELECT maCa, tenCa, gioBatDau, gioKetThuc FROM CaLamViec ORDER BY gioBatDau, maCa";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(new CaLamViec(
                        rs.getString("maCa"),
                        rs.getString("tenCa"),
                        null,
                        toLdt(rs.getTimestamp("gioBatDau")),
                        toLdt(rs.getTimestamp("gioKetThuc"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public boolean them(CaLamViec c) {
        String sql = "INSERT INTO CaLamViec(maCa, tenCa, gioBatDau, gioKetThuc) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getMaCa());
            ps.setString(2, c.getTenCa());
            ps.setTimestamp(3, toTs(c.getGioBatDau()));
            ps.setTimestamp(4, toTs(c.getGioKetThuc()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(CaLamViec c) {
        String sql = "UPDATE CaLamViec SET tenCa = ?, gioBatDau = ?, gioKetThuc = ? WHERE maCa = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getTenCa());
            ps.setTimestamp(2, toTs(c.getGioBatDau()));
            ps.setTimestamp(3, toTs(c.getGioKetThuc()));
            ps.setString(4, c.getMaCa());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(CaLamViec c) {
        String sql = "DELETE FROM CaLamViec WHERE maCa = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getMaCa());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maCa, 3, 10) AS INT)) FROM CaLamViec";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("CA%03d", max + 1);
    }
}
