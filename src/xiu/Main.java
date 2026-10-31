package xiu;

import DAO.ConnectDB;
import xiu.common.Theme;
import xiu.ui.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        Theme.install();
        // Kiểm tra kết nối SQL Server trước khi mở màn hình đăng nhập
//        String loi = ConnectDB.kiemTra();
//        if (loi != null) {
//            JOptionPane.showMessageDialog(null,
//                    "Không kết nối được SQL Server:\n" + loi
//                    + "\n\nKiểm tra: SQL Server đã chạy và bật TCP/IP (cổng 1433), đã chạy script XIU_Store.sql,"
//                    + "\nthông tin trong file db.properties đúng, và thư mục lib/ có file mssql-jdbc-xx.jar.",
//                    "Lỗi kết nối CSDL", JOptionPane.ERROR_MESSAGE);
//            System.exit(1);
//        }
        Runtime.getRuntime().addShutdownHook(new Thread(ConnectDB::disconnect));
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
