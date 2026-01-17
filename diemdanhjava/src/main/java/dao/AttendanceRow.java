package dao;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AttendanceRow {

    private final StringProperty mssv = new SimpleStringProperty("");
    private final StringProperty fullName = new SimpleStringProperty("");
    private final StringProperty className = new SimpleStringProperty("");
    private final StringProperty time = new SimpleStringProperty("");
    private final StringProperty status = new SimpleStringProperty("");

    public AttendanceRow() {}

    public AttendanceRow(String mssv, String fullName, String className, String time, String status) {
        this.mssv.set(mssv);
        this.fullName.set(fullName);
        this.className.set(className);
        this.time.set(time);
        this.status.set(status);
    }

    public String getMssv() { return mssv.get(); }
    public void setMssv(String v) { mssv.set(v); }
    public StringProperty mssvProperty() { return mssv; }

    public String getFullName() { return fullName.get(); }
    public void setFullName(String v) { fullName.set(v); }
    public StringProperty fullNameProperty() { return fullName; }

    public String getClassName() { return className.get(); }
    public void setClassName(String v) { className.set(v); }
    public StringProperty classNameProperty() { return className; }

    public String getTime() { return time.get(); }
    public void setTime(String v) { time.set(v); }
    public StringProperty timeProperty() { return time; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }
}
