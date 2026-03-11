package com.student.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an Attendance record.
 */
public class Attendance {

    public enum Status { PRESENT, ABSENT, LATE }

    private int        attendanceId;
    private int        studentId;
    private int        courseId;
    private LocalDate  attendanceDate;
    private Status     status;
    private LocalDateTime createdAt;

    // Display fields
    private String studentName;
    private String rollNumber;
    private String courseCode;
    private String courseName;

    public Attendance() {}

    public Attendance(int studentId, int courseId, LocalDate date, Status status) {
        this.studentId      = studentId;
        this.courseId       = courseId;
        this.attendanceDate = date;
        this.status         = status;
    }

    // Getters & Setters
    public int        getAttendanceId()  { return attendanceId; }
    public void       setAttendanceId(int id)      { this.attendanceId = id; }

    public int        getStudentId()     { return studentId; }
    public void       setStudentId(int id)         { this.studentId = id; }

    public int        getCourseId()      { return courseId; }
    public void       setCourseId(int id)          { this.courseId = id; }

    public LocalDate  getAttendanceDate(){ return attendanceDate; }
    public void       setAttendanceDate(LocalDate d){ this.attendanceDate = d; }

    public Status     getStatus()        { return status; }
    public void       setStatus(Status s)          { this.status = s; }

    public LocalDateTime getCreatedAt()  { return createdAt; }
    public void       setCreatedAt(LocalDateTime ts){ this.createdAt = ts; }

    public String getStudentName() { return studentName; }
    public void   setStudentName(String n) { this.studentName = n; }

    public String getRollNumber()  { return rollNumber; }
    public void   setRollNumber(String r) { this.rollNumber = r; }

    public String getCourseCode()  { return courseCode; }
    public void   setCourseCode(String c) { this.courseCode = c; }

    public String getCourseName()  { return courseName; }
    public void   setCourseName(String n) { this.courseName = n; }

    @Override
    public String toString() {
        return String.format("Attendance[Student=%d, Course=%d, Date=%s, Status=%s]",
                studentId, courseId, attendanceDate, status);
    }
}
