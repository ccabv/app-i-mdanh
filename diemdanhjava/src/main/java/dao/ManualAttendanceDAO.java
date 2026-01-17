package dao;

import db.DataDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManualAttendanceDAO {

    /* =========================================================
       1️⃣ LƯU ĐIỂM DANH TAY
       - TỰ TẠO / LẤY QRSession
       - KHÔNG BỊ TRÙNG (QRSessionID, StudentID)
       ========================================================= */
    public static boolean insertManualAttendance(
            String mssv,
            String subject,
            String room,
            String status
    ) {

        try (Connection con = DataDBConnection.getConnection()) {

            // 1️⃣ LẤY HOẶC TẠO SESSION (KHÔNG BAO GIỜ NULL)
            Integer sessionId = getOrCreateSession(con, subject, room);
            if (sessionId == null) {
                System.err.println("❌ sessionId = NULL");
                return false;
            }

            // 2️⃣ INSERT (CHỐNG TRÙNG)
            String sql = """
                INSERT INTO AttendanceDetail (QRSessionID, StudentID, Status, CheckTime)
                SELECT ?, s.StudentID, ?, GETDATE()
                FROM Students s
                WHERE s.MSSV = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM AttendanceDetail ad
                      WHERE ad.QRSessionID = ?
                        AND ad.StudentID = s.StudentID
                  )
            """;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, sessionId);
                ps.setString(2, status);
                ps.setString(3, mssv);
                ps.setInt(4, sessionId);

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.err.println("❌ insertManualAttendance()");
            e.printStackTrace();
            return false;
        }
    }

    /* =========================================================
       2️⃣ LẤY HOẶC TẠO QR SESSION (DÙNG CHUNG)
       ========================================================= */
    private static Integer getOrCreateSession(
            Connection con,
            String subject,
            String room
    ) {

        // 1️⃣ TÌM SESSION CHƯA QUÁ 3 GIỜ
        String findSql = """
            SELECT TOP 1 QRSessionID
            FROM QRSession
            WHERE Subject = ?
              AND Room = ?
              AND CreatedAt >= DATEADD(HOUR, -3, GETDATE())
            ORDER BY CreatedAt DESC
        """;

        try (PreparedStatement ps = con.prepareStatement(findSql)) {
            ps.setString(1, subject);
            ps.setString(2, room);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("QRSessionID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2️⃣ NẾU KHÔNG CÓ → TẠO SESSION MỚI
        String insertSql = """
            INSERT INTO QRSession (Subject, Room, CreatedAt)
            OUTPUT INSERTED.QRSessionID
            VALUES (?, ?, GETDATE())
        """;

        try (PreparedStatement ps = con.prepareStatement(insertSql)) {
            ps.setString(1, subject);
            ps.setString(2, room);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
