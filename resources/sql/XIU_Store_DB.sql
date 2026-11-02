-- ============================================================
-- HỆ THỐNG QUẢN LÝ SIÊU THỊ ĐIỆN MÁY XIU
-- Script khởi tạo toàn bộ Database, Bảng và Dữ liệu mẫu (Đã thêm Địa chỉ KH)
-- ============================================================

USE master;
GO

-- ============ 1. XÓA DATABASE CŨ (NẾU CÓ) ============
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'XIU_Store_DB')
BEGIN
    ALTER DATABASE XIU_Store_DB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE XIU_Store_DB;
END
GO

-- ============ 2. TẠO DATABASE MỚI ============
CREATE DATABASE XIU_Store_DB;
GO
USE XIU_Store_DB;
GO

-- ============================================================
-- 3. TẠO CÁC BẢNG
-- ============================================================

-- 3.1. Nhân viên
CREATE TABLE NhanVien (
    maNhanVien      VARCHAR(10)     PRIMARY KEY,
    hoTenNhanVien   NVARCHAR(100)   NOT NULL,
    gioiTinh        BIT             NOT NULL,
    chucVu          NVARCHAR(50)    NOT NULL,
    soDienThoai     VARCHAR(15)     UNIQUE,
    ngaySinh        DATE,
    trinhDoHocVan   NVARCHAR(100),
    heSoLuong       FLOAT           CHECK (heSoLuong > 0),
    luongCoBan      INT             CHECK (luongCoBan > 0),
    trangThai       BIT             DEFAULT 1
);
GO

-- 3.2. Tài khoản nhân viên
CREATE TABLE TaiKhoanNhanVien (
    maTaiKhoan      VARCHAR(10)     PRIMARY KEY,
    tenDangNhap     VARCHAR(50)     UNIQUE NOT NULL,
    matKhau         VARCHAR(100)    NOT NULL,
    loaiQuyen       VARCHAR(20)     CHECK (loaiQuyen IN ('QUAN_LY','BAN_HANG','THU_NGAN','CSKH','KHO')),
    trangThai       BIT             DEFAULT 1,
    maNhanVien      VARCHAR(10)     UNIQUE REFERENCES NhanVien(maNhanVien)
);
GO

-- 3.3. Sản phẩm
CREATE TABLE SanPham (
    maSanPham       VARCHAR(10)     PRIMARY KEY,
    tenSanPham      NVARCHAR(200)   NOT NULL,
    giaBan          FLOAT           CHECK (giaBan > 0),
    soLuongTon      INT             CHECK (soLuongTon >= 0),
    thoiHanBaoHanh  INT             CHECK (thoiHanBaoHanh >= 0),
    trangThai NVARCHAR(20) NOT NULL CHECK (trangThai IN (N'Đang bán',N'Tạm ngưng',N'Hết hàng',N'Vô hiệu hóa'))
);
GO

-- 3.4. Khách hàng (Đã bổ sung cột diaChi)
CREATE TABLE KhachHang (
    maKhachHang     VARCHAR(10)     PRIMARY KEY,
    hoTenKhachHang  NVARCHAR(100)   NOT NULL,
    soDienThoai     VARCHAR(15)     UNIQUE,
    diaChi          NVARCHAR(200),
    diemTichLuy     INT             CHECK (diemTichLuy >= 0) DEFAULT 0,
    loaiKH          VARCHAR(20)     CHECK (loaiKH IN ('KH_MOI','KH_VIP'))
);
GO

-- 3.5. Nhà cung cấp
CREATE TABLE NhaCungCap (
    maNCC           VARCHAR(10)     PRIMARY KEY,
    tenNCC          NVARCHAR(200),
    soDienThoai     VARCHAR(15)     UNIQUE,
    diaChi          NVARCHAR(200),
    email           VARCHAR(100),
    trangThai       BIT             DEFAULT 1
);
GO

-- 3.6. Khuyến mãi
CREATE TABLE KhuyenMai (
    maKhuyenMai     VARCHAR(10)     PRIMARY KEY,
    tenKhuyenMai    NVARCHAR(200),
    ngayBatDau      DATETIME,
    ngayKetThuc     DATETIME,
    giaTriGiam      FLOAT           CHECK (giaTriGiam > 0),
    trangThai       BIT             DEFAULT 1
);
GO

-- 3.7. Ca làm việc
CREATE TABLE CaLamViec (
    maCa            VARCHAR(10)     PRIMARY KEY,
    tenCa           NVARCHAR(50),
    gioBatDau       DATETIME,
    gioKetThuc      DATETIME
);
GO

