package xiu.ui;

import DAO.*;
import ENTITY.*;
import ENUM.*;
import xiu.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/** Trang chủ: tổng quan hoạt động (doanh thu hôm nay, đơn hàng, khách hàng, biểu đồ). */
public class DashboardPanel extends JPanel implements Refreshable {
    private final JLabel lbRev = value(), lbOrders = value(), lbCustomers = value(), lbLow = value();
    private final JLabel lbPdh = value(), lbPnk = value(), lbBh = value(), lbBad = value();
    private final Charts.Bar bar = new Charts.Bar();
    private final Charts.Pie pie = new Charts.Pie();

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 14)); setBackground(Theme.BG); setBorder(new EmptyBorder(18, 22, 18, 22));
        add(Theme.title("Tổng quan hoạt động"), BorderLayout.NORTH);
        JPanel body = Theme.transparent(new BorderLayout(0, 14));
        JPanel cards = Theme.transparent(new GridLayout(2, 4, 14, 14));
        cards.add(stat("Doanh thu hôm nay", lbRev)); cards.add(stat("Đơn hàng hôm nay", lbOrders));
        cards.add(stat("Tổng khách hàng", lbCustomers)); cards.add(stat("Sản phẩm sắp hết (<10)", lbLow));
        // Việc cần xử lý
        cards.add(stat("Phiếu đặt hàng chờ xác nhận", lbPdh)); cards.add(stat("Phiếu nhập kho chờ duyệt", lbPnk));
        cards.add(stat("Phiếu bảo hành đang xử lý", lbBh)); cards.add(stat("Khách hàng VIP", lbBad));
        body.add(cards, BorderLayout.NORTH);
        JPanel charts = Theme.transparent(new GridLayout(1, 2, 14, 0));
        JPanel c1 = Theme.card(); c1.add(Theme.label("Doanh thu theo tháng (triệu đồng)"), BorderLayout.NORTH); c1.add(bar);
        JPanel c2 = Theme.card(); c2.add(Theme.label("Top sản phẩm bán chạy"), BorderLayout.NORTH); c2.add(pie);
        charts.add(c1); charts.add(c2);
        body.add(charts, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
        refresh();
    }

    private static JLabel value() { JLabel l = new JLabel("0"); l.setFont(Theme.BASE.deriveFont(Font.BOLD, 26f)); l.setForeground(Theme.NAVY); return l; }

    private JPanel stat(String t, JLabel v) {
        JPanel p = Theme.card(); p.setBorder(new EmptyBorder(18, 20, 18, 20));
        p.add(Theme.muted(t), BorderLayout.NORTH); p.add(v, BorderLayout.CENTER);
        return p;
    }

    @Override public void refresh() {
        double rev = 0; int orders = 0;
        double[] month = new double[12];
        int year = LocalDate.now().getYear();
        Map<String, Integer> sold = new HashMap<>();
        for (HoaDon h : new HoaDonDAO().getAll()) {
            if (!h.daThanhToan()) continue;
            double t = h.thanhTienDaLap();
            if (Fmt.isToday(h.getNgayLap())) { rev += t; orders++; }
            if (h.getNgayLap().getYear() == year) month[h.getNgayLap().getMonthValue() - 1] += t;
            for (ChiTietHoaDon c : h.getChiTietList()) sold.merge(c.getMaSanPham().getTenSanPham(), c.getSoLuong(), Integer::sum);
        }
        int low = 0;
        for (SanPham s : new SanPhamDAO().getAll()) if (s.sapHetHang(10)) low++;
        int pdh = 0, pnk = 0, bh = 0, bad = 0;
        for (PhieuDatHang p : new PhieuDatHangDAO().getAll()) if (!p.daXacNhan()) pdh++;
        for (PhieuNhapKho p : new PhieuNhapKhoDAO().getAll()) if (p.getTrangThai() == TrangThaiPhieuNhapKho.CHO_DUYET) pnk++;
        for (PhieuBaoHanh p : new PhieuBaoHanhDAO().getAll()) if (p.getTrangThai() == TrangThaiPhieuBaoHanh.DANG_XU_LY) bh++;
        for (KhachHang k : new KhachHangDAO().getAll())
            if (k.getLoaiKH() == LoaiKhachHang.KH_VIP) bad++;
        lbPdh.setText(String.valueOf(pdh)); lbPnk.setText(String.valueOf(pnk)); lbBh.setText(String.valueOf(bh)); lbBad.setText(String.valueOf(bad));
        lbRev.setText(Fmt.money(rev)); lbOrders.setText(String.valueOf(orders));
        lbCustomers.setText(String.valueOf(new KhachHangDAO().getAll().size())); lbLow.setText(String.valueOf(low));

        int months = LocalDate.now().getMonthValue();
        String[] ml = new String[months]; double[] mv = new double[months];
        for (int m = 0; m < months; m++) { ml[m] = "T" + (m + 1); mv[m] = month[m] / 1_000_000.0; }
        bar.setData(ml, mv, "triệu đồng");

        List<Map.Entry<String, Integer>> top = new ArrayList<>(sold.entrySet());
        top.sort((a, b) -> b.getValue() - a.getValue());
        int n = Math.min(4, top.size());
        String[] pl = new String[n + 1]; double[] pv = new double[n + 1]; double other = 0;
        for (int i = 0; i < top.size(); i++) { if (i < n) { pl[i] = top.get(i).getKey(); pv[i] = top.get(i).getValue(); } else other += top.get(i).getValue(); }
        pl[n] = "Khác"; pv[n] = other;
        pie.setData(pl, pv);
    }
}
