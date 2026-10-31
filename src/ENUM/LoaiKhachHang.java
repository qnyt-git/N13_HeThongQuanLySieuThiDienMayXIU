package ENUM;

public enum LoaiKhachHang {
    KH_VIP("Khách VIP"),
    KH_MOI("Khách mới"),

    private final String nhan;
	
	LoaiKhachHang(String nhan) {this.nhan = nhan;}
	
    @Override public String toString() { return nhan; }
}