-- 3.8. Phân ca
CREATE TABLE PhanCa (
    maPhanCa        VARCHAR(10)     PRIMARY KEY,
    ngayLam         DATE,
    trangThai       NVARCHAR(20)    CHECK (trangThai IN (N'Đã phân', N'Đã làm', N'Nghỉ')),
    maNhanVien      VARCHAR(10)     REFERENCES NhanVien(maNhanVien),
    maCa            VARCHAR(10)     REFERENCES CaLamViec(maCa),
    CONSTRAINT UQ_PhanCa UNIQUE (maNhanVien, ngayLam, maCa)
);
GO

-- 3.9. Phiếu đặt hàng
CREATE TABLE PhieuDatHang (
    maPhieuDatHang  VARCHAR(10)     PRIMARY KEY,
    ngayDatHang     DATETIME        DEFAULT GETDATE(),
    trangThai       NVARCHAR(20)    CHECK (trangThai IN (N'Chờ xử lý', N'Đã xác nhận', N'Đã hoàn thành', N'Đã hủy')),
    maKhachHang     VARCHAR(10)     REFERENCES KhachHang(maKhachHang),
    maNhanVien      VARCHAR(10)     REFERENCES NhanVien(maNhanVien),
    ghiChu          NVARCHAR(500)
);
GO

-- 3.10. Chi tiết phiếu đặt hàng
CREATE TABLE ChiTietPhieuDatHang (
    maPhieuDatHang  VARCHAR(10)     REFERENCES PhieuDatHang(maPhieuDatHang),
    maSanPham       VARCHAR(10)     REFERENCES SanPham(maSanPham),
    soLuong         INT             CHECK (soLuong > 0),
    donGia          FLOAT           CHECK (donGia > 0),
    PRIMARY KEY (maPhieuDatHang, maSanPham)
);
GO

-- 3.11. Hóa đơn
CREATE TABLE HoaDon (
    maHoaDon              VARCHAR(10)     PRIMARY KEY,
    ngayLap               DATETIME        DEFAULT GETDATE(),
    phuongThucThanhToan NVARCHAR(20)    CHECK (phuongThucThanhToan IN (N'Tiền mặt', N'QR', N'Chuyển khoản')),
    trangThai             NVARCHAR(20)    CHECK (trangThai IN (N'Chưa thanh toán', N'Đã thanh toán', N'Đã hủy')),
    lyDoHuyHoaDon         NVARCHAR(500),
    maPhieuDatHang        VARCHAR(10)     NULL REFERENCES PhieuDatHang(maPhieuDatHang),
    maKhachHang           VARCHAR(10)     REFERENCES KhachHang(maKhachHang),
    maNhanVien            VARCHAR(10)     REFERENCES NhanVien(maNhanVien),
    maKhuyenMai           VARCHAR(10)     NULL REFERENCES KhuyenMai(maKhuyenMai),
    ghiChu                NVARCHAR(500)
);
GO

-- 3.12. Chi tiết hóa đơn
CREATE TABLE ChiTietHoaDon (
    maHoaDon        VARCHAR(10)     REFERENCES HoaDon(maHoaDon),
    maSanPham       VARCHAR(10)     REFERENCES SanPham(maSanPham),
    soLuong         INT             CHECK (soLuong > 0),
    donGia          FLOAT           CHECK (donGia > 0),
    PRIMARY KEY (maHoaDon, maSanPham)
);
GO

-- 3.13. Phiếu nhập kho
CREATE TABLE PhieuNhapKho (
    maPhieuNhapKho  VARCHAR(10)     PRIMARY KEY,
    ngayNhap        DATETIME        DEFAULT GETDATE(),
    lyDoNhap        NVARCHAR(500),
    maNhanVien      VARCHAR(10)     REFERENCES NhanVien(maNhanVien),
    trangThai       NVARCHAR(20)    CHECK (trangThai IN (N'Chờ duyệt', N'Đã xác nhận',N'Đã hoàn thành', N'Đã hủy')),
    maNCC           VARCHAR(10)     REFERENCES NhaCungCap(maNCC)
);
GO

-- 3.14. Chi tiết phiếu nhập kho
CREATE TABLE ChiTietPhieuNhapKho (
    maPhieuNhapKho  VARCHAR(10)     REFERENCES PhieuNhapKho(maPhieuNhapKho),
    maSanPham       VARCHAR(10)     REFERENCES SanPham(maSanPham),
    soLuong         INT             CHECK (soLuong > 0),
    donGia          FLOAT           CHECK (donGia > 0),
    PRIMARY KEY (maPhieuNhapKho, maSanPham)
);
GO

