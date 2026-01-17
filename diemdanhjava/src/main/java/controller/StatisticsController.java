package controller;

import app.MainApp;
import dao.StatisticsDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.AttendanceRecord;

public class StatisticsController {

    /* ================= SUMMARY ================= */
    @FXML private Label lblTotalAttendance;
    @FXML private Label lblTotalStudent;
    @FXML private Label lblTotalSubject;

    /* ================= TABLE ================= */
    @FXML private TableView<AttendanceRecord> tableStat;

    @FXML private TableColumn<AttendanceRecord, Number> colSTT;
    @FXML private TableColumn<AttendanceRecord, String> colMSSV;
    @FXML private TableColumn<AttendanceRecord, String> colName;
    @FXML private TableColumn<AttendanceRecord, String> colClass;   // ✅ THÊM
    @FXML private TableColumn<AttendanceRecord, String> colSubject;
    @FXML private TableColumn<AttendanceRecord, String> colRoom;
    @FXML private TableColumn<AttendanceRecord, String> colStatus;
    @FXML private TableColumn<AttendanceRecord, String> colTime;

    private final ObservableList<AttendanceRecord> data =
            FXCollections.observableArrayList();

    private final StatisticsDAO statisticsDAO = new StatisticsDAO();

    /* ================= INIT ================= */
    @FXML
    public void initialize() {

        /* ===== STT tự tăng ===== */
        colSTT.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        tableStat.getItems().indexOf(cell.getValue()) + 1
                )
        );

        /* ===== MAP DATA ===== */
        colMSSV.setCellValueFactory(c -> c.getValue().mssvProperty());
        colName.setCellValueFactory(c -> c.getValue().fullNameProperty());
        colClass.setCellValueFactory(c -> c.getValue().classNameProperty()); // ✅
        colSubject.setCellValueFactory(c -> c.getValue().subjectNameProperty());
        colRoom.setCellValueFactory(c -> c.getValue().roomProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());
        colTime.setCellValueFactory(c -> c.getValue().checkTimeFormattedProperty());

        tableStat.setItems(data);

        loadStatistics();
    }

    /* ================= LOAD DATA ================= */
    private void loadStatistics() {
        try {
            data.setAll(statisticsDAO.getAllAttendanceStatistics());

            lblTotalAttendance.setText(
                    String.valueOf(statisticsDAO.countTotalAttendanceDetails())
            );
            lblTotalStudent.setText(
                    String.valueOf(statisticsDAO.countStudents())
            );
            lblTotalSubject.setText(
                    String.valueOf(statisticsDAO.countSubjects())
            );

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không tải được dữ liệu thống kê.");
        }
    }

    /* ================= ACTION ================= */
    @FXML
    private void onBack() {
        MainApp.changeScene("/view/menu.fxml", "Menu");
    }

    /* ================= UTIL ================= */
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
