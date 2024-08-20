
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connection;


    private static final String URL = "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms";
    private static final String USER = "\"22107453d\"@dbms";
    private static final String PASSWORD = "rbzyaypf";

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to establish a connection to the database.");
            System.exit(1);
        }
        return null;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}