package controller;

import app.MainApp;
import dao.CameraAttendanceDAO;
import dao.StudentDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.AttendanceRow;
import model.Student;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class CameraAttendanceController {

    static { OpenCV.loadLocally(); }

    /* ================= TOP ================= */
    @FXML private TextField txtRoom;
    @FXML private TextField txtSubject;

    /* ================= CAMERA ================= */
    @FXML private ImageView cameraView;

    /* ================= SEARCH ================= */
    @FXML private TextField txtSearch;

    /* ================= TABLE ================= */
    @FXML private TableView<AttendanceRow> tableAttendance;
    @FXML private TableColumn<AttendanceRow, String> colId;
    @FXML private TableColumn<AttendanceRow, String> colName;
    @FXML private TableColumn<AttendanceRow, String> colClass;
    @FXML private TableColumn<AttendanceRow, String> colTime;
    @FXML private TableColumn<AttendanceRow, String> colStatus;

    private final ObservableList<AttendanceRow> rows =
            FXCollections.observableArrayList();

    /* ================= CAMERA CORE ================= */
    private VideoCapture capture;
    private Timer timer;
    private volatile boolean cameraActive = false;
    private Mat lastFrame;

    /* ================= DAO ================= */
    private final StudentDAO studentDAO = new StudentDAO();
    private final CameraAttendanceDAO cameraDAO = new CameraAttendanceDAO();

    /* ================= STATE ================= */
    private Student currentStudent;
    private int currentAttendanceId = -1;

    /* ================= INIT ================= */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().mssvProperty());
        colName.setCellValueFactory(c -> c.getValue().fullNameProperty());
        colClass.setCellValueFactory(c -> c.getValue().classNameProperty());
        colTime.setCellValueFactory(c -> c.getValue().timeProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());

        tableAttendance.setItems(rows);

        if (txtSearch != null) {
            txtSearch.setOnAction(e -> onMSSVEntered());
        }
    }

    /* ================= CAMERA ================= */

    @FXML
    public void startCamera() {
        if (cameraActive) return;

        capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            alert("Lỗi", "Không mở được camera");
            safeReleaseCapture();
            return;
        }

        cameraActive = true;

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!cameraActive) return;

                Mat frame = new Mat();
                boolean ok = capture.read(frame);
                if (!ok || frame.empty()) {
                    frame.release();
                    return;
                }

                // 🔥 LẬT CAMERA KIỂU SELFIE (TRÁI ↔ PHẢI)
                Core.flip(frame, frame, 1);

                Mat cloned = frame.clone();
                frame.release();

                if (lastFrame != null) lastFrame.release();
                lastFrame = cloned;

                Image img = matToImage(cloned);
                Platform.runLater(() -> cameraView.setImage(img));
            }
        }, 0, 33);
    }

    @FXML
    public void pauseCamera() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @FXML
    public void stopCamera() {
        cameraActive = false;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        safeReleaseCapture();

        if (lastFrame != null) {
            lastFrame.release();
            lastFrame = null;
        }

        cameraView.setImage(null);
    }

    private void safeReleaseCapture() {
        if (capture != null) {
            try {
                if (capture.isOpened()) capture.release();
            } catch (Exception ignored) {}
            capture = null;
        }
    }

    /* ================= REFRESH ================= */
    @FXML
    public void onRefresh() {
        rows.clear();
        currentStudent = null;
        currentAttendanceId = -1;

        if (txtSearch != null) txtSearch.clear();
        if (txtRoom != null) txtRoom.clear();
        if (txtSubject != null) txtSubject.clear();

        tableAttendance.refresh();
    }

    /* ================= MSSV ENTER ================= */
    @FXML
    public void onMSSVEntered() {
        String mssv = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        if (mssv.isEmpty()) return;

        Student s = studentDAO.findByMSSV(mssv);
        if (s == null) {
            currentStudent = null;
            alert("Không tìm thấy", "MSSV không tồn tại: " + mssv);
            return;
        }
        currentStudent = s;

        AttendanceRow row = findRowByMssv(mssv);
        if (row == null) {
            rows.add(new AttendanceRow(
                    s.getMssv(),
                    s.getFullName(),
                    s.getClassName(),
                    "",
                    "Chưa chụp"
            ));
        } else {
            tableAttendance.getSelectionModel().select(row);
            tableAttendance.scrollTo(row);
        }

        txtSearch.clear();
    }

    private AttendanceRow findRowByMssv(String mssv) {
        Optional<AttendanceRow> opt = rows.stream()
                .filter(r -> r.getMssv() != null &&
                        r.getMssv().equalsIgnoreCase(mssv))
                .findFirst();
        return opt.orElse(null);
    }

    /* ================= CAPTURE ================= */
    @FXML
    public void captureImage() {
        if (!cameraActive || lastFrame == null || lastFrame.empty()) {
            alert("Lỗi", "Camera chưa sẵn sàng");
            return;
        }

        if (currentStudent == null) {
            alert("Lỗi", "Chưa nhập MSSV hợp lệ");
            return;
        }

        String room = txtRoom.getText().trim();
        String subject = txtSubject.getText().trim();

        if (room.isEmpty() || subject.isEmpty()) {
            alert("Thiếu dữ liệu", "Vui lòng nhập Môn học và Phòng");
            return;
        }

        if (currentAttendanceId == -1) {
            currentAttendanceId =
                    cameraDAO.createAttendance(1, 1, room, subject);
            if (currentAttendanceId == -1) {
                alert("Lỗi", "Không tạo được buổi điểm danh");
                return;
            }
        }

        byte[] photo = matToBytes(lastFrame);

        boolean ok = cameraDAO.saveCameraDetail(
                currentAttendanceId,
                currentStudent.getStudentID(),
                photo
        );

        if (!ok) {
            alert("Lỗi", "Không lưu được ảnh");
            return;
        }

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        AttendanceRow row = findRowByMssv(currentStudent.getMssv());
        if (row != null) {
            row.setTime(time);
            row.setStatus("Có mặt");
        }

        tableAttendance.refresh();
        currentStudent = null;
    }

    /* ================= EXPORT IMAGE ================= */
    @FXML
    public void exportImage() {
        AttendanceRow selected =
                tableAttendance.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Thiếu chọn", "Chọn sinh viên để xuất ảnh");
            return;
        }

        Student s = studentDAO.findByMSSV(selected.getMssv());
        if (s == null) return;

        byte[] photo =
                cameraDAO.getLatestPhoto(currentAttendanceId, s.getStudentID());
        if (photo == null || photo.length == 0) {
            alert("Không có ảnh", "Sinh viên chưa có ảnh");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Lưu ảnh điểm danh");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Image (*.png)", "*.png")
        );
        chooser.setInitialFileName(
                "attendance_" + selected.getMssv() + ".png"
        );

        File file =
                chooser.showSaveDialog(tableAttendance.getScene().getWindow());
        if (file == null) return;

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(photo);
            new Alert(Alert.AlertType.INFORMATION,
                    "✅ Đã xuất ảnh:\n" + file.getAbsolutePath())
                    .showAndWait();
        } catch (Exception e) {
            alert("Lỗi", "Không lưu được ảnh");
        }
    }

    @FXML
    public void onBackToMenu() {
        stopCamera();
        MainApp.changeScene("/view/menu.fxml", "Menu");
    }

    /* ================= UTIL ================= */

    private Image matToImage(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    private byte[] matToBytes(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return buffer.toArray();
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
