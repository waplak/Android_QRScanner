package lk.waplak.dakma.auth;

public class ScanResult {
    private int studentId;
    private int courseId;
    private int centreId;
    private int lecturerId;
    private String studentRegNo;
    private String studentName;
    private boolean hasAdmissionFee;
    private boolean hasMaintenanceFee;
    private String date;
    private int feeTypeId;
    private String editorUserName;
    private String editorRole;
    private double fee;
    private boolean hasPaymentDone;
    private String feeTypeEndDate;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCentreId() {
        return centreId;
    }

    public void setCentreId(int centreId) {
        this.centreId = centreId;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public boolean isHasAdmissionFee() {
        return hasAdmissionFee;
    }

    public void setHasAdmissionFee(boolean hasAdmissionFee) {
        this.hasAdmissionFee = hasAdmissionFee;
    }

    public boolean isHasMaintenanceFee() {
        return hasMaintenanceFee;
    }

    public void setHasMaintenanceFee(boolean hasMaintenanceFee) {
        this.hasMaintenanceFee = hasMaintenanceFee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFeeTypeId(int feeTypeId) {
        this.feeTypeId = feeTypeId;
    }

    public String getEditorUserName() {
        return editorUserName;
    }

    public void setEditorUserName(String editorUserName) {
        this.editorUserName = editorUserName;
    }

    public String getEditorRole() {
        return editorRole;
    }

    public void setEditorRole(String editorRole) {
        this.editorRole = editorRole;
    }

    public String getFeeTypeEndDate() {
        return feeTypeEndDate;
    }

    public void setFeeTypeEndDate(String feeTypeEndDate) {
        this.feeTypeEndDate = feeTypeEndDate;
    }

    public int getFeeTypeId() {
        return feeTypeId;
    }





    public String getStudentRegNo() {
        return studentRegNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public double getFee() {
        return fee;
    }

    public boolean isHasPaymentDone() {
        return hasPaymentDone;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public void setHasPaymentDone(boolean hasPaymentDone) {
        this.hasPaymentDone = hasPaymentDone;
    }


}
