package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO xử lý bảng Classes
 * - Chỉ dùng để TRA CỨU
 * - KHÔNG tự tạo lớp
 */
public class ClassDAO {

    /**
     * Lấy ClassID theo tên lớp (ClassName)
     * Ví dụ: "25IT1"
     */
    public Integer findClassIdByClassName(String className) {

        String sql = """
            SELECT TOP 1 ClassID
            FROM Classes
            WHERE ClassName = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, className);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("ClassID");
            }

        } catch (Exception e) {
            System.err.println("❌ ClassDAO.findClassIdByClassName()");
            e.printStackTrace();
        }

        return null; // KHÔNG TÌM THẤY
    }
}
