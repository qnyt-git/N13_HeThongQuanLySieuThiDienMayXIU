package xiu.common;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/** Màu sắc, font và các thành phần giao diện dùng chung để đảm bảo tính nhất quán. */
public final class Theme {
    private Theme() {}

    public static final Color NAVY = new Color(0x0B2447), BLUE = new Color(0x1565C0), BG = new Color(0xF3F6FB),
            BORDER = new Color(0xDDE3EC), TEXT = new Color(0x1F2937), MUTED = new Color(0x6B7280),
            GREEN = new Color(0x15803D), GREEN_BG = new Color(0xDCFCE7), RED = new Color(0xB91C1C),
            RED_BG = new Color(0xFEE2E2), AMBER = new Color(0x92400E), AMBER_BG = new Color(0xFEF3C7);

    public static final Font BASE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD = BASE.deriveFont(Font.BOLD);
    public static final Font TITLE = BASE.deriveFont(Font.BOLD, 22f);

    private static final Set<String> GOOD = new HashSet<>(List.of("Hoạt động", "Đã thanh toán", "Đã nhập", "Đã giao",
            "Đang áp dụng", "Đang bán", "Đã xác nhận", "Đã làm", "Đã trả", "Đang làm", "Còn hàng", "Khách tốt", "Đã giao"));
    private static final Set<String> BAD = new HashSet<>(List.of("Tạm khóa", "Đã hủy", "Ngừng", "Ngừng bán", "Nghỉ",
            "Nghỉ việc", "Quá hạn", "Hết hàng"));
    private static final Set<String> WARN = new HashSet<>(List.of("Chờ xử lý", "Chưa thanh toán", "Chờ xác nhận",
            "Đã phân", "Đang xử lý", "Chờ duyệt", "Khách mới"));
    private static final Set<String> MONEY_COLS = new HashSet<>(List.of("Giá bán", "Lương cơ bản", "Tổng tiền",
            "Thành tiền", "Đơn giá", "Giảm giá"));

