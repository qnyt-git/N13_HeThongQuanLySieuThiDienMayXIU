package DAO;

import ENTITY.CaLamViec;
import ENTITY.NhanVien;
import ENTITY.PhanCa;
import ENUM.TrangThaiPhanCa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** DAO của PhanCa. Ràng buộc UNIQUE (nhân viên, ngày, ca) do SQL kiểm tra: trùng ca thì them() trả về false. */
public class PhanCaDAO implements IDAO<PhanCa> {

    private static TrangThaiPhanCa docTrangThai(String s) {
        String x = s == null ? "" : s.trim();
        for (TrangThaiPhanCa t : TrangThaiPhanCa.values()) {
            if (t.name().equalsIgnoreCase(x) || t.toString().equalsIgnoreCase(x)) return t;
        }
        return TrangThaiPhanCa.DA_PHAN;
    }

    private static java.sql.Date toSqlDate(PhanCa p) {
        return p.getNgayLam() == null ? null : java.sql.Date.valueOf(p.getNgayLam());
    }

    @Override
    public List<PhanCa> getAll() {
        List<PhanCa> ds = new ArrayList<>();

        Map<String, NhanVien> nv = new HashMap<>();
        for (NhanVien n : new NhanVienDAO().getAll()) nv.put(n.getMaNhanVien(), n);
        Map<String, CaLamViec> ca = new HashMap<>();
        for (CaLamViec c : new CaLamViecDAO().getAll()) ca.put(c.getMaCa(), c);

        String sql = "SELECT maPhanCa, maNhanVien, maCa, ngayLam, trangThai FROM PhanCa ORDER BY ngayLam, maPhanCa";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                java.sql.Date d = rs.getDate("ngayLam");
                ds.add(new PhanCa(
                        rs.getString("maPhanCa"),
                        nv.get(rs.getString("maNhanVien")),
                        ca.get(rs.getString("maCa")),
                        d == null ? null : d.toLocalDate(),
                        docTrangThai(rs.getString("trangThai"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public boolean them(PhanCa p) {
        String sql = "INSERT INTO PhanCa(maPhanCa, maNhanVien, maCa, ngayLam, trangThai) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getMaPhanCa());
            ps.setString(2, p.getMaNhanVien().getMaNhanVien());
            ps.setString(3, p.getMaCa().getMaCa());
            ps.setDate(4, toSqlDate(p));
            ps.setString(5, p.getTrangThai().toString());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(PhanCa p) {
        String sql = "UPDATE PhanCa SET maNhanVien = ?, maCa = ?, ngayLam = ?, trangThai = ? WHERE maPhanCa = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getMaNhanVien().getMaNhanVien());
            ps.setString(2, p.getMaCa().getMaCa());
            ps.setDate(3, toSqlDate(p));
            ps.setString(4, p.getTrangThai().toString());
            ps.setString(5, p.getMaPhanCa());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(PhanCa p) {
        String sql = "DELETE FROM PhanCa WHERE maPhanCa = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getMaPhanCa());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maPhanCa, 3, 10) AS INT)) FROM PhanCa";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("PC%03d", max + 1);
    }
}
