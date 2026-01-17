package service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    // ĐỔI THÀNH GMAIL CỦA BẠN
    private static final String FROM_EMAIL = "youremail";

    // APP PASSWORD (16 ký tự – KHÔNG DẤU CÁCH)
    private static final String APP_PASSWORD = "apppasword";

    public static void sendOtp(String toEmail, String otp) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                FROM_EMAIL,
                                APP_PASSWORD
                        );
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
        );
        message.setSubject("Ma OTP he thong diem danh");
        message.setText(
                "Ma OTP cua ban la: " + otp +
                        "\nMa co hieu luc trong 5 phut."
        );

        Transport.send(message);
    }
}
