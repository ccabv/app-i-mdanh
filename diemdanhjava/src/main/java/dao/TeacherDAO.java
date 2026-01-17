package dao;

import db.DataDBConnection;
import model.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TeacherDAO {

    /* ================= LOAD ================= */
    public static ArrayList<Teacher> getAllTeachers() {
        ArrayList<Teacher> list = new ArrayList<>();

        String sql =
                "SELECT TeacherID, TeacherCode, FullName " +
                        "FROM Teachers ORDER BY TeacherCode";

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Teacher(
                        rs.getInt("TeacherID"),
                        rs.getString("TeacherCode"),
                        rs.getString("FullName")
                ));
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi getAllTeachers()");
            e.printStackTrace();
        }
        return list;
    }

    /* ================= INSERT ================= */
    public static boolean insertTeacher(String code, String name) {

        String sql =
                "INSERT INTO Teachers (TeacherCode, FullName) VALUES (?, ?)";

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, name);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Lỗi insertTeacher()");
            e.printStackTrace();
            return false;
        }
    }

    /* ================= DELETE ================= */
    public static boolean deleteTeacher(int teacherID) {

        String sql = "DELETE FROM Teachers WHERE TeacherID = ?";

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teacherID);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Lỗi deleteTeacher()");
            e.printStackTrace();
            return false;
        }
    }
}
