package xiu.ui;

import DAO.TaiKhoanNhanVienDAO;
import ENTITY.TaiKhoanNhanVien;
import xiu.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Màn hình đăng nhập (kèm quên mật khẩu). */
public class LoginFrame extends JFrame {
    private final JTextField tfUser = Theme.field(20);
    private final JPasswordField pfPass = new JPasswordField(20);
    private final JLabel lbError = new JLabel(" ");

    public LoginFrame() {
        super("XIU - Đăng nhập hệ thống");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 540); setLocationRelativeTo(null); setResizable(false);
        setLayout(new GridLayout(1, 2));

        JPanel brand = new JPanel(null) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, Theme.NAVY, getWidth(), getHeight(), Theme.BLUE));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.setFont(Theme.BASE.deriveFont(Font.BOLD, 96f));
                String logo = "XIU"; g.drawString(logo, (getWidth() - g.getFontMetrics().stringWidth(logo)) / 2, 190);
                g.setFont(Theme.BASE.deriveFont(Font.BOLD, 22f));
                String s1 = "Siêu thị điện máy XIU"; g.drawString(s1, (getWidth() - g.getFontMetrics().stringWidth(s1)) / 2, 250);
                g.setFont(Theme.BASE.deriveFont(16f));
                String s2 = "Mua sắm thông minh - Cuộc sống tiện nghi"; g.drawString(s2, (getWidth() - g.getFontMetrics().stringWidth(s2)) / 2, 285);
                g.dispose();
            }
        };

        JPanel right = new JPanel(new GridBagLayout()); right.setBackground(new Color(0xE8F0FB));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(Theme.BORDER, 1, true), new EmptyBorder(26, 30, 26, 30)));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; g.insets = new Insets(5, 0, 5, 0);

        JLabel title = new JLabel("Đăng nhập hệ thống", SwingConstants.CENTER); title.setFont(Theme.BASE.deriveFont(Font.BOLD, 22f)); title.setForeground(Theme.NAVY);
        g.gridy = 0; g.insets = new Insets(0, 0, 14, 0); form.add(title, g);
        g.insets = new Insets(5, 0, 5, 0);
        g.gridy = 1; form.add(Theme.label("Tên đăng nhập"), g);
        g.gridy = 2; form.add(tfUser, g);
        g.gridy = 3; form.add(Theme.label("Mật khẩu"), g);
        pfPass.setFont(Theme.BASE); pfPass.setBorder(tfUser.getBorder());
        JCheckBox show = new JCheckBox("Hiện mật khẩu"); show.setOpaque(false); show.setFont(Theme.BASE.deriveFont(12.5f));
        char echo = pfPass.getEchoChar();
        show.addActionListener(e -> pfPass.setEchoChar(show.isSelected() ? (char) 0 : echo));
        g.gridy = 4; form.add(pfPass, g);
        g.gridy = 5; form.add(show, g);
        lbError.setForeground(Theme.RED); lbError.setFont(Theme.BASE.deriveFont(12.5f));
        g.gridy = 6; form.add(lbError, g);
        JButton login = Theme.primary("Đăng nhập"); login.setFont(Theme.BOLD.deriveFont(15f));
        login.addActionListener(e -> doLogin());
        g.gridy = 7; form.add(login, g);
        JButton forgot = Theme.ghost("Quên mật khẩu?");
        forgot.addActionListener(e -> new ForgotPasswordDialog(this).setVisible(true));
        g.gridy = 8; form.add(forgot, g);

        right.add(form, new GridBagConstraints());
        add(brand); add(right);
        getRootPane().setDefaultButton(login);
    }

    private void doLogin() {
        String u = tfUser.getText().trim(), p = new String(pfPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) { lbError.setText("Vui lòng nhập tên đăng nhập và mật khẩu"); return; }
        try {
            TaiKhoanNhanVien tk = new TaiKhoanNhanVienDAO().timTheoTenDangNhap(u);
            String err = tk == null ? "Tên đăng nhập không tồn tại" : tk.kiemTraDangNhap(u, p);
            if (err != null) {
                lbError.setText(err);
                pfPass.setText("");
                pfPass.requestFocus();
                return;
            }
            PhienLamViec.dangNhap(tk);
            dispose();
            new MainFrame().setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            String detail = ex.getMessage() == null ? "Không rõ nguyên nhân" : ex.getMessage();
            lbError.setText("Không truy vấn được tài khoản SQL Server");
            JOptionPane.showMessageDialog(this,
                    "Không thể kiểm tra tài khoản đăng nhập.\n"
                    + detail + "\n\nKiểm tra dịch vụ SQL Server, database XIU_Store và bảng TaiKhoanNhanVien.",
                    "Lỗi kết nối cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
}
