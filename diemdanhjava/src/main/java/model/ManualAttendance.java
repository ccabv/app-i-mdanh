package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ManualAttendance {

    private final StringProperty mssv;
    private final StringProperty fullName;
    private final StringProperty className;
    private final StringProperty subject;
    private final StringProperty room;
    private final StringProperty status;

    /* ===== CONSTRUCTOR ===== */
    public ManualAttendance(
            String mssv,
            String fullName,
            String className,
            String subject,
            String room,
            String status
    ) {
        this.mssv = new SimpleStringProperty(mssv);
        this.fullName = new SimpleStringProperty(fullName);
        this.className = new SimpleStringProperty(className);
        this.subject = new SimpleStringProperty(subject);
        this.room = new SimpleStringProperty(room);
        this.status = new SimpleStringProperty(status);
    }

    /* ===== GETTER PROPERTY (CHO TABLEVIEW) ===== */
    public StringProperty mssvProperty() {
        return mssv;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public StringProperty roomProperty() {
        return room;
    }

    public StringProperty statusProperty() {
        return status;
    }

    /* ===== GETTER THƯỜNG (OPTIONAL) ===== */
    public String getMssv() {
        return mssv.get();
    }

    public String getFullName() {
        return fullName.get();
    }

    public String getClassName() {
        return className.get();
    }

    public String getSubject() {
        return subject.get();
    }

    public String getRoom() {
        return room.get();
    }

    public String getStatus() {
        return status.get();
    }
}
