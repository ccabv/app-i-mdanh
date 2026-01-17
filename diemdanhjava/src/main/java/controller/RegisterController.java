package controller;

import app.MainApp;
import db.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;
import service.EmailService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class RegisterController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtOtp;

    private String generatedOtp;

    /* ===== GỬI OTP ===== */
    @FXML
    public void sendOtp() {
        try {
            String email = txtEmail.getText().trim();
            if (email.isEmpty()) {
                System.out.println("⚠ Vui lòng nhập email");
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

    /* ===== ĐĂNG KÝ ===== */
    @FXML
    public void register() {

        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String otp = txtOtp.getText().trim();

        if (email.isEmpty() || password.isEmpty() || otp.isEmpty()) {
            System.out.println("⚠ Nhập đầy đủ thông tin");
            return;
        }

        if (!otp.equals(generatedOtp)) {
            System.out.println("❌ OTP không đúng");
            return;
        }

        // 🔐 HASH PASSWORD ĐÚNG CHUẨN
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection conn = DBConnection.getConnection()) {

            // 1️⃣ Check email tồn tại
            PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM users WHERE email = ?"
            );
            check.setString(1, email);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("❌ Email đã tồn tại");
                return;
            }

            // 2️⃣ INSERT USER
            PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO users (full_name, email, password_hash, verified)
                VALUES (?, ?, ?, 1)
            """);

            ps.setString(1, "User");
            ps.setString(2, email);
            ps.setString(3, hash);

            ps.executeUpdate();

            System.out.println("✅ Đăng ký thành công: " + email);
            MainApp.changeScene("/view/login.fxml", "Đăng nhập");

        } catch (Exception e) {
            System.out.println("❌ Lỗi đăng ký");
            e.printStackTrace();
        }
    }

    @FXML
    public void backLogin() {
        MainApp.changeScene("/view/login.fxml", "Đăng nhập");
    }
}
