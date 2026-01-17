package controller;

import app.MainApp;
import dao.ManualAttendanceDAO;
import dao.StudentDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ManualAttendance;
import model.Student;

public class ManualAttendanceController {

    @FXML private TextField txtMSSV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtLop;
    @FXML private TextField txtMonHoc;
    @FXML private TextField txtPhong;

    @FXML private RadioButton rbPresent;
    @FXML private RadioButton rbAbsent;
    @FXML private RadioButton rbLate;

    @FXML private TableView<ManualAttendance> tableAttendance;
    @FXML private TableColumn<ManualAttendance, Number> colSTT;
    @FXML private TableColumn<ManualAttendance, String> colMSSV;
    @FXML private TableColumn<ManualAttendance, String> colHoTen;
    @FXML private TableColumn<ManualAttendance, String> colLop;
    @FXML private TableColumn<ManualAttendance, String> colTrangThai;

    private ToggleGroup statusGroup;

    private final StudentDAO studentDAO = new StudentDAO();
    private final ManualAttendanceDAO manualAttendanceDAO = new ManualAttendanceDAO();

    private final ObservableList<ManualAttendance> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        statusGroup = new ToggleGroup();
        rbPresent.setToggleGroup(statusGroup);
        rbAbsent.setToggleGroup(statusGroup);
        rbLate.setToggleGroup(statusGroup);

        colSTT.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(tableAttendance.getItems().indexOf(c.getValue()) + 1)
        );

        colMSSV.setCellValueFactory(c -> c.getValue().mssvProperty());
        colHoTen.setCellValueFactory(c -> c.getValue().fullNameProperty());
        colLop.setCellValueFactory(c -> c.getValue().classNameProperty());
        colTrangThai.setCellValueFactory(c -> c.getValue().statusProperty());

        tableAttendance.setItems(data);
    }

    @FXML
    private void onMSSVChanged() {
        String mssv = txtMSSV.getText().trim();

        if (mssv.isEmpty()) {
            txtHoTen.clear();
            txtLop.clear();
            return;
        }

        Student student = studentDAO.findByMSSV(mssv);
        if (student == null) {
            txtHoTen.clear();
            txtLop.clear();
        } else {
            txtHoTen.setText(student.getFullName());
            txtLop.setText(student.getClassName());
        }
    }

    @FXML
    private void onSave() {
        if (txtMSSV.getText().isEmpty()
                || txtHoTen.getText().isEmpty()
                || txtMonHoc.getText().isEmpty()
                || txtPhong.getText().isEmpty()
                || statusGroup.getSelectedToggle() == null) {

            showAlert("Thiếu dữ liệu", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        String status =
                rbPresent.isSelected() ? "Có mặt"
                        : rbAbsent.isSelected() ? "Vắng"
                        : "Trễ";

        boolean ok = manualAttendanceDAO.insertManualAttendance(
                txtMSSV.getText().trim(),
                txtMonHoc.getText().trim(),
                txtPhong.getText().trim(),
                status
        );

        if (!ok) {
            showAlert("Lỗi", "Không lưu được vào hệ thống!");
            return;
        }

        ManualAttendance record = new ManualAttendance(
                txtMSSV.getText().trim(),
                txtHoTen.getText().trim(),
                txtLop.getText().trim(),
                txtMonHoc.getText().trim(),
                txtPhong.getText().trim(),
                status
        );

        data.add(record);
        onClear();
    }

    @FXML
    private void onBack() {
        MainApp.changeScene("/view/menu.fxml", "Menu");
    }

    @FXML
    private void onClear() {
        txtMSSV.clear();
        txtHoTen.clear();
        txtLop.clear();
        txtMonHoc.clear();
        txtPhong.clear();
        statusGroup.selectToggle(null);
    }

    @FXML
    private void onDelete() {
        ManualAttendance selected = tableAttendance.getSelectionModel().getSelectedItem();
        if (selected != null) data.remove(selected);
    }

    @FXML
    private void onExport() {
        System.out.println("EXPORT CLICKED");
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}

