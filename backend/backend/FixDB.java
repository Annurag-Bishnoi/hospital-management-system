import java.sql.*;

public class FixDB {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms_db?useSSL=false&serverTimezone=UTC", "root", "Anurag@123");
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT user_id FROM users WHERE username = 'doctor1'");
        
        if (rs.next()) {
            long userId = rs.getLong("user_id");
            System.out.println("Found doctor1 user_id: " + userId);
            
            int inserted = stmt.executeUpdate("INSERT IGNORE INTO user_roles (user_id, role_id, assigned_at) VALUES (" + userId + ", 2, NOW())");
            System.out.println("Inserted rows: " + inserted);
        } else {
            System.out.println("No user found with username doctor1");
        }
        
        rs.close();
        stmt.close();
        conn.close();
    }
}
