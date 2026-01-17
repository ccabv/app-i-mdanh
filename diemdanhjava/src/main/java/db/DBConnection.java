package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;" +
                    "databaseName=AttendanceApp;" +   // ✅ ĐÚNG
                    "encrypt=true;trustServerCertificate=true";

    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    public static Connection getConnection() throws Exception {
        Connection c = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("✅ JAVA CONNECTED TO DB = " + c.getCatalog());
        return c;
    }
}
