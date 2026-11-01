package xiu.ui;

import DAO.IDAO;
import xiu.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Màn hình danh mục dùng chung cho một lớp entity T: bảng + tìm kiếm + Thêm/Sửa/Xóa trong hộp thoại nhập liệu.
 * - dao    : truy cập dữ liệu (getAll/them/capNhat/xoa/taoMaMoi)
 * - row    : chuyển entity thành một dòng bảng (cột đầu là mã)
 * - create : tạo entity rỗng với mã mới; apply : ghi giá trị form vào entity bằng setter
 */
public class CrudPanel<T> extends JPanel implements Refreshable {
    public enum Kind { TEXT, NAME, PHONE, EMAIL, INT_POS, INT_NONNEG, MONEY, DECIMAL, DATE, TIME, COMBO, AREA, PASSWORD }

    /** Mô tả một trường nhập liệu. */
    public static class F {
        final String label; final Kind kind;
        Supplier<Object[]> opts = () -> new Object[0];
        boolean required = true; String regex, regexMsg; Supplier<String> def;

        private F(String label, Kind kind) { this.label = label; this.kind = kind; }
        public static F of(String label, Kind kind) { return new F(label, kind); }
        public F opts(Object... o) { this.opts = () -> o; return this; }
        public F opts(Supplier<Object[]> s) { this.opts = s; return this; }
        public F optional() { this.required = false; return this; }
        public F regex(String re, String msg) { this.regex = re; this.regexMsg = msg; return this; }
        public F def(Supplier<String> d) { this.def = d; return this; }
    }

    protected List<T> repo;
    protected final IDAO<T> dao;
    protected final DefaultTableModel model;
    protected final JTable table;
    protected final List<F> fields;
    private final String title;
    private final Function<T, Object[]> row;
    private final Function<String, T> create;
    private final BiConsumer<T, Object[]> apply;
    private final JTextField search = Theme.field(24);
    private final JPanel buttons = Theme.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final JLabel count = Theme.muted("");

    public CrudPanel(String title, IDAO<T> dao, String[] columns, Function<T, Object[]> row,
                     List<F> fields, Function<String, T> create, BiConsumer<T, Object[]> apply) {
        this.title = title; this.dao = dao; this.row = row;
        this.fields = fields; this.create = create; this.apply = apply;
        setLayout(new BorderLayout(0, 12)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));

