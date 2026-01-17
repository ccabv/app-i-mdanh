package dao;

import db.DataDBConnection; // DB AttendanceSystem
import model.AttendanceRecord;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    private static final DateTimeFormatter F =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /* =========================================================
       1️⃣ TẠO QR SESSION (FIX CHUẨN JDBC)
       ========================================================= */
    public static Integer createQRSession(String subject, String room) {

        String sql = """
            INSERT INTO dbo.QRSession (Subject, Room, CreatedAt)
            OUTPUT INSERTED.QRSessionID
            VALUES (?, ?, GETDATE())
        """;

        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // ✅ PHẢI SET PARAM TRƯỚC
            ps.setString(1, subject);
            ps.setString(2, room);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }

        } catch (Exception e) {
            System.err.println("❌ createQRSession()");
            e.printStackTrace();
            return null;
        }
    }

    /* =========================================================
       2️⃣ SINH VIÊN QUÉT QR
       ========================================================= */
    public static boolean insertAttendanceByQR(int sessionId, String mssv) {

        String sql = """
            INSERT INTO dbo.AttendanceDetail (QRSessionID, StudentID, Status, CheckTime)
            SELECT ?, s.StudentID, N'Có mặt', GETDATE()
            FROM dbo.Students s
            WHERE s.MSSV = ?
              AND NOT EXISTS (
                  SELECT 1
                  FROM dbo.AttendanceDetail ad
                  WHERE ad.QRSessionID = ?
                    AND ad.StudentID = s.StudentID
              )
        """;

        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ps.setString(2, mssv);
            ps.setInt(3, sessionId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ insertAttendanceByQR()");
            e.printStackTrace();
            return false;
        }
    }

    /* =========================================================
       3️⃣ LOAD THEO QR SESSION
       ========================================================= */
    public static List<AttendanceRecord> getAttendanceBySession(int sessionId) {

        List<AttendanceRecord> list = new ArrayList<>();

        String sql = """
            SELECT
                s.MSSV,
                s.FullName,
                s.ClassName,
                qs.Subject,
                qs.Room,
                ad.Status,
                ad.CheckTime
            FROM dbo.AttendanceDetail ad
            JOIN dbo.Students s   ON s.StudentID = ad.StudentID
            JOIN dbo.QRSession qs ON qs.QRSessionID = ad.QRSessionID
            WHERE ad.QRSessionID = ?
            ORDER BY ad.CheckTime DESC
        """;

        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("CheckTime");

                    list.add(new AttendanceRecord(
                            rs.getString("MSSV"),
                            rs.getString("FullName"),
                            rs.getString("ClassName"),
                            rs.getString("Subject"),
                            rs.getString("Room"),
                            rs.getString("Status"),
                            ts == null ? "" : ts.toLocalDateTime().format(F)
                    ));
                }
            }

        } catch (Exception e) {
            System.err.println("❌ getAttendanceBySession()");
            e.printStackTrace();
        }

        return list;
    }

    /* =========================================================
       4️⃣ LẤY SESSION MỚI NHẤT
       ========================================================= */
    public static Integer getLatestQRSessionId() {

        String sql = """
            SELECT TOP 1 QRSessionID
            FROM dbo.QRSession
            ORDER BY CreatedAt DESC
        """;

        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : null;

        } catch (Exception e) {
            System.err.println("❌ getLatestQRSessionId()");
            e.printStackTrace();
            return null;
        }
    }

    /* =========================================================
       5️⃣ THỐNG KÊ TOÀN BỘ
       ========================================================= */
    public static List<AttendanceRecord> getAllAttendanceStatistics() {

        List<AttendanceRecord> list = new ArrayList<>();

        String sql = """
            SELECT
                s.MSSV,
                s.FullName,
                s.ClassName,
                qs.Subject,
                qs.Room,
                ad.Status,
                ad.CheckTime
            FROM dbo.AttendanceDetail ad
            JOIN dbo.Students s   ON s.StudentID = ad.StudentID
            JOIN dbo.QRSession qs ON qs.QRSessionID = ad.QRSessionID
            ORDER BY ad.CheckTime DESC
        """;

        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("CheckTime");

                list.add(new AttendanceRecord(
                        rs.getString("MSSV"),
                        rs.getString("FullName"),
                        rs.getString("ClassName"),
                        rs.getString("Subject"),
                        rs.getString("Room"),
                        rs.getString("Status"),
                        ts == null ? "" : ts.toLocalDateTime().format(F)
                ));
            }

        } catch (Exception e) {
            System.err.println("❌ getAllAttendanceStatistics()");
            e.printStackTrace();
        }

        return list;
    }

    /* =========================================================
       6️⃣ COUNTS
       ========================================================= */
    public static int countTotalAttendanceDetails() {
        return count("SELECT COUNT(*) FROM dbo.AttendanceDetail");
    }

    public static int countStudents() {
        return count("SELECT COUNT(*) FROM dbo.Students");
    }

    public static int countSubjects() {
        return count("SELECT COUNT(DISTINCT Subject) FROM dbo.QRSession");
    }

    private static int count(String sql) {
        try (Connection c = DataDBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (Exception e) {
            System.err.println("❌ count(): " + sql);
            e.printStackTrace();
            return 0;
        }
    }
}