-- 3.15. Phiếu bảo hành
CREATE TABLE PhieuBaoHanh (
    maPhieuBaoHanh  VARCHAR(10)     PRIMARY KEY,
    maKhachHang     VARCHAR(10)     REFERENCES KhachHang(maKhachHang),
    ngayTiepNhan    DATETIME        DEFAULT GETDATE(),
    ngayHenTra      DATETIME,
    trangThai       NVARCHAR(20)    CHECK (trangThai IN (N'Đã xác nhận', N'Đã trả', N'Đã hủy')),
    CONSTRAINT CK_NgayHenTra CHECK (ngayHenTra >= ngayTiepNhan)
);
GO

-- 3.16. Chi tiết phiếu bảo hành
CREATE TABLE ChiTietPhieuBaoHanh (
    maPhieuBaoHanh       VARCHAR(10)     REFERENCES PhieuBaoHanh(maPhieuBaoHanh),
    maSanPham            VARCHAR(10)     REFERENCES SanPham(maSanPham),
    tinhTrangSanPham     NVARCHAR(500)   NOT NULL,
    noiDungBaoHanh       NVARCHAR(500)   NOT NULL,
    PRIMARY KEY (maPhieuBaoHanh, maSanPham)
);
GO

-- ============================================================
-- 4. NHẬP DỮ LIỆU MẪU
-- ============================================================

-- 4.1. Nhân viên
INSERT INTO NhanVien(maNhanVien, hoTenNhanVien, chucVu, soDienThoai, ngaySinh, 
                     trinhDoHocVan, heSoLuong, luongCoBan, gioiTinh, trangThai) VALUES
('NV001', N'Nguyễn Văn A',  N'Quản lý', '0901234567', '1990-01-15', N'Đại học',   3.5, 5000000, 1, 1),
('NV002', N'Trần Thị B',    N'NVBH',    '0902222333', '1995-05-20', N'Cao đẳng',  2.4, 5000000, 0,  1),
('NV003', N'Lê Văn C',      N'NVBH',    '0903333444', '1993-08-10', N'Đại học',   2.4, 5000000, 1, 1),
('NV004', N'Phạm Thị D',    N'NVBH',    '0904444555', '1997-03-25', N'Trung cấp', 2.4, 5000000, 0,  1),
('NV005', N'Hoàng Văn E',   N'NVKT',    '0905555666', '1992-11-12', N'Đại học',   2.3, 5000000, 1, 1),
('NV006', N'Vũ Thị F',      N'CSKH',    '0906666777', '1994-07-18', N'Cao đẳng',  2.3, 5000000, 0,  1),
('NV007', N'Đặng Văn G',    N'CSKH',    '0907777888', '1996-09-30', N'Đại học',   2.3, 5000000, 1, 1),
('NV008', N'Bùi Thị H',     N'NVKho',   '0908888999', '1991-02-14', N'Trung cấp', 2.3, 5000000, 0,  1),
('NV009', N'Ngô Văn I',     N'NVKho',   '0909999000', '1998-06-22', N'Cao đẳng',  2.3, 5000000, 1, 1),
('NV010', N'Đỗ Thị K',      N'NVKT_BH', '0910111222', '1993-04-05', N'Đại học',   2.5, 5500000, 0,  1),
('NV011', N'Lý Văn L',      N'NVBH',    '0911222333', '1995-12-20', N'Trung cấp', 2.4, 5000000, 1, 0);
GO

-- 4.2. Tài khoản
INSERT INTO TaiKhoanNhanVien(maTaiKhoan, tenDangNhap, matKhau, loaiQuyen, 
                           trangThai, maNhanVien) VALUES
('TK001', 'admin',     '123456', 'QUAN_LY',  1, 'NV001'),
('TK002', 'banhang',   '123456', 'BAN_HANG', 1, 'NV002'),
('TK003', 'banhang2',  '123456', 'BAN_HANG', 1, 'NV003'),
('TK004', 'banhang3',  '123456', 'BAN_HANG', 1, 'NV004'),
('TK005', 'ketoan',    '123456', 'BAN_HANG', 1, 'NV005'),
('TK006', 'cskh',      '123456', 'CSKH',     1, 'NV006'),
('TK007', 'cskh2',     '123456', 'CSKH',     1, 'NV007'),
('TK008', 'kho',       '123456', 'KHO',      1, 'NV008'),
('TK009', 'kho2',      '123456', 'KHO',      1, 'NV009'),
('TK010', 'kythuat',   '123456', 'CSKH',     1, 'NV010'),
('TK011', 'nv_nghi',   '123456', 'BAN_HANG', 0, 'NV011');
GO

