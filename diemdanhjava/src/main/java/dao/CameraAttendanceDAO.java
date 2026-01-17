package dao;

import db.DataDBConnection;

import java.sql.*;

public class CameraAttendanceDAO {

    // Tạo buổi điểm danh (AttendanceSystem.dbo.Attendance)
    public int createAttendance(int subjectId, int classId, String room, String subjectName) {

        String sql = """
            INSERT INTO dbo.Attendance
            (SubjectID, ClassID, AttendanceType, Room, CreatedAt, CheckTime)
            OUTPUT INSERTED.AttendanceID
            VALUES (?, ?, N'CAMERA', ?, GETDATE(), GETDATE())
        """;

        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ps.setInt(2, classId);
            ps.setString(3, room);

            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;

        } catch (Exception e) {
            System.err.println("❌ createAttendance()");
            e.printStackTrace();
            return -1;
        }
    }

    // Lưu ảnh vào AttendanceDetail.Photo
    public boolean saveCameraDetail(int attendanceId, int studentId, byte[] photo) {

        String sql = """
            INSERT INTO dbo.AttendanceDetail
            (AttendanceID, StudentID, Status, CheckTime, Photo, PhotoTime)
            VALUES (?, ?, N'Có mặt', GETDATE(), ?, GETDATE())
        """;

        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, attendanceId);
            ps.setInt(2, studentId);
            ps.setBytes(3, photo);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ saveCameraDetail()");
            e.printStackTrace();
            return false;
        }
    }

    // Lấy ảnh mới nhất của SV trong buổi điểm danh hiện tại
    public byte[] getLatestPhoto(int attendanceId, int studentId) {

        String sql = """
            SELECT TOP 1 Photo
            FROM dbo.AttendanceDetail
            WHERE AttendanceID = ?
              AND StudentID = ?
              AND Photo IS NOT NULL
            ORDER BY PhotoTime DESC
        """;

        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, attendanceId);
            ps.setInt(2, studentId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBytes("Photo");
            }

        } catch (Exception e) {
            System.err.println("❌ getLatestPhoto()");
            e.printStackTrace();
        }

        return null;
    }
}
