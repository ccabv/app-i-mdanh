package dao;

import db.DataDBConnection;   // ✅ DB AttendanceSystem
import model.AttendanceRecord;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDAO {

    private static final DateTimeFormatter F =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /* =========================================================
       1️⃣ LOAD CHI TIẾT ĐIỂM DANH (QR + TAY + CAMERA)
       ========================================================= */
    public static List<AttendanceRecord> getAllAttendanceStatistics() {

        List<AttendanceRecord> list = new ArrayList<>();

        String sql = """
            SELECT
                s.MSSV,
                s.FullName,
                s.ClassName,        -- ✅ LỚP
                qs.Subject,
                qs.Room,
                ad.Status,
                ad.CheckTime
            FROM dbo.AttendanceDetail ad
            JOIN dbo.Students s   ON s.StudentID = ad.StudentID
            JOIN dbo.QRSession qs ON qs.QRSessionID = ad.QRSessionID
            ORDER BY ad.CheckTime DESC
        """;

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("CheckTime");

                list.add(new AttendanceRecord(
                        rs.getString("MSSV"),
                        rs.getString("FullName"),
                        rs.getString("ClassName"),   // ✅ FIX
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
       2️⃣ TỔNG LƯỢT ĐIỂM DANH
       ========================================================= */
    public static int countTotalAttendanceDetails() {

        String sql = "SELECT COUNT(*) FROM dbo.AttendanceDetail";

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (Exception e) {
            System.err.println("❌ countTotalAttendanceDetails()");
            e.printStackTrace();
            return 0;
        }
    }

    /* =========================================================
       3️⃣ TỔNG SINH VIÊN
       ========================================================= */
    public static int countStudents() {

        String sql = "SELECT COUNT(*) FROM dbo.Students";

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (Exception e) {
            System.err.println("❌ countStudents()");
            e.printStackTrace();
            return 0;
        }
    }

    /* =========================================================
       4️⃣ TỔNG MÔN HỌC (THEO QR)
       ========================================================= */
    public static int countSubjects() {

        String sql = "SELECT COUNT(DISTINCT Subject) FROM dbo.QRSession";

        try (Connection con = DataDBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (Exception e) {
            System.err.println("❌ countSubjects()");
            e.printStackTrace();
            return 0;
        }
    }
}