-- 4.3. Ca làm việc
INSERT INTO CaLamViec(maCa, tenCa, gioBatDau, gioKetThuc) VALUES
('CA001', N'Ca sáng',  '2024-01-01 06:00:00', '2024-01-01 12:00:00'),
('CA002', N'Ca chiều', '2024-01-01 12:00:00', '2024-01-01 18:00:00'),
('CA003', N'Ca tối',   '2024-01-01 18:00:00', '2024-01-01 22:00:00');
GO

-- 4.4. Nhà cung cấp
INSERT INTO NhaCungCap(maNCC, tenNCC, soDienThoai, diaChi, email, trangThai) VALUES
('NCC001', N'Công ty Samsung VN',     '0281234567', N'TP.HCM',     'samsung@vn.com', 1),
('NCC002', N'Công ty LG Electronics', '0282345678', N'TP.HCM',     'lg@vn.com',      1),
('NCC003', N'Công ty Toshiba VN',     '0283456789', N'Hà Nội',     'toshiba@vn.com', 1),
('NCC004', N'Công ty Sony VN',        '0284567890', N'Bình Dương', 'sony@vn.com',    1),
('NCC005', N'Công ty Daikin VN',      '0285678901', N'TP.HCM',     'daikin@vn.com',  1);
GO

-- 4.5. Sản phẩm
INSERT INTO SanPham(maSanPham, tenSanPham, giaBan, soLuongTon, 
                    thoiHanBaoHanh, trangThai) VALUES
('SP001', N'Tủ lạnh LG Inverter 240L',   12000000, 15, 24, N'Đang bán'),
('SP002', N'TV Samsung 4K 55 inch',      18000000,  8, 12, N'Đang bán'),
('SP003', N'Máy giặt Toshiba 10kg',      9000000, 20, 24, N'Đang bán'),
('SP004', N'Điều hòa Daikin 1.5HP',      11500000, 12, 24, N'Đang bán'),
('SP005', N'Tủ lạnh Samsung 488L',       25000000,  5, 24, N'Đang bán'),
('SP006', N'Máy giặt LG TurboWash 10kg', 10250000,  0, 24, N'Tạm ngưng'),
('SP007', N'Smart TV Sony 4K 65 inch',   32000000,  3, 24, N'Đang bán'),
('SP008', N'Quạt điều hòa Sunhouse',     3450000, 25, 12, N'Đang bán');
GO

-- 4.6. Khách hàng (Đã bổ sung dữ liệu địa chỉ)
INSERT INTO KhachHang(maKhachHang, hoTenKhachHang, soDienThoai, diaChi, 
                      diemTichLuy, loaiKH) VALUES
('KH001', N'Nguyễn Thị Mai',  '0911111111', N'123 Nguyễn Trãi, Q.5, TP.HCM', 1500, 'KH_VIP'),
('KH002', N'Trần Văn Nam',    '0922222222', N'45 Lê Lợi, Q.1, TP.HCM',      350,  'KH_MOI'),
('KH003', N'Lê Thị Hoa',      '0933333333', N'78 CMT8, Q.10, TP.HCM',      2200, 'KH_VIP'),
('KH004', N'Phạm Văn Tùng',   '0944444444', N'12 Điện Biên Phủ, Q.Bình Thạnh, TP.HCM', 100, 'KH_MOI'),
('KH005', N'Hoàng Thị Lan',   '0955555555', N'90 Lê Văn Việt, TP. Thủ Đức', 80,   'KH_MOI'),
('KH006', N'Vũ Văn Khánh',    '0966666666', N'15 Quang Trung, Q.Gò Vấp, TP.HCM', 0,   'KH_MOI'),
('KH007', N'Đặng Thị Thu',    '0977777777', N'34 Nguyễn Văn Bảo, Q.Gò Vấp, TP.HCM', 500, 'KH_VIP');
GO

-- 4.7. Khuyến mãi
INSERT INTO KhuyenMai(maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, 
                      giaTriGiam, trangThai) VALUES
('KM001', N'Khai trương - Giảm 10%',   '2024-01-01', '2030-12-31', 10, 1),
('KM002', N'Black Friday - Giảm 20%',  '2024-11-20', '2024-12-05', 20, 1),
('KM003', N'Tết 2025 - Giảm 15%',      '2025-01-15', '2025-02-15', 15, 1);
GO
