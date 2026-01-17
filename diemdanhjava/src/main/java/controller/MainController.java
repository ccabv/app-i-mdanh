package controller;

import app.MainApp;
import app.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    /* ===== USER LABEL ===== */
    @FXML
    private Label lblUser;

    /* ===== INIT ===== */
    @FXML
    public void initialize() {

        String fullName = UserSession.getFullName();
        String email = UserSession.getEmail();

        // 🔥 LOGIC HIỂN THỊ USERNAME
        if (fullName != null && !fullName.isBlank() && !fullName.equalsIgnoreCase("User")) {
            lblUser.setText(fullName);
        } else if (email != null && email.contains("@")) {
            // lấy phần trước @ làm username
            lblUser.setText(email.split("@")[0]);
        } else {
            lblUser.setText("Guest");
        }
    }

    /* ========= ĐIỂM DANH ========= */

    @FXML
    private void onCameraClick() {
        MainApp.changeScene(
                "/view/camera_attendance.fxml",
                "Camera"
        );
    }

    @FXML
    private void onManualClick() {
        MainApp.changeScene(
                "/view/ddtay.fxml",
                "Điểm danh tay"
        );
    }

    @FXML
    private void onQRClick() {
        MainApp.changeScene(
                "/view/qr_attendance.fxml",
                "QR"
        );
    }

    /* ========= THỐNG KÊ ========= */

    @FXML
    private void onStatisticClick() {
        MainApp.changeScene(
                "/view/statistics.fxml",
                "Thống kê"
        );
    }

    /* ========= QUẢN LÝ ========= */

    @FXML
    private void onTeacherClick() {
        MainApp.changeScene(
                "/view/teacher.fxml",
                "Giáo viên"
        );
    }

    @FXML
    private void onStudentClick() {
        MainApp.changeScene(
                "/view/student.fxml",
                "Sinh viên"
        );
    }

    /* ========= ĐĂNG XUẤT ========= */

    @FXML
    private void onLogout() {
        UserSession.clear();
        MainApp.changeScene(
                "/view/login.fxml",
                "Đăng nhập"
        );
    }
}
