package DAO;

import ENTITY.NhanVien;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class NhanVienDAO implements IDAO<NhanVien> {

    private static NhanVien maNhanVien(ResultSet rs) throws SQLException {
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

    // khi thêm/sửa nhânviên xuống csdl
    // Phải thông qua hàm này để quy đổi về đúng chuẩn ngày tháng mà SQL Server đọc được
    // Java không thể nhét trực tiếp kiểu LocalDate vào câu lệnh của JDBC
    private static java.sql.Date toSqlDate(NhanVien n) {
        return n.getNgaySinh() == null ? null : java.sql.Date.valueOf(n.getNgaySinh());
    }
    
    //lấy ds nv
    @Override
    public List<NhanVien> getAll() {
        List<NhanVien> ds = new ArrayList<>(); //tạo 1 danh sách nv: biến ds
        String sql = "SELECT maNhanVien, hoTenNhanVien, gioiTinh, chucVu, soDienThoai, ngaySinh, trinhDoHocVan, heSoLuong, luongCoBan, trangThai "
                + "FROM NhanVien ORDER BY maNhanVien";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(maNhanVien(rs)); //vòng lặp lấy ds nv rồi đẩy lên
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    //tìm
    public NhanVien timTheoMa(String maNhanVien) throws SQLException {
        String sql = "SELECT maNhanVien, hoTenNhanVien, gioiTinh, chucVu, soDienThoai, ngaySinh, trinhDoHocVan, heSoLuong, luongCoBan, trangThai "
                + "FROM NhanVien WHERE maNhanVien = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNhanVien); //gán giá trị vô dấu ? (để tìm)

            try (ResultSet rs = ps.executeQuery()) { //thực thi truy vấn
                if (rs.next()) {
                    return maNhanVien(rs); //kiểm tra xem có nv nào mã đó không
                }
                return null; 
            }
        }
    }
    
    //thêm
    @Override
    public boolean them(NhanVien n) {
        String sql = "INSERT INTO NhanVien(maNhanVien, hoTenNhanVien, gioiTinh, chucVu, soDienThoai, ngaySinh, trinhDoHocVan, heSoLuong, "
                + "luongCoBan, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getMaNhanVien());			// lấy giá trị từ n
            ps.setString(2, n.getHoTenNhanVien());
            ps.setBoolean(3, n.isGioiTinh());
            ps.setString(4, n.getChucVu());
            ps.setString(5, n.getSoDienThoai());
            ps.setDate(6, toSqlDate(n));
            ps.setString(7, n.getTrinhDoHocVan());
            ps.setDouble(8, n.getHeSoLuong());
            ps.setInt(9, n.getLuongCoBan());
            ps.setBoolean(10, n.isTrangThai());
            return ps.executeUpdate() > 0;			//cập nhật  nếu biểu thức >0
        } catch (Exception e) {
            e.printStackTrace();	
        }
        return false;
    }
    
    //sửa
    @Override
    public boolean capNhat(NhanVien n) {
        String sql = "UPDATE NhanVien SET hoTenNhanVien = ?, gioiTinh = ?, chucVu = ?, soDienThoai = ?, ngaySinh = ?, "
                + "trinhDoHocVan = ?, heSoLuong = ?, luongCoBan = ?, trangThai = ? WHERE maNhanVien = ?";

        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
        	ps.setString(1, n.getHoTenNhanVien()); 		// lấy giá trị từ n
            ps.setBoolean(2, n.isGioiTinh());
            ps.setString(3, n.getChucVu());
            ps.setString(4, n.getSoDienThoai());
            ps.setDate(5, toSqlDate(n));
            ps.setString(6, n.getTrinhDoHocVan());
            ps.setDouble(7, n.getHeSoLuong());
            ps.setInt(8, n.getLuongCoBan());
            ps.setBoolean(9, n.isTrangThai());
            ps.setString(10, n.getMaNhanVien());
            return ps.executeUpdate() > 0;			//cập nhật nếu biểu thức > 0
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //xóa
    @Override
    public boolean xoa(NhanVien n) {
        String sql = "DELETE FROM NhanVien WHERE maNhanVien = ?";
        // xóa nhân viên theo cột mã nhân viên
        try (PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql)) {
            ps.setString(1, n.getMaNhanVien());	// lấy mã nhân viên
            return ps.executeUpdate() > 0; 		// cập nhật nếu số dòng > 0
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
