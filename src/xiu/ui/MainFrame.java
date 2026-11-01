package xiu.ui;

import DAO.TaiKhoanNhanVienDAO;
import xiu.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Khung chính. Menu hướng chức năng: Hệ thống | Danh mục | Cập nhật | Tìm kiếm | Xử lý | Báo biểu.
 * Mỗi mục menu mở một màn hình trong CardLayout (tạo khi dùng lần đầu, nạp lại dữ liệu mỗi lần mở).
 * Quyền sử dụng theo loại quyền tài khoản: QUAN_LY, BAN_HANG, CSKH, KHO.
 */
public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, Supplier<JPanel>> factories = new LinkedHashMap<>();
    private final Map<String, JPanel> created = new HashMap<>();
    private final JLabel lbScreen = new JLabel("Trang chủ");

    public MainFrame() {
        super("XIU - Hệ thống quản lý siêu thị điện máy");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { exit(); }
        });
        setSize(1280, 780); setMinimumSize(new Dimension(1000, 640)); setLocationRelativeTo(null);

        registerScreens();
        setJMenuBar(buildMenu());

        JPanel header = new JPanel(new BorderLayout()); header.setBackground(Color.WHITE);
        header.setBorder(new javax.swing.border.MatteBorder(0, 0, 1, 0, Theme.BORDER));
        JLabel logo = new JLabel("XIU"); logo.setFont(Theme.BASE.deriveFont(Font.BOLD, 22f)); logo.setForeground(Theme.BLUE); logo.setBorder(new EmptyBorder(8, 22, 8, 12));
        lbScreen.setFont(Theme.BOLD); lbScreen.setForeground(Theme.MUTED);
        JLabel user = new JLabel(PhienLamViec.nhanVien().getHoTenNhanVien() + "  |  " + PhienLamViec.quyen()); user.setFont(Theme.BOLD); user.setForeground(Theme.NAVY); user.setBorder(new EmptyBorder(0, 0, 0, 22));
        JPanel l = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); l.setOpaque(false); l.add(logo); l.add(lbScreen);
        header.add(l, BorderLayout.WEST); header.add(user, BorderLayout.EAST);

        content.setBackground(Theme.BG);
        JLabel status = new JLabel("  Tài khoản: " + PhienLamViec.taiKhoan().getTenDangNhap() + "   |   Phím tắt: Ctrl+số/chữ theo từng mục menu, Alt+chữ gạch chân để mở nhóm");
        status.setFont(Theme.BASE.deriveFont(12f)); status.setForeground(Theme.MUTED); status.setBorder(new EmptyBorder(4, 8, 4, 8));
        add(header, BorderLayout.NORTH); add(content); add(status, BorderLayout.SOUTH);
        show("home");
    }

    private void registerScreens() {
        factories.put("home", DashboardPanel::new);
        factories.put("nhanVien", NhanVienPanel::new);
        factories.put("taiKhoan", DanhMuc::taiKhoan);
        factories.put("caLam", DanhMuc::caLam);
        factories.put("sanPham", DanhMuc::sanPham);
        factories.put("khachHang", DanhMuc::khachHang);
        factories.put("nhaCungCap", DanhMuc::nhaCungCap);
        factories.put("khuyenMai", DanhMuc::khuyenMai);
        factories.put("phanCa", DanhMuc::phanCa);
        factories.put("nhapKho", NhapKhoPanel::new);
        factories.put("baoHanh", BaoHanhPanel::new);
        factories.put("timSanPham", TimKiemSanPhamPanel::new);
        factories.put("lichSu", LichSuMuaHangPanel::new);
        factories.put("lichLam", LichLamViecPanel::new);
        factories.put("banHang", BanHangPanel::new);
        factories.put("thongKe", ThongKePanel::new);
    }

    private void show(String id) {
        JPanel p = created.get(id);
        if (p == null) { p = factories.get(id).get(); created.put(id, p); content.add(p, id); }
        else if (p instanceof Refreshable r) r.refresh();
        cards.show(content, id);
    }

    // ---------------- Menu hướng chức năng ----------------
    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        bar.setBorder(new EmptyBorder(2, 10, 2, 10));
        String all = "QUAN_LY,BAN_HANG,CSKH,KHO";

        JMenu he = menu(bar, "Hệ thống", KeyEvent.VK_H);
        item(he, "Trang chủ", "home", ctrl(KeyEvent.VK_H), all);
        he.addSeparator();
        action(he, "Đổi mật khẩu...", ctrl(KeyEvent.VK_P), this::changePassword);
        action(he, "Đăng xuất", ctrl(KeyEvent.VK_L), this::logout);
        action(he, "Thoát", ctrl(KeyEvent.VK_Q), this::exit);

        JMenu dm = menu(bar, "Danh mục", KeyEvent.VK_D);
        item(dm, "Nhân viên", "nhanVien", ctrl(KeyEvent.VK_1), "QUAN_LY");
        item(dm, "Tài khoản nhân viên", "taiKhoan", ctrl(KeyEvent.VK_2), "QUAN_LY");
        item(dm, "Ca làm việc", "caLam", ctrl(KeyEvent.VK_3), "QUAN_LY");
        dm.addSeparator();
        item(dm, "Sản phẩm", "sanPham", ctrl(KeyEvent.VK_4), "QUAN_LY,KHO");
        item(dm, "Nhà cung cấp", "nhaCungCap", ctrl(KeyEvent.VK_5), "QUAN_LY,KHO");
        item(dm, "Nhập kho", "nhapKho", ctrl(KeyEvent.VK_I), "QUAN_LY,KHO");
        dm.addSeparator();
        item(dm, "Khách hàng", "khachHang", ctrl(KeyEvent.VK_6), "QUAN_LY,BAN_HANG,CSKH");
        item(dm, "Khuyến mãi", "khuyenMai", ctrl(KeyEvent.VK_7), "QUAN_LY");

        JMenu cn = menu(bar, "Cập nhật", KeyEvent.VK_C);
        item(cn, "Phân ca làm việc", "phanCa", ctrl(KeyEvent.VK_F), "QUAN_LY");
        item(cn, "Tiếp nhận bảo hành", "baoHanh", ctrl(KeyEvent.VK_B), "QUAN_LY,CSKH");

        JMenu tk = menu(bar, "Tìm kiếm", KeyEvent.VK_T);
        item(tk, "Sản phẩm", "timSanPham", ctrl(KeyEvent.VK_K), all);
        item(tk, "Lịch sử mua hàng", "lichSu", ctrl(KeyEvent.VK_J), "QUAN_LY,BAN_HANG,CSKH");
        item(tk, "Lịch làm việc", "lichLam", ctrl(KeyEvent.VK_W), all);

        JMenu xl = menu(bar, "Xử lý", KeyEvent.VK_X);
        item(xl, "Bán hàng & thanh toán", "banHang", ctrl(KeyEvent.VK_O), "QUAN_LY,BAN_HANG");

        JMenu bb = menu(bar, "Báo biểu", KeyEvent.VK_B);
        item(bb, "Thống kê số liệu", "thongKe", ctrl(KeyEvent.VK_R), "QUAN_LY,KHO");
        return bar;
    }

    private static KeyStroke ctrl(int key) { return KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK); }

    private JMenu menu(JMenuBar bar, String text, int mnemonic) {
        JMenu m = new JMenu(text); m.setMnemonic(mnemonic); m.setFont(Theme.BOLD.deriveFont(14f));
        m.setForeground(Color.WHITE); m.setBorder(new EmptyBorder(6, 12, 6, 12));
        bar.add(m); return m;
    }

    private void item(JMenu m, String text, String screen, KeyStroke ks, String roles) {
        JMenuItem it = new JMenuItem(text); it.setAccelerator(ks); it.setFont(Theme.BASE);
        it.setEnabled(Arrays.asList(roles.split(",")).contains(PhienLamViec.quyen()));
        it.addActionListener(e -> { lbScreen.setText("›  " + text); show(screen); });
        m.add(it);
    }

    private void action(JMenu m, String text, KeyStroke ks, Runnable r) {
        JMenuItem it = new JMenuItem(text); it.setAccelerator(ks); it.setFont(Theme.BASE);
        it.addActionListener(e -> r.run()); m.add(it);
    }

    // ---------------- Hệ thống ----------------
    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        PhienLamViec.dangXuat();
        dispose(); new LoginFrame().setVisible(true);
    }

    private void exit() {
        if (JOptionPane.showConfirmDialog(this, "Thoát chương trình?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) System.exit(0);
    }

    private void changePassword() {
        JPasswordField o = new JPasswordField(16), n = new JPasswordField(16), c = new JPasswordField(16);
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Mật khẩu hiện tại")); p.add(o); p.add(new JLabel("Mật khẩu mới")); p.add(n); p.add(new JLabel("Nhập lại mật khẩu mới")); p.add(c);
        if (JOptionPane.showConfirmDialog(this, p, "Đổi mật khẩu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        String np = new String(n.getPassword());
        String err = np.length() < 6 ? "Mật khẩu mới tối thiểu 6 ký tự" : !np.equals(new String(c.getPassword())) ? "Mật khẩu nhập lại không khớp" : null;
        if (err == null && !PhienLamViec.taiKhoan().doiMatKhau(new String(o.getPassword()), np)) err = "Mật khẩu hiện tại không đúng";
        if (err == null && !new TaiKhoanNhanVienDAO().capNhat(PhienLamViec.taiKhoan())) err = "Không lưu được mật khẩu mới";
        if (err != null) { JOptionPane.showMessageDialog(this, err, "Đổi mật khẩu thất bại", JOptionPane.WARNING_MESSAGE); return; }
        JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
