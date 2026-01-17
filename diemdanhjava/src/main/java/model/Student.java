package model;

public class Student {

    private int studentID;
    private String mssv;
    private String fullName;
    private String className;

    /* ===== CONSTRUCTOR ===== */
    public Student(int studentID, String mssv, String fullName, String className) {
        this.studentID = studentID;
        this.mssv = mssv;
        this.fullName = fullName;
        this.className = className;
    }

    /* ===== GETTERS ===== */
    public int getStudentID() {
        return studentID;
    }

    public String getMssv() {
        return mssv;
    }

    public String getFullName() {
        return fullName;
    }

    public String getClassName() {
        return className;
    }

    /* ===== SETTERS (không bắt buộc nhưng nên có) ===== */
    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
