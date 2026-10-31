package xiu.ui;

import DAO.*;
import ENTITY.*;
import xiu.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Quên mật khẩu: xác thực bằng tên đăng nhập + số điện thoại nhân viên rồi đặt lại mật khẩu. */
public class ForgotPasswordDialog extends JDialog {
    public ForgotPasswordDialog(Window owner) {
        super(owner, "Quên mật khẩu", ModalityType.APPLICATION_MODAL);
        JTextField tfUser = Theme.field(20), tfPhone = Theme.field(20);
        JPasswordField pf1 = new JPasswordField(20), pf2 = new JPasswordField(20);
        for (JPasswordField p : new JPasswordField[]{pf1, pf2}) { p.setFont(Theme.BASE); p.setBorder(tfUser.getBorder()); }
        JLabel err = new JLabel(" "); err.setForeground(Theme.RED);

        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(18, 22, 8, 22));
        GridBagConstraints g = new GridBagConstraints(); g.insets = new Insets(5, 4, 5, 4); g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.WEST;
        String[] lb = {"Tên đăng nhập", "Số điện thoại nhân viên", "Mật khẩu mới", "Nhập lại mật khẩu"};
        JComponent[] cs = {tfUser, tfPhone, pf1, pf2};
        for (int i = 0; i < 4; i++) { g.gridy = i; g.gridx = 0; g.weightx = 0; p.add(Theme.label(lb[i]), g); g.gridx = 1; g.weightx = 1; p.add(cs[i], g); }

        JButton ok = Theme.primary("Đặt lại mật khẩu"), cancel = Theme.ghost("Quay lại đăng nhập");
        cancel.addActionListener(e -> dispose());
        ok.addActionListener(e -> {
            TaiKhoanNhanVienDAO dao = new TaiKhoanNhanVienDAO();
            TaiKhoanNhanVien tk = dao.timTheoTenDangNhap(tfUser.getText().trim());
            if (tk == null) { err.setText("Tên đăng nhập không tồn tại"); return; }
            String b = new String(pf2.getPassword());
            if (!new String(pf1.getPassword()).equals(b)) { err.setText("Mật khẩu nhập lại không khớp"); return; }
            String loi = tk.datLaiMatKhau(tfPhone.getText().trim(), b);     // xác thực SĐT + kiểm tra độ dài trong entity
            if (loi != null) { err.setText(loi); return; }
            if (!dao.capNhat(tk)) { err.setText("Không lưu được mật khẩu mới"); return; }
            JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        JPanel south = new JPanel(new BorderLayout()); south.setBackground(Color.WHITE); south.setBorder(new EmptyBorder(0, 22, 16, 22));
        JPanel bt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); bt.setBackground(Color.WHITE); bt.add(cancel); bt.add(ok);
        south.add(err, BorderLayout.NORTH); south.add(bt, BorderLayout.SOUTH);
        add(p); add(south, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(ok);
        pack(); setLocationRelativeTo(owner);
    }
}
