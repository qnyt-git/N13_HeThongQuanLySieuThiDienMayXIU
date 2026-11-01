package DAO;

import ENTITY.NhanVien;
import ENTITY.TaiKhoanNhanVien;
import ENUM.LoaiQuyen;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** DAO của TaiKhoanNhanVien (bảng TaiKhoanNhanVien). Cột loaiQuyen lưu tên hằng enum. */
public class TaiKhoanNhanVienDAO implements IDAO<TaiKhoanNhanVien> {

    private static LoaiQuyen docQuyen(String s) {
        String x = s == null ? "" : s.trim();
        for (LoaiQuyen q : LoaiQuyen.values()) {
            if (q.name().equalsIgnoreCase(x) || q.toString().equalsIgnoreCase(x)) return q;
        }
        return LoaiQuyen.BAN_HANG;
    }

    private static String maNV(TaiKhoanNhanVien t) {
        return t.getMaNhanVien() == null ? null : t.getMaNhanVien().getMaNhanVien();
    }

    @Override
    public List<TaiKhoanNhanVien> getAll() {
        List<TaiKhoanNhanVien> ds = new ArrayList<>();

        Map<String, NhanVien> nv = new HashMap<>();
        for (NhanVien n : new NhanVienDAO().getAll()) nv.put(n.getMaNhanVien(), n);

        String sql = "SELECT maTaiKhoan, tenDangNhap, matKhau, loaiQuyen, trangThai, maNhanVien "
                + "FROM TaiKhoanNhanVien ORDER BY maTaiKhoan";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(new TaiKhoanNhanVien(
                        rs.getString("maTaiKhoan"),
                        rs.getString("tenDangNhap"),
                        nv.get(rs.getString("maNhanVien")),
                        rs.getString("matKhau"),
                        docQuyen(rs.getString("loaiQuyen")),
                        rs.getBoolean("trangThai")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Tìm tài khoản theo tên đăng nhập (kèm nhân viên). @return null nếu không có. */
    public TaiKhoanNhanVien timTheoTenDangNhap(String tenDangNhap) throws Exception {
        String sql = "SELECT maTaiKhoan, tenDangNhap, matKhau, loaiQuyen, trangThai, maNhanVien "
                + "FROM TaiKhoanNhanVien WHERE tenDangNhap = ?";

        String maTK, tenDN, matKhau, quyen, maNhanVien;
        boolean trangThai;

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                maTK = rs.getString("maTaiKhoan");
                tenDN = rs.getString("tenDangNhap");
                matKhau = rs.getString("matKhau");
                quyen = rs.getString("loaiQuyen");
                trangThai = rs.getBoolean("trangThai");
                maNhanVien = rs.getString("maNhanVien");
            }
        }

        NhanVien nv = new NhanVienDAO().timTheoMa(maNhanVien);

        return new TaiKhoanNhanVien(
                maTK, tenDN, nv, matKhau, docQuyen(quyen), trangThai
        );
    }

    @Override
    public boolean them(TaiKhoanNhanVien t) {
        String sql = "INSERT INTO TaiKhoanNhanVien(maTaiKhoan, tenDangNhap, matKhau, loaiQuyen, trangThai, maNhanVien) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getMaTaiKhoan());
            ps.setString(2, t.getTenDangNhap());
            ps.setString(3, t.getMatKhau());
            ps.setString(4, t.getLoaiQuyen().name());
            ps.setBoolean(5, t.isTrangThai());
            ps.setString(6, maNV(t));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhat(TaiKhoanNhanVien t) {
        String sql = "UPDATE TaiKhoanNhanVien SET tenDangNhap = ?, matKhau = ?, loaiQuyen = ?, trangThai = ?, "
                + "maNhanVien = ? WHERE maTaiKhoan = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getTenDangNhap());
            ps.setString(2, t.getMatKhau());
            ps.setString(3, t.getLoaiQuyen().name());
            ps.setBoolean(4, t.isTrangThai());
            ps.setString(5, maNV(t));
            ps.setString(6, t.getMaTaiKhoan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean xoa(TaiKhoanNhanVien t) {
        String sql = "DELETE FROM TaiKhoanNhanVien WHERE maTaiKhoan = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getMaTaiKhoan());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String taoMaMoi() {
        int max = 0;
        String sql = "SELECT MAX(TRY_CAST(SUBSTRING(maTaiKhoan, 3, 10) AS INT)) FROM TaiKhoanNhanVien";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) max = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("TK%03d", max + 1);
    }
}