    public static void install() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
        for (Object k : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(k) instanceof FontUIResource) UIManager.put(k, new FontUIResource(BASE));
        }
        UIManager.put("MenuBar.background", NAVY);
        UIManager.put("MenuBar.border", BorderFactory.createEmptyBorder());
        UIManager.put("Menu.background", NAVY);
        UIManager.put("Menu.foreground", Color.WHITE);
        UIManager.put("Menu.selectionBackground", BLUE);
        UIManager.put("Menu.selectionForeground", Color.WHITE);
        UIManager.put("Menu.acceleratorForeground", MUTED);
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("OptionPane.messageFont", new FontUIResource(BASE));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(BASE));
    }

    // ---------------- Buttons ----------------
    public static class Btn extends JButton {
        private final Color bg;
        private final boolean outline;
        private boolean hover;

        Btn(String text, Color bg, Color fg, boolean outline) {
            super(text);
            this.bg = bg; this.outline = outline;
            setForeground(fg); setFont(BOLD);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            if (!isEnabled()) {
                g2.setColor(new Color(0xE2E8F0)); g2.fillRoundRect(0, 0, w, h, 10, 10);
            } else if (outline) {
                g2.setColor(hover ? new Color(0xEFF6FF) : Color.WHITE); g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setColor(bg); g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            } else {
                Color c = getModel().isPressed() ? bg.darker() : hover ? bg.brighter() : bg;
                g2.setColor(c); g2.fillRoundRect(0, 0, w, h, 10, 10);
            }
            g2.dispose();
            if (!isEnabled()) { Color f = getForeground(); setForeground(MUTED); super.paintComponent(g); setForeground(f); }
            else super.paintComponent(g);
        }
    }

    public static JButton primary(String t) { return new Btn(t, BLUE, Color.WHITE, false); }
    public static JButton danger(String t) { return new Btn(t, new Color(0xDC2626), Color.WHITE, false); }
    public static JButton success(String t) { return new Btn(t, new Color(0x16A34A), Color.WHITE, false); }
    public static JButton ghost(String t) { return new Btn(t, BLUE, BLUE, true); }

    // ---------------- Inputs / labels ----------------
    public static JTextField field(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(BASE);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    public static JLabel title(String t) {
        JLabel l = new JLabel(t); l.setFont(TITLE); l.setForeground(NAVY); return l;
    }

    public static JLabel label(String t) {
        JLabel l = new JLabel(t); l.setFont(BASE); l.setForeground(TEXT); return l;
    }

    public static JLabel muted(String t) {
        JLabel l = new JLabel(t); l.setFont(BASE); l.setForeground(MUTED); return l;
    }

    public static JPanel card() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(14, 16, 14, 16)));
        return p;
    }

    public static JPanel page() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18, 22, 18, 22));
        return p;
    }

    public static JPanel transparent(LayoutManager lm) { JPanel p = new JPanel(lm); p.setOpaque(false); return p; }

    // ---------------- Tables ----------------
    /** Model bảng chỉ đọc; kiểu cột lấy theo giá trị đầu tiên để sắp xếp số đúng. */
    public static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                for (int r = 0; r < getRowCount(); r++) { Object v = getValueAt(r, c); if (v != null) return v.getClass(); }
                return Object.class;
            }
        };
    }

    public static JTable table(TableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(34);
        t.setShowVerticalLines(false);
        t.setGridColor(new Color(0xEDF1F7));
        t.setSelectionBackground(new Color(0xDBEAFE));
        t.setSelectionForeground(TEXT);
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);
        t.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 38));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, false, false, r, c);
                l.setFont(BOLD); l.setForeground(NAVY); l.setBackground(new Color(0xF1F5F9));
                l.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, BORDER), new EmptyBorder(0, 8, 0, 8)));
                return l;
            }
        });
        t.setDefaultRenderer(Object.class, new CellRenderer());
        t.setDefaultRenderer(Number.class, new CellRenderer());
        applyRenderers(t);
        sizeColumns(t);
        return t;
    }

    private static final List<String> WIDE = List.of("Tên", "Nhân viên", "Khách hàng", "Địa chỉ", "Ghi chú", "Nội dung", "Tình trạng", "Lý do", "Email", "Nhà cung cấp", "Ca làm việc");

    /** Cột văn bản dài rộng hơn, cột mã/số gọn hơn. */
    private static void sizeColumns(JTable t) {
        for (int i = 0; i < t.getColumnCount(); i++) {
            String n = t.getColumnName(i);
            boolean wide = WIDE.stream().anyMatch(n::startsWith);
            int w = wide ? 240 : MONEY_COLS.contains(n) ? 130 : n.startsWith("Mã") || n.equals("STT") ? 90 : Math.max(100, n.length() * 11);
            t.getColumnModel().getColumn(i).setPreferredWidth(w);
        }
    }

    public static JScrollPane scroll(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    /** Gán renderer theo tên cột: trạng thái (huy hiệu màu), tiền (định dạng đ), mật khẩu (ẩn). */
    public static void applyRenderers(JTable t) {
        for (int i = 0; i < t.getColumnCount(); i++) {
            String n = t.getColumnName(i);
            TableColumn col = t.getColumnModel().getColumn(i);
            if (n.equals("Trạng thái")) col.setCellRenderer(new CellRenderer(Mode.STATUS));
            else if (MONEY_COLS.contains(n)) col.setCellRenderer(new CellRenderer(Mode.MONEY));
            else if (n.equals("Mật khẩu")) col.setCellRenderer(new CellRenderer(Mode.MASK));
        }
    }

    private enum Mode { PLAIN, STATUS, MONEY, MASK }

    private static class CellRenderer extends DefaultTableCellRenderer {
        private final Mode mode;
        CellRenderer() { this(Mode.PLAIN); }
        CellRenderer(Mode m) { this.mode = m; }

        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            String text = v == null ? "" : v.toString();
            if (mode == Mode.MONEY && v instanceof Number n) text = Fmt.money(n);
            if (mode == Mode.MASK) text = "••••••";
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, text, sel, false, r, c);
            l.setBorder(new EmptyBorder(0, 8, 0, 8));
            l.setHorizontalAlignment(mode == Mode.MONEY ? RIGHT : LEFT);
            l.setForeground(TEXT);
            if (mode == Mode.STATUS) {
                Color fg = GOOD.contains(text) ? GREEN : BAD.contains(text) ? RED : WARN.contains(text) ? AMBER : null;
                Color bg = GOOD.contains(text) ? GREEN_BG : BAD.contains(text) ? RED_BG : WARN.contains(text) ? AMBER_BG : null;
                if (fg != null) {
                    JLabel badge = new JLabel(text, SwingConstants.CENTER) {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.dispose(); super.paintComponent(g);
                        }
                    };
                    badge.setOpaque(false); badge.setBackground(bg); badge.setForeground(fg); badge.setFont(BOLD.deriveFont(12.5f));
                    JPanel wrap = new JPanel(new GridBagLayout());
                    wrap.setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
                    wrap.setBorder(new EmptyBorder(0, 6, 0, 6));
                    GridBagConstraints g = new GridBagConstraints();
                    g.fill = GridBagConstraints.BOTH; g.weightx = 1; g.weighty = 1; g.insets = new Insets(6, 0, 6, 0);
                    wrap.add(badge, g);
                    return wrap;
                }
            }
            return l;
        }
    }
}
