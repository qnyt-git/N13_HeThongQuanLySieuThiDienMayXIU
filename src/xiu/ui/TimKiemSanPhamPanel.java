package xiu.ui;

import DAO.*;
import ENTITY.*;
import xiu.common.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Tra cứu sản phẩm theo từ khóa, khoảng giá, tình trạng tồn kho. */
public class TimKiemSanPhamPanel extends JPanel implements Refreshable {
    private final JTextField tfKey = Theme.field(18), tfFrom = Theme.field(9), tfTo = Theme.field(9);
    private final JCheckBox ckStock = new JCheckBox("Chỉ hiển thị còn hàng");
    private final DefaultTableModel result = Theme.model("Mã SP", "Tên sản phẩm", "Mã vạch", "Giá bán", "Số lượng tồn", "Bảo hành (tháng)", "Trạng thái");
    private final JLabel lbCount = Theme.muted("");

    public TimKiemSanPhamPanel() {
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        JPanel top = Theme.transparent(new BorderLayout(0, 12));
        top.add(Theme.title("Tìm kiếm sản phẩm"), BorderLayout.NORTH);

        JPanel criteria = Theme.card();
        JPanel row = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 10, 4));
        ckStock.setFont(Theme.BASE); ckStock.setOpaque(false);
        JButton find = Theme.primary("Tìm kiếm"), reset = Theme.ghost("Làm mới");
        find.addActionListener(e -> search());
        reset.addActionListener(e -> { tfKey.setText(""); tfFrom.setText(""); tfTo.setText(""); ckStock.setSelected(false); search(); });
        row.add(Theme.label("Mã / tên / mã vạch:")); row.add(tfKey);
        row.add(Theme.label("Giá từ:")); row.add(tfFrom); row.add(Theme.label("đến:")); row.add(tfTo);
        row.add(ckStock); row.add(find); row.add(reset);
        criteria.add(row);
        top.add(criteria, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
        add(Theme.scroll(Theme.table(result)), BorderLayout.CENTER);
        add(lbCount, BorderLayout.SOUTH);
        tfKey.addActionListener(e -> search());
        search();
    }

    private void search() {
        double from = 0, to = Double.MAX_VALUE;
        try {
            if (!tfFrom.getText().trim().isEmpty()) from = Double.parseDouble(tfFrom.getText().trim());
            if (!tfTo.getText().trim().isEmpty()) to = Double.parseDouble(tfTo.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Khoảng giá phải là số", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE); return;
        }
        if (from > to) { JOptionPane.showMessageDialog(this, "Giá bắt đầu không được lớn hơn giá kết thúc", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE); return; }
        String k = tfKey.getText().trim().toLowerCase();
        result.setRowCount(0);
        for (SanPham s : new SanPhamDAO().getAll()) {
            String hay = (s.getMaSanPham() + " " + s.getTenSanPham() + " " + s.getMaVach()).toLowerCase();
            if (!k.isEmpty() && !hay.contains(k)) continue;
            if (s.getGiaBan() < from || s.getGiaBan() > to) continue;
            if (ckStock.isSelected() && !s.coTheBanSoLuong(1)) continue;
            result.addRow(new Object[]{s.getMaSanPham(), s.getTenSanPham(), s.getMaVach(), s.getGiaBan(), s.getSoLuongTon(), s.baoHanhThang(), s.getTrangThai()});
        }
        lbCount.setText("Tìm thấy " + result.getRowCount() + " sản phẩm");
    }

    @Override public void refresh() { search(); }
}
