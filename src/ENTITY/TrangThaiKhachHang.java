package ENTITY;

public enum TrangThaiKhachHang {
	
	KH_MOI("Khách hàng mới"), 
    TOT ("Khách hàng tiềm năng"), //Nhân viên biết khách hàng này là khách hàng tốt của cửa hàng
    NOXAU("khách hàng nợ xấu"),	// nhân viên biết khách hàng này bị nợ xấu
    DANHSACHDEN("Danh sách đen"); //nhân viên biết khách hàng này nằm trong danh sách đen

    private final String tenHienThi;

    TrangThaiKhachHang(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    @Override
    public String toString() {
        return tenHienThi;
    }
}