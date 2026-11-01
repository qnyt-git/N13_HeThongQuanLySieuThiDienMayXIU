package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	private static Connection con = null;

	public static Connection getConnection(){
		try {
			if (con == null || con.isClosed()) {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

				String url = "jdbc:sqlserver://localhost:1433;"
						+ "databaseName=XIU_Store_DB;"
						+ "encrypt=false;"
						+ "trustServerCertificate=true";

				String user = "sa";
				String password = "sapassword";

				con = DriverManager.getConnection(url, user, password);
				
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

	public static void disconnect() {
		if (con != null) {
			try {
				con.close();
				System.out.println("Đã đóng kết nối!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}