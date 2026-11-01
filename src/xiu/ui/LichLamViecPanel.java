package xiu.ui;

import DAO.*;
import ENTITY.*;
import ENUM.*;
import xiu.common.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Lịch làm việc theo tuần: hàng = ca, cột = ngày, ô = nhân viên được phân ca. */
public class LichLamViecPanel extends JPanel implements Refreshable {
    private LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
    private final JLabel lbWeek = new JLabel();
    private final DefaultTableModel model = new DefaultTableModel() {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tb = new JTable(model);

    public LichLamViecPanel() {
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        JPanel top = Theme.transparent(new BorderLayout(0, 12));
        top.add(Theme.title("Lịch làm việc"), BorderLayout.NORTH);
        JPanel nav = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton prev = Theme.ghost("‹ Tuần trước"), next = Theme.ghost("Tuần sau ›"), now = Theme.primary("Tuần này");
        prev.addActionListener(e -> { monday = monday.minusWeeks(1); refresh(); });
        next.addActionListener(e -> { monday = monday.plusWeeks(1); refresh(); });
        now.addActionListener(e -> { monday = LocalDate.now().with(DayOfWeek.MONDAY); refresh(); });
        lbWeek.setFont(Theme.BOLD); lbWeek.setForeground(Theme.NAVY); lbWeek.setBorder(new EmptyBorder(0, 12, 0, 0));
        nav.add(prev); nav.add(now); nav.add(next); nav.add(lbWeek);
        top.add(nav, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        tb.setRowHeight(110); tb.setFillsViewportHeight(true); tb.setGridColor(Theme.BORDER); tb.setShowGrid(true);
        tb.getTableHeader().setReorderingAllowed(false); tb.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tb.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, r, c);
                l.setHorizontalAlignment(CENTER); l.setFont(Theme.BOLD); l.setForeground(Color.WHITE); l.setBackground(Theme.NAVY);
                l.setOpaque(true); l.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
                return l;
            }
        });
        tb.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, r, c);
                l.setVerticalAlignment(TOP); l.setBorder(new EmptyBorder(8, 10, 8, 10));
                l.setBackground(c == 0 ? new Color(0xF1F5F9) : Color.WHITE); l.setOpaque(true);
                return l;
            }
        });
        add(Theme.scroll(tb), BorderLayout.CENTER);
        refresh();
    }

    @Override public void refresh() {
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        String[] cols = new String[8]; cols[0] = "Ca làm việc";
        for (int i = 0; i < 7; i++) cols[i + 1] = days[i] + " (" + monday.plusDays(i).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM")) + ")";
        model.setColumnIdentifiers(cols);
        model.setRowCount(0);
        List<PhanCa> phanCas = new PhanCaDAO().getAll();
        for (CaLamViec ca : new CaLamViecDAO().getAll()) {
            Object[] row = new Object[8];
            row[0] = "<html><b>" + ca.getTenCa() + "</b><br>" + ca.gioBatDauText() + " - " + ca.gioKetThucText() + "</html>";
            for (int d = 0; d < 7; d++) {
                LocalDate date = monday.plusDays(d);
                List<String> names = new ArrayList<>();
                for (PhanCa p : phanCas) {
                    if (!p.getMaCa().equals(ca) || !p.getNgayLam().equals(date)) continue;
                    String nv = p.getMaNhanVien().getHoTenNhanVien();
                    names.add(p.daNghi() ? "<s><font color='#B91C1C'>" + nv + "</font></s>" : nv);
                }
                row[d + 1] = names.isEmpty() ? "" : "<html>" + String.join("<br>", names) + "</html>";
            }
            model.addRow(row);
        }
        lbWeek.setText("Tuần " + Fmt.date(monday) + " - " + Fmt.date(monday.plusDays(6)));
        tb.getColumnModel().getColumn(0).setPreferredWidth(130);
    }
}
