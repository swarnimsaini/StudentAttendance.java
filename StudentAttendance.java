
import java.sql.*;
import java.util.Scanner;

public class StudentAttendance {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String url = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";
        String user = "SYSTEM";
        String password = "Sw.sa@3006";
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");
            Statement st = con.createStatement();

            try {
                st.executeUpdate(
                    "CREATE TABLE StudentAttendance (" +
                    "student_id NUMBER, " +
                    "name VARCHAR2(50), " +
                    "att_date DATE, " +
                    "status VARCHAR2(10), " +
                    "PRIMARY KEY (student_id, att_date))"
                );
                System.out.println("Table created successfully.");
            } catch (SQLException e) {
                if (e.getErrorCode() == 955) {
                    System.out.println("Table already exists.");
                } else {
                    throw e;
                }
            }

            con.setAutoCommit(false);
            String sql = "INSERT INTO StudentAttendance VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            System.out.print("Enter attendance date: ");
            String date = sc.nextLine();
            System.out.print("Enter number of students: ");
            int n = sc.nextInt();
            sc.nextLine();

            for (int i = 0; i < n; i++) {
                System.out.print("Enter student ID: ");
                int id = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter student name: ");
                String name = sc.nextLine();
                System.out.print("Enter attendance status: ");
                String status = sc.nextLine();
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setDate(3, Date.valueOf(date));
                ps.setString(4, status);
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("Attendance recorded successfully!");

            ResultSet rs = st.executeQuery(
                "SELECT student_id, name, att_date, status " +
                "FROM StudentAttendance ORDER BY student_id, att_date"
            );

            while (rs.next()) {
                System.out.println("Student ID: " + rs.getInt("student_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Date: " + rs.getDate("att_date"));
                System.out.println("Status: " + rs.getString("status"));
            }

            rs.close();
            ResultSet rs2 = st.executeQuery(
                "SELECT student_id, name, " +
                "COUNT(CASE WHEN UPPER(status) = 'PRESENT' THEN 1 END) AS present_days, " +
                "COUNT(*) AS total_days, " +
                "ROUND(COUNT(CASE WHEN UPPER(status) = 'PRESENT' THEN 1 END) " +
                "* 100.0 / COUNT(*), 2) AS percentage " +
                "FROM StudentAttendance GROUP BY student_id, name"
            );
            while (rs2.next()) {
                System.out.println("Student ID: " + rs2.getInt("student_id"));
                System.out.println("Name: " + rs2.getString("name"));
                System.out.println("Present Days: " + rs2.getInt("present_days"));
                System.out.println("Total Days: " + rs2.getInt("total_days"));
                System.out.println("Attendance: " + rs2.getDouble("percentage") + "%");
            }

            rs2.close();
            ps.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        sc.close();
    }
}





