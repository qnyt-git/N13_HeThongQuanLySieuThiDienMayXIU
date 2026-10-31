package DAO;

import ENTITY.KhuyenMai;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO của KhuyenMai. SQL lưu ngày (DATE); entity dùng LocalDateTime nên đọc lên được quy về
 * 00:00 ngày bắt đầu và 23:59:59 ngày kết thúc. Entity chưa có "loại" nên khi thêm mới cột maLoai = GIAM_GIA
 * và khi sửa thì giữ nguyên.
 */
public class KhuyenMaiDAO implements IDAO<KhuyenMai> {

    private static java.sql.Date toSqlDate(LocalDateTime t) {
        return t == null ? null : java.sql.Date.valueOf(t.toLocalDate());
    }

    @Override
    public List<KhuyenMai> getAll() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ghiChu, ngayBatDau, ngayKetThuc, giaTriGiam, trangThai "
                + "FROM KhuyenMai ORDER BY maKhuyenMai";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                java.sql.Date bd = rs.getDate("ngayBatDau");
                java.sql.Date kt = rs.getDate("ngayKetThuc");
                LocalDate ngayBd = bd == null ? null : bd.toLocalDate();
                LocalDate ngayKt = kt == null ? null : kt.toLocalDate();

                ds.add(new KhuyenMai(
                        rs.getString("maKhuyenMai"),
                        rs.getString("tenKhuyenMai"),
                        ngayBd == null ? null : ngayBd.atStartOfDay(),
                        ngayKt == null ? null : ngayKt.atTime(23, 59, 59),
                        rs.getDouble("giaTriGiam"),
                        rs.getBoolean("trangThai"),
                        rs.getString("ghiChu")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public boolean them(KhuyenMai k) {
        String sql = "INSERT INTO KhuyenMai(maKhuyenMai, tenKhuyenMai, ghiChu, ngayBatDau, ngayKetThuc, giaTriGiam, "
                + "trangThai, maLoai) VALUES (?, ?, ?, ?, ?, ?, ?, 'GIAM_GIA')";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getMaKhuyenMai());
            ps.setString(2, k.getTenKhuyenMai());
            ps.setString(3, k.getGhiChu());
            ps.setDate(4, toSqlDate(k.getThoiGianBatDau()));
            ps.setDate(5, toSqlDate(k.getThoiGianKetThuc()));
            ps.setDouble(6, k.getGiaTriGiam());
            ps.setBoolean(7, k.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(KhuyenMai k) {
        String sql = "UPDATE KhuyenMai SET tenKhuyenMai = ?, ghiChu = ?, ngayBatDau = ?, ngayKetThuc = ?, "
                + "giaTriGiam = ?, trangThai = ? WHERE maKhuyenMai = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getTenKhuyenMai());
            ps.setString(2, k.getGhiChu());
            ps.setDate(3, toSqlDate(k.getThoiGianBatDau()));
            ps.setDate(4, toSqlDate(k.getThoiGianKetThuc()));
            ps.setDouble(5, k.getGiaTriGiam());
            ps.setBoolean(6, k.isTrangThai());
            ps.setString(7, k.getMaKhuyenMai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(KhuyenMai k) {
        String sql = "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getMaKhuyenMai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maKhuyenMai, 3, 10) AS INT)) FROM KhuyenMai";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("KM%03d", max + 1);
    }
}
