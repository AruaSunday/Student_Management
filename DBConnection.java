import java.sql.*;

public class DBConnection {
    public static Connection connect() {
        try {
            // Update the username, password, and port as per your system
            String url = "jdbc:postgresql://localhost:5432/StudentManagement";
            String user = "postgres";
            String password = "Arua08141";

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
