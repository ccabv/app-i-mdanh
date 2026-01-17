package model;

import javafx.beans.property.*;

public class AttendanceRecord {

    /* ================= CORE ================= */
    private final IntegerProperty sessionId;     // >0: QR | -1: Camera/Tay
    private final StringProperty mssv;
    private final StringProperty fullName;
    private final StringProperty className;      // ✅ LỚP (STATISTICS)
    private final StringProperty subjectName;
    private final StringProperty room;
    private final StringProperty status;
    private final StringProperty time;

    /* =================================================
       CONSTRUCTOR 1️⃣ – QR ATTENDANCE (CÓ sessionId)
       ================================================= */
    public AttendanceRecord(
            int sessionId,
            String mssv,
            String fullName,
            String className,
            String subjectName,
            String room,
            String status,
            String time
    ) {
        this.sessionId   = new SimpleIntegerProperty(sessionId);
        this.mssv        = new SimpleStringProperty(mssv);
        this.fullName    = new SimpleStringProperty(fullName);
        this.className   = new SimpleStringProperty(className);
        this.subjectName = new SimpleStringProperty(subjectName);
        this.room        = new SimpleStringProperty(room);
        this.status      = new SimpleStringProperty(status);
        this.time        = new SimpleStringProperty(time);
    }

    /* =================================================
       CONSTRUCTOR 2️⃣ – CAMERA / TAY / STATISTICS
       (KHÔNG sessionId → = -1)
       ================================================= */
    public AttendanceRecord(
            String mssv,
            String fullName,
            String className,
            String subjectName,
            String room,
            String status,
            String time
    ) {
        this.sessionId   = new SimpleIntegerProperty(-1);
        this.mssv        = new SimpleStringProperty(mssv);
        this.fullName    = new SimpleStringProperty(fullName);
        this.className   = new SimpleStringProperty(className);
        this.subjectName = new SimpleStringProperty(subjectName);
        this.room        = new SimpleStringProperty(room);
        this.status      = new SimpleStringProperty(status);
        this.time        = new SimpleStringProperty(time);
    }

    /* =================================================
       GETTERS – LOGIC / SERVER / CONTROLLER
       ================================================= */
    public int getSessionId() {
        return sessionId.get();
    }

    public String getMssv() {
        return mssv.get();
    }

    public String getFullName() {
        return fullName.get();
    }

    public String getClassName() {
        return className.get();
    }

    public String getSubjectName() {
        return subjectName.get();
    }

    public String getRoom() {
        return room.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getTime() {
        return time.get();
    }

    /* =================================================
       TIỆN ÍCH
       ================================================= */
    public boolean isQRRecord() {
        return sessionId.get() > 0;
    }

    /* =================================================
       PROPERTIES – TABLEVIEW (JavaFX)
       ================================================= */
    public IntegerProperty sessionIdProperty() {
        return sessionId;
    }

    public StringProperty mssvProperty() {
        return mssv;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty classNameProperty() {   // ✅ FIX LỖI STATISTICS
        return className;
    }

    public StringProperty subjectNameProperty() {
        return subjectName;
    }

    public StringProperty roomProperty() {
        return room;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty checkTimeFormattedProperty() {
        return time;
    }
}
