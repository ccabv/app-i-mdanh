package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Teacher {

    /* ===== PROPERTIES ===== */
    private final IntegerProperty teacherID;
    private final StringProperty teacherCode;
    private final StringProperty fullName;

    /* ===== CONSTRUCTOR (KHỚP DAO) ===== */
    public Teacher(int teacherID, String teacherCode, String fullName) {
        this.teacherID = new SimpleIntegerProperty(teacherID);
        this.teacherCode = new SimpleStringProperty(teacherCode);
        this.fullName = new SimpleStringProperty(fullName);
    }

    /* ===== GETTERS ===== */
    public int getTeacherID() {
        return teacherID.get();
    }

    public String getTeacherCode() {
        return teacherCode.get();
    }

    public String getFullName() {
        return fullName.get();
    }

    /* ===== SETTERS (CHO CRUD SAU NÀY) ===== */
    public void setTeacherCode(String teacherCode) {
        this.teacherCode.set(teacherCode);
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    /* ===== PROPERTY METHODS (CHO TABLEVIEW) ===== */
    public IntegerProperty teacherIDProperty() {
        return teacherID;
    }

    public StringProperty teacherCodeProperty() {
        return teacherCode;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }
}
