package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.AttendanceServer;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;

            // 🔥 START SERVER 1 LẦN DUY NHẤT
            AttendanceServer.start();

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/view/login.fxml")
            );

            Scene scene = new Scene(loader.load());
            stage.setTitle("Hệ thống điểm danh");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 BẮT BUỘC PHẢI CÓ
    @Override
    public void stop() {
        System.out.println("🛑 App closing");
        AttendanceServer.stop();
    }

    public static void changeScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource(fxmlPath)
            );
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
