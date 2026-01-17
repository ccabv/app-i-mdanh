package dao;

import db.DataDBConnection;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class StudentDAO {

    /* ================= LOAD ALL ================= */
    public static ArrayList<Student> getAllStudents() {

        ArrayList<Student> list = new ArrayList<>();

        String sql = """
            SELECT StudentID, MSSV, FullName, ClassName
            FROM Students
            ORDER BY MSSV
        """;

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("StudentID"),
                        rs.getString("MSSV"),
                        rs.getString("FullName"),
                        rs.getString("ClassName")
                ));
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi getAllStudents()");
            e.printStackTrace();
        }

        return list;
    }

    /* ================= FIND BY MSSV (AUTO FILL) ================= */
    public static Student findByMSSV(String mssv) {

        String sql = """
            SELECT StudentID, MSSV, FullName, ClassName
            FROM Students
            WHERE MSSV = ?
        """;

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mssv);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getInt("StudentID"),
                        rs.getString("MSSV"),
                        rs.getString("FullName"),
                        rs.getString("ClassName")
                );
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi findByMSSV()");
            e.printStackTrace();
        }

        return null;
    }

    /* ================= INSERT ================= */
    public static boolean insertStudent(String mssv, String name, String className) {

        String sql = """
            INSERT INTO Students (MSSV, FullName, ClassName)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mssv);
            ps.setString(2, name);
            ps.setString(3, className);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Lỗi insertStudent()");
            e.printStackTrace();
            return false;
        }
    }

    /* ================= DELETE ================= */
    public static boolean deleteStudent(int studentID) {

        String sql = "DELETE FROM Students WHERE StudentID = ?";

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Lỗi deleteStudent()");
            e.printStackTrace();
            return false;
        }
    }
}
