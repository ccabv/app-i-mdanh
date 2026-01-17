package server;

import com.sun.net.httpserver.HttpServer;
import dao.AttendanceDAO;
import javafx.application.Platform;
import model.AttendanceRecord;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

public class AttendanceServer {

    private static final int PORT = 8080;

    private static HttpServer server;
    private static boolean started = false;
    private static Consumer<AttendanceRecord> onNewAttendance;

    /* ================= CALLBACK ================= */
    public static void setOnNewAttendance(Consumer<AttendanceRecord> consumer) {
        onNewAttendance = consumer;
        System.out.println("🔁 Callback attached");
    }

    /* ================= START SERVER ================= */
    public static synchronized void start() {
        try {
            if (started && server != null) {
                System.out.println("⚠ AttendanceServer already started");
                return;
            }

            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            /* ================= PAGE ================= */
            server.createContext("/attendance", exchange -> {
                byte[] html = HTML_PAGE.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders()
                        .set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(html);
                }
            });

            /* ================= SUBMIT ================= */
            server.createContext("/submit", exchange -> {
                try {
                    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        exchange.sendResponseHeaders(405, -1);
                        return;
                    }

                    String body = new String(
                            exchange.getRequestBody().readAllBytes(),
                            StandardCharsets.UTF_8
                    );

                    Map<String, String> form = parseForm(body);

                    // 🔴 BẮT BUỘC: MSSV + SESSION
                    String mssv = trimToNull(form.get("mssv"));
                    String sessionStr = trimToNull(form.get("session"));

                    if (mssv == null || sessionStr == null) {
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }

                    int sessionId = Integer.parseInt(sessionStr);

                    System.out.println("📥 SUBMIT: MSSV=" + mssv + " | session=" + sessionId);

                    /* ========== 1️⃣ GHI DATABASE ========== */
                    boolean ok = AttendanceDAO.insertAttendanceByQR(sessionId, mssv);

                    /* ========== 2️⃣ PUSH REALTIME ========== */
                    if (ok && onNewAttendance != null) {

                        // Lấy record mới nhất của session này (đủ dữ liệu)
                        List<AttendanceRecord> list =
                                AttendanceDAO.getAttendanceBySession(sessionId);

                        AttendanceRecord latest =
                                list.isEmpty() ? null : list.get(0);

                        if (latest != null) {
                            Platform.runLater(() -> {
                                System.out.println(
                                        "📤 PUSH UI: " + latest.getMssv()
                                                + " | session=" + latest.getSessionId()
                                );
                                onNewAttendance.accept(latest);
                            });
                        }
                    }

                    /* ========== 3️⃣ RESPONSE HTML ========== */
                    byte[] res = (ok ? SUCCESS_PAGE : FAIL_PAGE)
                            .getBytes(StandardCharsets.UTF_8);

                    exchange.getResponseHeaders()
                            .set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, res.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(res);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                }
            });

            server.setExecutor(null);
            server.start();
            started = true;

            System.out.println("✅ AttendanceServer STARTED at http://localhost:" + PORT + "/attendance");

        } catch (Exception e) {
            System.err.println("❌ AttendanceServer START FAILED");
            e.printStackTrace();
            started = false;
            server = null;
        }
    }

    /* ================= STOP ================= */
    public static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            started = false;
            System.out.println("🛑 AttendanceServer stopped");
        }
    }

    /* ================= FORM PARSER ================= */
    private static Map<String, String> parseForm(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isBlank()) return map;

        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(
                        kv[0],
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                );
            }
        }
        return map;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /* ================= HTML (GIỮ NGUYÊN UI) ================= */
    private static final String HTML_PAGE = """
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Điểm danh</title>
<style>
body{
  font-family: Arial;
  background: linear-gradient(#43e0a6,#1abc9c);
  padding:30px;
}
.card{
  max-width:420px;
  margin:auto;
  background:white;
  padding:25px;
  border-radius:16px;
}
input,button{
  width:100%;
  padding:12px;
  margin-top:12px;
  border-radius:8px;
}
button{
  background:#1abc9c;
  color:white;
  border:none;
  font-weight:bold;
}
input[readonly]{
  background:#eee;
}
</style>
</head>

<body>
<div class="card">
<h2>📋 Điểm danh sinh viên</h2>

<form method="POST" action="/submit">
    <input name="mssv" placeholder="Mã sinh viên" required>
    <input name="name" placeholder="Họ tên" required>

    <input name="subject" id="subject" readonly>
    <input name="room" id="room" readonly>

    <!-- 🔴 NGẦM – KHÔNG ẢNH HƯỞNG UI -->
    <input type="hidden" name="session" id="session">

    <button type="submit">Điểm danh</button>
</form>
</div>

<script>
const p = new URLSearchParams(window.location.search);
document.getElementById("subject").value = p.get("subject") || "";
document.getElementById("room").value = p.get("room") || "";
document.getElementById("session").value = p.get("session") || "";
</script>

</body>
</html>
""";

    private static final String SUCCESS_PAGE = """
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Thành công</title>
<style>
body{
  font-family: Arial;
  background: linear-gradient(#43e0a6,#1abc9c);
  padding:40px;
  color:white;
  text-align:center;
}
.card{
  background:white;
  color:#2c3e50;
  padding:30px;
  border-radius:16px;
  max-width:420px;
  margin:auto;
}
</style>
</head>
<body>
<div class="card">
  <h2>✅ Điểm danh thành công</h2>
  <p>Bạn có thể đóng trang này.</p>
</div>
</body>
</html>
""";

    private static final String FAIL_PAGE = """
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Thất bại</title>
<style>
body{
  font-family: Arial;
  background: linear-gradient(#e74c3c,#c0392b);
  padding:40px;
  color:white;
  text-align:center;
}
.card{
  background:white;
  color:#2c3e50;
  padding:30px;
  border-radius:16px;
  max-width:420px;
  margin:auto;
}
</style>
</head>
<body>
<div class="card">
  <h2>❌ Điểm danh thất bại</h2>
  <p>MSSV không tồn tại hoặc đã điểm danh.</p>
</div>
</body>
</html>
""";
}
