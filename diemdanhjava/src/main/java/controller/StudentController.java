package controller;

import app.MainApp;
import dao.StudentDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Student;

public class StudentController {

    @FXML private TextField txtMSSV;
    @FXML private TextField txtName;
    @FXML private TextField txtClass;

    @FXML private TableView<Student> tableStudent;
    @FXML private TableColumn<Student, Number> colSTT;
    @FXML private TableColumn<Student, String> colMSSV;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colClass;

    private final StudentDAO studentDAO = new StudentDAO();
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initTable();
        loadStudents();
    }

    private void initTable() {
        colSTT.setCellValueFactory(cellData ->
                Bindings.createIntegerBinding(
                        () -> tableStudent.getItems().indexOf(cellData.getValue()) + 1,
                        tableStudent.itemsProperty()
                )
        );

        colMSSV.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMssv())
        );

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullName())
        );

        colClass.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getClassName())
        );
    }

    private void loadStudents() {
        studentList.setAll(studentDAO.getAllStudents());
        tableStudent.setItems(studentList);
    }

    @FXML
    private void onAdd() {
        String mssv = txtMSSV.getText().trim();
        String name = txtName.getText().trim();
        String className = txtClass.getText().trim();

        if (mssv.isEmpty() || name.isEmpty() || className.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ MSSV, Họ tên và Lớp");
            return;
        }

        boolean success = studentDAO.insertStudent(mssv, name, className);
        if (success) {
            clearForm();
            loadStudents();
        } else {
            showAlert("Lỗi", "Không thể thêm sinh viên (trùng MSSV?)");
        }
    }

    @FXML
    private void onDelete() {
        Student selected = tableStudent.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn sinh viên để xóa");
            return;
        }

        if (confirmDialog("Xác nhận", "Bạn có chắc muốn xóa sinh viên " + selected.getFullName() + "?")) {
            studentDAO.deleteStudent(selected.getStudentID());
            loadStudents();
        }
    }

    @FXML
    private void onBack() {
        MainApp.changeScene("/view/menu.fxml", "Menu");
    }

    private void clearForm() {
        txtMSSV.clear();
        txtName.clear();
        txtClass.clear();
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
