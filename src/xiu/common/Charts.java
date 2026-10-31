package xiu.common;

import javax.swing.*;
import java.awt.*;

/** Biểu đồ cột và tròn tự vẽ bằng Java2D (không cần thư viện ngoài). */
public final class Charts {
    private Charts() {}

    public static class Bar extends JComponent {
        private String[] labels = {};
        private double[] values = {};
        private String unit = "";

        public Bar() { setPreferredSize(new Dimension(420, 260)); }

        public void setData(String[] labels, double[] values, String unit) {
            this.labels = labels; this.values = values; this.unit = unit; repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), left = 52, bottom = 28, top = 12;
            double max = 1; for (double v : values) max = Math.max(max, v);
            double step = Math.pow(10, Math.floor(Math.log10(max)));
            double nice = Math.ceil(max / step) * step;
            g.setFont(Theme.BASE.deriveFont(12f));
            for (int i = 0; i <= 5; i++) {
                int y = top + (h - top - bottom) * i / 5;
                g.setColor(new Color(0xE5E7EB)); g.drawLine(left, y, w - 6, y);
                g.setColor(Theme.MUTED);
                String t = String.format("%,.0f", nice * (5 - i) / 5).replace(',', '.');
                g.drawString(t, left - 8 - g.getFontMetrics().stringWidth(t), y + 4);
            }
            int n = Math.max(1, values.length);
            int slot = (w - left - 6) / n, bw = Math.max(8, slot * 6 / 10);
            for (int i = 0; i < values.length; i++) {
                int bh = (int) ((h - top - bottom) * values[i] / nice);
                int x = left + i * slot + (slot - bw) / 2, y = h - bottom - bh;
                g.setPaint(new GradientPaint(0, y, Theme.BLUE, 0, h - bottom, new Color(0x90CAF9)));
                g.fillRoundRect(x, y, bw, bh, 6, 6);
                g.setColor(Theme.TEXT);
                String lb = labels[i];
                g.drawString(lb, x + bw / 2 - g.getFontMetrics().stringWidth(lb) / 2, h - 10);
            }
            if (!unit.isEmpty()) { g.setColor(Theme.MUTED); g.drawString(unit, left + 4, top + 12); }
            g.dispose();
        }
    }

    public static class Pie extends JComponent {
        private static final Color[] PAL = {new Color(0x0D47A1), new Color(0x1976D2), new Color(0x4DB6AC),
                new Color(0x90CAF9), new Color(0xF4A3A3), new Color(0xFFD54F)};
        private String[] labels = {};
        private double[] values = {};

        public Pie() { setPreferredSize(new Dimension(420, 260)); }

        public void setData(String[] labels, double[] values) { this.labels = labels; this.values = values; repaint(); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double sum = 0; for (double v : values) sum += v;
            if (sum <= 0) { g.setColor(Theme.MUTED); g.drawString("Chưa có dữ liệu", 20, 30); g.dispose(); return; }
            int d = Math.min(getHeight() - 20, getWidth() / 2 - 10);
            int x0 = 10, y0 = (getHeight() - d) / 2;
            double start = 90;
            g.setFont(Theme.BASE.deriveFont(12f));
            for (int i = 0; i < values.length; i++) {
                double ang = -360 * values[i] / sum;
                g.setColor(PAL[i % PAL.length]);
                g.fillArc(x0, y0, d, d, (int) Math.round(start), (int) Math.round(ang) - (i == values.length - 1 ? 0 : 0));
                start += ang;
            }
            int lx = x0 + d + 24, ly = Math.max(20, getHeight() / 2 - values.length * 12);
            g.setFont(Theme.BASE.deriveFont(13f));
            for (int i = 0; i < values.length; i++) {
                g.setColor(PAL[i % PAL.length]); g.fillRoundRect(lx, ly + i * 24, 12, 12, 4, 4);
                g.setColor(Theme.TEXT);
                g.drawString(labels[i] + "  " + Math.round(values[i] * 100 / sum) + "%", lx + 20, ly + i * 24 + 11);
            }
            g.dispose();
        }
    }
}
