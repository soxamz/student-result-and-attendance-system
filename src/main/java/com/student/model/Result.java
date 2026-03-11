package com.student.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a student's Result in a course.
 */
public class Result {

    private int        resultId;
    private int        studentId;
    private int        courseId;
    private double     marksObtained;
    private double     maxMarks;
    private String     examType;
    private LocalDate  examDate;
    private String     remarks;
    private LocalDateTime createdAt;

    // For display purposes (joined data)
    private String studentName;
    private String rollNumber;
    private String courseCode;
    private String courseName;

    public Result() {}

    public Result(int studentId, int courseId, double marksObtained,
                  double maxMarks, String examType, LocalDate examDate, String remarks) {
        this.studentId     = studentId;
        this.courseId      = courseId;
        this.marksObtained = marksObtained;
        this.maxMarks      = maxMarks;
        this.examType      = examType;
        this.examDate      = examDate;
        this.remarks       = remarks;
    }

    public double getPercentage() {
        return maxMarks > 0 ? (marksObtained / maxMarks) * 100 : 0;
    }

    public String getGrade() {
        double pct = getPercentage();
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        return "F";
    }

    // Getters & Setters
    public int        getResultId()     { return resultId; }
    public void       setResultId(int id)       { this.resultId = id; }

    public int        getStudentId()    { return studentId; }
    public void       setStudentId(int id)      { this.studentId = id; }

    public int        getCourseId()     { return courseId; }
    public void       setCourseId(int id)       { this.courseId = id; }

    public double     getMarksObtained(){ return marksObtained; }
    public void       setMarksObtained(double m){ this.marksObtained = m; }

    public double     getMaxMarks()     { return maxMarks; }
    public void       setMaxMarks(double m)     { this.maxMarks = m; }

    public String     getExamType()     { return examType; }
    public void       setExamType(String t)     { this.examType = t; }

    public LocalDate  getExamDate()     { return examDate; }
    public void       setExamDate(LocalDate d)  { this.examDate = d; }

    public String     getRemarks()      { return remarks; }
    public void       setRemarks(String r)      { this.remarks = r; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void          setCreatedAt(LocalDateTime ts) { this.createdAt = ts; }

    // Display fields
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
        return String.format("Result[Student=%d, Course=%d, Marks=%.1f/%.1f, Grade=%s]",
                studentId, courseId, marksObtained, maxMarks, getGrade());
    }
}
