package com.student.model;

import java.time.LocalDateTime;

/**
 * Represents a Course entity.
 */
public class Course {

    private int    courseId;
    private String courseCode;
    private String courseName;
    private int    credits;
    private LocalDateTime createdAt;

    public Course() {}

    public Course(String courseCode, String courseName, int credits) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits    = credits;
    }

    public int    getCourseId()   { return courseId; }
    public void   setCourseId(int id) { this.courseId = id; }

    public String getCourseCode() { return courseCode; }
    public void   setCourseCode(String code) { this.courseCode = code; }

    public String getCourseName() { return courseName; }
    public void   setCourseName(String name) { this.courseName = name; }

    public int    getCredits()    { return credits; }
    public void   setCredits(int credits) { this.credits = credits; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void          setCreatedAt(LocalDateTime ts) { this.createdAt = ts; }

    @Override
    public String toString() {
        return String.format("Course[ID=%d, Code=%s, Name=%s, Credits=%d]",
                courseId, courseCode, courseName, credits);
    }
}
