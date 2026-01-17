package controller;

import app.MainApp;
import app.UserSession;
import db.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField txtUsername; // email
    @FXML private PasswordField txtPassword;

    @FXML
    private void handleLogin() {

        String email = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("⚠️ Nhập email và mật khẩu");
            return;
        }

        String sql = """
            SELECT full_name, password_hash
            FROM users
            WHERE email = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Email không tồn tại");
                return;
            }

            String fullName = rs.getString("full_name");
            String hashInDb = rs.getString("password_hash");

            if (hashInDb == null || !hashInDb.startsWith("$2")) {
                System.out.println("❌ Mật khẩu trong DB không hợp lệ (chưa hash BCrypt)");
                return;
            }

            // 🔥 SO SÁNH BCRYPT
            if (BCrypt.checkpw(password, hashInDb)) {

                // ✅ LƯU SESSION
                UserSession.set(email, fullName);

                System.out.println("✅ Đăng nhập thành công: " + fullName);

                MainApp.changeScene("/view/menu.fxml", "Menu");

            } else {
                System.out.println("❌ Sai mật khẩu");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goRegister() {
        MainApp.changeScene("/view/register.fxml", "Đăng ký");
    }

    @FXML
    private void goForgot() {
        MainApp.changeScene("/view/forgot.fxml", "Quên mật khẩu");
    }
}
