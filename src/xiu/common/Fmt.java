package xiu.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Tiện ích định dạng tiền, ngày giờ. */
public final class Fmt {
    private Fmt() {}

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private static final DecimalFormat DF;
    static {
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setGroupingSeparator('.');
        s.setDecimalSeparator(',');
        DF = new DecimalFormat("#,##0", s);
    }

    public static String money(Number n) { return DF.format(n.longValue()) + "đ"; }

    public static LocalDate parseDate(String s) { return LocalDate.parse(s.trim(), DATE); }

    public static String date(LocalDate d) { return d.format(DATE); }

    public static String date(java.time.LocalDateTime d) { return d.format(DATE); }

    public static String dateTime(java.time.LocalDateTime d) { return d.format(DATETIME); }

    public static boolean isToday(java.time.LocalDateTime d) { return d.toLocalDate().equals(LocalDate.now()); }

    public static String time(java.time.LocalDateTime d) { return d.format(TIME); }

    /** "NV001 - Nguyễn Văn An" -> "NV001". */
    public static String code(String s) {
        if (s == null) return "";
        int i = s.indexOf(" - ");
        return i < 0 ? s.trim() : s.substring(0, i).trim();
    }
}
