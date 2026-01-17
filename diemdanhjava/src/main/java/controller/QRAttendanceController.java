package controller;

import app.MainApp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dao.AttendanceDAO;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.AttendanceRecord;
import server.AttendanceServer;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class QRAttendanceController {

    /* ================= LEFT ================= */
    @FXML private ImageView imgQR;
    @FXML private TextField txtSubject;
    @FXML private TextField txtRoom;

    /* ================= TABLE ================= */
    @FXML private TableView<AttendanceRecord> tableQR;
    @FXML private TableColumn<AttendanceRecord, Number> colSTT;
    @FXML private TableColumn<AttendanceRecord, String> colMSSV;
    @FXML private TableColumn<AttendanceRecord, String> colName;
    @FXML private TableColumn<AttendanceRecord, String> colTime;

    private final ObservableList<AttendanceRecord> data =
            FXCollections.observableArrayList();

    /** session QR hiện tại */
    private int currentSessionId = -1;

    /* ================= INIT ================= */
    @FXML
    public void initialize() {
        System.out.println("🔥 QRAttendanceController initialize");

        /* ===== TABLE ===== */
        colSTT.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        tableQR.getItems().indexOf(cell.getValue()) + 1
                )
        );
        colMSSV.setCellValueFactory(c -> c.getValue().mssvProperty());
        colName.setCellValueFactory(c -> c.getValue().fullNameProperty());
        colTime.setCellValueFactory(c -> c.getValue().checkTimeFormattedProperty());

        tableQR.setItems(data);

        /* ===== START SERVER (1 LẦN) ===== */
        AttendanceServer.start();

        /* ===== REALTIME CALLBACK ===== */
        AttendanceServer.setOnNewAttendance(record -> {
            Platform.runLater(() -> {
                // chỉ nhận record QR
                if (!record.isQRRecord()) return;

                // chỉ nhận đúng session đang mở
                if (record.getSessionId() == currentSessionId) {
                    System.out.println(
                            "📥 Realtime QR: " +
                                    record.getMssv() +
                                    " | session=" + record.getSessionId()
                    );
                    data.add(0, record);
                    tableQR.scrollTo(0);
                }
            });
        });

        /* ===== LOAD SESSION MỚI NHẤT (NẾU CÓ) ===== */
        Integer latest = AttendanceDAO.getLatestQRSessionId();
        if (latest != null) {
            currentSessionId = latest;
            loadAttendanceBySession(currentSessionId);
        }
    }

    /* ================= TẠO QR ================= */
    @FXML
    private void onGenerateQR() {
        try {
            String subject = txtSubject.getText() == null
                    ? "" : txtSubject.getText().trim();
            String room = txtRoom.getText() == null
                    ? "" : txtRoom.getText().trim();

            if (subject.isEmpty() || room.isEmpty()) {
                showAlert(Alert.AlertType.WARNING,
                        "Thiếu thông tin",
                        "Vui lòng nhập Môn học và Phòng học.");
                return;
            }

            /* ===== 1️⃣ TẠO SESSION ===== */
            Integer sessionId = AttendanceDAO.createQRSession(subject, room);
            if (sessionId == null) {
                showAlert(Alert.AlertType.ERROR,
                        "Lỗi DB",
                        "Không tạo được QRSession.");
                return;
            }
            currentSessionId = sessionId;

            /* ===== 2️⃣ TẠO URL ===== */
            String ip = InetAddress.getLocalHost().getHostAddress();
            String url =
                    "http://" + ip + ":8080/attendance"
                            + "?session=" + sessionId
                            + "&subject=" + URLEncoder.encode(subject, StandardCharsets.UTF_8)
                            + "&room=" + URLEncoder.encode(room, StandardCharsets.UTF_8);

            /* ===== 3️⃣ RENDER QR ===== */
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix =
                    writer.encode(url, BarcodeFormat.QR_CODE, 260, 260);

            BufferedImage image =
                    new BufferedImage(260, 260, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < 260; x++) {
                for (int y = 0; y < 260; y++) {
                    image.setRGB(x, y,
                            matrix.get(x, y)
                                    ? 0xFF000000
                                    : 0xFFFFFFFF);
                }
            }

            Image fxImage = SwingFXUtils.toFXImage(image, null);
            imgQR.setImage(fxImage);

            showAlert(Alert.AlertType.INFORMATION,
                    "Đã tạo QR",
                    "Session = " + sessionId +
                            "\nMôn: " + subject +
                            "\nPhòng: " + room);

            /* ===== 4️⃣ LOAD BẢNG ===== */
            loadAttendanceBySession(sessionId);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không tạo được QR. Xem log.");
        }
    }

    /* ================= LOAD THEO SESSION ================= */
    private void loadAttendanceBySession(int sessionId) {
        try {
            List<AttendanceRecord> list =
                    AttendanceDAO.getAttendanceBySession(sessionId);
            data.setAll(list);
            tableQR.refresh();
            System.out.println(
                    "✅ Loaded session=" + sessionId +
                            " | rows=" + list.size()
            );
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không load được danh sách.");
        }
    }

    /* ================= CLEAR ================= */
    @FXML
    private void onClearQR() {
        imgQR.setImage(null);
        txtSubject.clear();
        txtRoom.clear();
        currentSessionId = -1;
        data.clear();
    }

    /* ================= BACK ================= */
    @FXML
    private void onBack() {
        MainApp.changeScene("/view/menu.fxml", "Menu");
    }

    /* ================= UTIL ================= */
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