        model = Theme.model(columns);
        reload();
        table = Theme.table(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        search.getDocument().addDocumentListener(new DocumentListener() {
            void f() {
                String t = search.getText().trim();
                sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(t)));
                updateCount();
            }
            public void insertUpdate(DocumentEvent e) { f(); }
            public void removeUpdate(DocumentEvent e) { f(); }
            public void changedUpdate(DocumentEvent e) { f(); }
        });

        JButton add = Theme.primary("+ Thêm mới"), edit = Theme.ghost("Sửa"), del = Theme.danger("Xóa");
        add.addActionListener(e -> openForm(null));
        edit.addActionListener(e -> { T t = selected(); if (t != null) openForm(t); });
        del.addActionListener(e -> deleteSelected());
        buttons.add(edit); buttons.add(del); buttons.add(add);

        JPanel bar = Theme.transparent(new BorderLayout());
        JPanel left = Theme.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.add(Theme.label("Tìm kiếm:")); left.add(search);
        bar.add(left, BorderLayout.WEST); bar.add(buttons, BorderLayout.EAST);

        JPanel top = Theme.transparent(new BorderLayout(0, 12));
        top.add(Theme.title(title), BorderLayout.NORTH); top.add(bar, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
        add(Theme.scroll(table), BorderLayout.CENTER);
        add(count, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) { T t = selected(); if (t != null) openForm(t); } }
        });
        updateCount();
    }

    // ---------------- Móc nối cho lớp con / nơi gọi ----------------
    /** Thêm nút chức năng phụ lên thanh công cụ. */
    protected void addButton(JButton b) { buttons.add(b, 0); }

    /** Kiểm tra bổ sung ngoài miền giá trị. v[0] = mã, v[1..] = giá trị các trường. Trả về lỗi hoặc null. */
    protected String validateRow(Object[] v, T editing) { return null; }

    /** Trả về thông báo nếu không được phép xóa. */
    protected String checkDelete(T t) { return null; }

    /** Giá trị ở cột col của entity khác (không tính except) có trùng val không. */
    protected boolean dup(int col, Object val, T except) {
        for (T t : repo)
            if (t != except && row.apply(t)[col].toString().equalsIgnoreCase(val.toString())) return true;
        return false;
    }

    @Override public void refresh() { reload(); }

    protected final void reload() {
        repo = dao.getAll();
        model.setRowCount(0);
        for (T t : repo) model.addRow(row.apply(t));
        if (table != null) updateCount();
    }

    private void updateCount() { count.setText("Hiển thị " + table.getRowCount() + " / " + model.getRowCount() + " bản ghi"); }

    private T selected() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng trong bảng", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return null; }
        return repo.get(table.convertRowIndexToModel(r));
    }

    private void deleteSelected() {
        T t = selected(); if (t == null) return;
        String err = checkDelete(t);
        if (err != null) { JOptionPane.showMessageDialog(this, err, "Không thể xóa", JOptionPane.WARNING_MESSAGE); return; }
        Object[] r = row.apply(t);
        if (JOptionPane.showConfirmDialog(this, "Xóa " + r[0] + " - " + r[1] + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (!dao.xoa(t)) JOptionPane.showMessageDialog(this, "Không xóa được dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            reload();
        }
    }

    // ---------------- Form nhập liệu ----------------
    private void openForm(T editing) {
        boolean edit = editing != null;
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), (edit ? "Cập nhật " : "Thêm mới ") + title.toLowerCase(), Dialog.ModalityType.APPLICATION_MODAL);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(18, 22, 10, 22));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 4, 6, 4); g.anchor = GridBagConstraints.WEST;

        Object[] cur = edit ? row.apply(editing) : null;
        String id = edit ? cur[0].toString() : dao.taoMaMoi();
        JTextField idf = Theme.field(24); idf.setText(id); idf.setEnabled(false);
        addRow(p, g, 0, model.getColumnName(0) + " (tự động)", idf);

        JComponent[] comps = new JComponent[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            F f = fields.get(i);
            Object init = edit ? cur[i + 1] : (f.def != null ? f.def.get() : null);
            comps[i] = makeComp(f, init);
            addRow(p, g, i + 1, f.label + (f.required ? " *" : ""), comps[i] instanceof JTextArea ? Theme.scroll(comps[i]) : comps[i]);
        }

        JLabel err = new JLabel(" "); err.setForeground(Theme.RED);
        JButton save = Theme.primary("Lưu"), cancel = Theme.ghost("Hủy");
        cancel.addActionListener(e -> d.dispose());
        JPanel south = new JPanel(new BorderLayout()); south.setBackground(Color.WHITE); south.setBorder(new EmptyBorder(0, 22, 16, 22));
        JPanel bt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); bt.setBackground(Color.WHITE); bt.add(cancel); bt.add(save);
        south.add(err, BorderLayout.NORTH); south.add(bt, BorderLayout.SOUTH);

        save.addActionListener(e -> {
            Object[] vals = new Object[fields.size() + 1]; vals[0] = id;
            for (int i = 0; i < fields.size(); i++) {
                Object raw = read(comps[i]);
                String m = validate(fields.get(i), raw);
                if (m != null) { err.setText(m); comps[i].requestFocusInWindow(); return; }
                vals[i + 1] = convert(fields.get(i), raw);
            }
            String m = validateRow(vals, editing);
            if (m != null) { err.setText(m); return; }
            boolean ok;
            if (edit) { apply.accept(editing, vals); ok = dao.capNhat(editing); }
            else { T t = create.apply(id); apply.accept(t, vals); ok = dao.them(t); }
            if (!ok) { err.setText("Không lưu được dữ liệu, vui lòng thử lại"); reload(); return; }
            reload();
            d.dispose();
        });

        d.getRootPane().setDefaultButton(save);
        d.add(p, BorderLayout.CENTER); d.add(south, BorderLayout.SOUTH);
        d.getContentPane().setBackground(Color.WHITE);
        d.pack(); d.setMinimumSize(new Dimension(480, 0)); d.setLocationRelativeTo(this); d.setVisible(true);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent c) {
        g.gridy = row; g.gridx = 0; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        JLabel l = Theme.label(label); l.setPreferredSize(new Dimension(150, 28)); p.add(l, g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL; p.add(c, g);
    }

    /** Trường ngày có nút lịch, không cần thư viện bên ngoài. */
    private static class DateInput extends JPanel {
        private final JTextField text = Theme.field(18);
        DateInput(String value) {
            super(new BorderLayout(5, 0));
            setOpaque(false);
            text.setText(value == null ? "" : value);
            JButton calendar = Theme.ghost("Chọn ngày");
            calendar.addActionListener(e -> {
                java.util.Date initial = new java.util.Date();
                try { initial = java.util.Date.from(Fmt.parseDate(text.getText().trim()).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()); }
                catch (Exception ignored) { }
                JSpinner picker = new JSpinner(new SpinnerDateModel(initial, null, null, java.util.Calendar.DAY_OF_MONTH));
                picker.setEditor(new JSpinner.DateEditor(picker, "dd/MM/yyyy"));
                int result = JOptionPane.showConfirmDialog(this, picker, "Chọn ngày", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    java.util.Date d = (java.util.Date) picker.getValue();
                    text.setText(new java.text.SimpleDateFormat("dd/MM/yyyy").format(d));
                }
            });
            add(text, BorderLayout.CENTER); add(calendar, BorderLayout.EAST);
        }
        String getText() { return text.getText().trim(); }
    }

    private JComponent makeComp(F f, Object init) {
        String s = init == null ? "" : init.toString();
        if (init instanceof Number n) s = f.kind == Kind.MONEY ? String.valueOf(n.longValue()) : f.kind.name().startsWith("INT") ? String.valueOf(n.intValue()) : n.toString();
        switch (f.kind) {
            case DATE: return new DateInput(s);
            case COMBO: {
                JComboBox<Object> cb = new JComboBox<>(f.opts.get());
                cb.setFont(Theme.BASE); cb.setPreferredSize(new Dimension(280, 34));
                if (init != null) cb.setSelectedItem(init);
                else if (cb.getItemCount() > 0) cb.setSelectedIndex(0);
                return cb;
            }
            case AREA: {
                JTextArea a = new JTextArea(s, 3, 24); a.setFont(Theme.BASE); a.setLineWrap(true); a.setWrapStyleWord(true); return a;
            }
            case PASSWORD: {
                JPasswordField pf = new JPasswordField(24); pf.setFont(Theme.BASE); pf.setText(s);
                pf.setBorder(Theme.field(1).getBorder()); return pf;
            }
            default: {
                JTextField t = Theme.field(24); t.setText(s);
                if (f.kind == Kind.DATE) t.setToolTipText("dd/MM/yyyy");
                if (f.kind == Kind.TIME) t.setToolTipText("HH:mm");
                return t;
            }
        }
    }

    private Object read(JComponent c) {
        if (c instanceof DateInput d) return d.getText();
        if (c instanceof JComboBox<?> cb) return cb.getSelectedItem();
        if (c instanceof JPasswordField pf) return new String(pf.getPassword());
        if (c instanceof javax.swing.text.JTextComponent t) return t.getText().trim();
        return "";
    }

    private String validate(F f, Object raw) {
        if (f.kind == Kind.COMBO) return raw == null && f.required ? "Vui lòng chọn " + f.label.toLowerCase() : null;
        String s = raw.toString();
        if (s.isEmpty()) return f.required ? f.label + " không được để trống" : null;
        try {
            switch (f.kind) {
                case NAME: if (!s.matches("^\\p{L}+( \\p{L}+)*$")) return f.label + " chỉ chứa chữ cái và khoảng trắng"; break;
                case PHONE: if (!s.matches("^0\\d{9}$")) return f.label + " phải gồm 10 chữ số, bắt đầu bằng 0"; break;
                case EMAIL: if (!s.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) return "Email không đúng định dạng"; break;
                case INT_POS: if (Integer.parseInt(s) <= 0) return f.label + " phải lớn hơn 0"; break;
                case INT_NONNEG: if (Integer.parseInt(s) < 0) return f.label + " phải lớn hơn hoặc bằng 0"; break;
                case MONEY: if (Long.parseLong(s) <= 0) return f.label + " phải lớn hơn 0"; break;
                case DECIMAL: if (Double.parseDouble(s) <= 0) return f.label + " phải lớn hơn 0"; break;
                case DATE: Fmt.parseDate(s); break;
                case TIME: LocalTime.parse(s, Fmt.TIME); break;
                case PASSWORD: if (s.length() < 6) return "Mật khẩu tối thiểu 6 ký tự"; break;
                default: break;
            }
        } catch (NumberFormatException ex) {
            return f.label + " phải là số hợp lệ";
        } catch (java.time.format.DateTimeParseException ex) {
            return f.label + (f.kind == Kind.DATE ? " không hợp lệ (dd/MM/yyyy)" : " không hợp lệ (HH:mm)");
        }
        if (f.regex != null && !s.matches(f.regex)) return f.regexMsg;
        return null;
    }

    private Object convert(F f, Object raw) {
        if (f.kind == Kind.COMBO) return raw;
        String s = raw.toString();
        if (s.isEmpty()) return "";
        switch (f.kind) {
            case INT_POS: case INT_NONNEG: return Integer.parseInt(s);
            case MONEY: return Long.parseLong(s);
            case DECIMAL: return Double.parseDouble(s);
            case DATE: return Fmt.date(Fmt.parseDate(s));
            case TIME: return LocalTime.parse(s, Fmt.TIME).format(Fmt.TIME);
            default: return s;
        }
    }
}
