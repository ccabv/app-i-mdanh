package controller;

import app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import service.EmailService;
import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class ForgotController {

    @FXML private TextField txtEmail;
    @FXML private TextField txtOtp;
    @FXML private PasswordField txtNewPassword;

    private String generatedOtp;

    /* ===== GỬI OTP ===== */
    @FXML
    public void sendOtp() {
        try {
            String email = txtEmail.getText().trim();

            if (email.isEmpty()) {
                System.out.println("⚠ Nhập email");
                return;
            }

            generatedOtp = String.format("%06d",
                    new Random().nextInt(999999));

            EmailService.sendOtp(email, generatedOtp);
            System.out.println("✅ OTP đã gửi: " + generatedOtp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===== RESET PASSWORD ===== */
    @FXML
    public void resetPassword() {
        try {
            String email = txtEmail.getText().trim();
            String otp = txtOtp.getText().trim();
            String newPassword = txtNewPassword.getText().trim();

            if (!otp.equals(generatedOtp)) {
                System.out.println("❌ OTP sai");
                return;
            }

            String newHash =
                    BCrypt.hashpw(newPassword, BCrypt.gensalt());

            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET password_hash=? WHERE email=?"
            );
            ps.setString(1, newHash);
            ps.setString(2, email);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                System.out.println("✅ Đổi mật khẩu thành công");
                MainApp.changeScene("/view/login.fxml", "Login");
            } else {
                System.out.println("❌ Email không tồn tại");
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backLogin() {
        MainApp.changeScene("/view/login.fxml", "Login");
    }
}
