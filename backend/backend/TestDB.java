import java.sql.*;

public class TestDB {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms_db?useSSL=false&serverTimezone=UTC", "root", "Anurag@123");
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT concept_class, COUNT(*) as cnt FROM medical_concepts GROUP BY concept_class");
        
        while (rs.next()) {
            System.out.println("Class: " + rs.getString("concept_class") + ", Count: " + rs.getInt("cnt"));
        }
        
        rs.close();
        stmt.close();
        conn.close();
    }
}
