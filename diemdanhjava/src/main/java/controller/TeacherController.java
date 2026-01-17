package controller;

import app.MainApp;
import dao.TeacherDAO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Teacher;

public class TeacherController {

    @FXML private TextField txtCode;
    @FXML private TextField txtName;

    @FXML private TableView<Teacher> tableTeacher;
    @FXML private TableColumn<Teacher, Number> colSTT;
    @FXML private TableColumn<Teacher, String> colCode;
    @FXML private TableColumn<Teacher, String> colName;

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final ObservableList<Teacher> teacherList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initTable();
        loadTeachers();
    }

    private void initTable() {
        colSTT.setCellValueFactory(cell ->
                Bindings.createIntegerBinding(
                        () -> tableTeacher.getItems().indexOf(cell.getValue()) + 1,
                        tableTeacher.itemsProperty()
                )
        );

        colCode.setCellValueFactory(data -> data.getValue().teacherCodeProperty());
        colName.setCellValueFactory(data -> data.getValue().fullNameProperty());
    }

    private void loadTeachers() {
        teacherList.setAll(teacherDAO.getAllTeachers());
        tableTeacher.setItems(teacherList);
    }

    @FXML
    private void onAdd() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ mã GV và họ tên");
            return;
        }

        boolean success = teacherDAO.insertTeacher(code, name);
        if (success) {
            clearForm();
            loadTeachers();
        } else {
            showAlert("Lỗi", "Không thể thêm giáo viên (trùng mã?)");
        }
    }

    @FXML
    private void onDelete() {
        Teacher selected = tableTeacher.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn giáo viên để xóa");
            return;
        }

        if (confirmDialog("Xác nhận", "Bạn có chắc muốn xóa giáo viên " + selected.getFullName() + "?")) {
            teacherDAO.deleteTeacher(selected.getTeacherID());
            loadTeachers();
        }
    }

    @FXML
    private void onBack() {
        MainApp.changeScene("/view/menu.fxml", "Menu");
    }

    private void clearForm() {
        txtCode.clear();
        txtName.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
