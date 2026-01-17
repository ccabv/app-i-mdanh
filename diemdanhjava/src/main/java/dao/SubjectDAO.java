package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DÙNG KHI:
 * - GV tự nhập môn (text)
 * - Camera / Manual cần SubjectID
 * - KHÔNG tạo môn mới
 */
public class SubjectDAO {

    /**
     * Tìm SubjectID theo:
     * - SubjectName (vd: "Cơ sở dữ liệu")
     * - hoặc SubjectCode (vd: "CSDL")
     */
    public Integer findSubjectIdByNameOrCode(String input) {

        String sql = """
            SELECT TOP 1 SubjectID
            FROM Subjects
            WHERE SubjectName = ?
               OR SubjectCode = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, input);
            ps.setString(2, input);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SubjectID");
            }

        } catch (Exception e) {
            System.err.println("❌ SubjectDAO.findSubjectIdByNameOrCode()");
            e.printStackTrace();
        }

        return null; // KHÔNG TÌM THẤY
    }
}
