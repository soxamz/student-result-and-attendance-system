package com.student.model;

import java.time.LocalDateTime;

/**
 * Represents a Student entity.
 */
public class Student {

    private int    studentId;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private int    semester;
    private LocalDateTime createdAt;

    // Constructors
    public Student() {}

    public Student(String rollNumber, String firstName, String lastName,
                   String email, String phone, String department, int semester) {
        this.rollNumber  = rollNumber;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.email       = email;
        this.phone       = phone;
        this.department  = department;
        this.semester    = semester;
    }

    // Getters & Setters
    public int    getStudentId()   { return studentId; }
    public void   setStudentId(int id) { this.studentId = id; }

    public String getRollNumber()  { return rollNumber; }
    public void   setRollNumber(String rn) { this.rollNumber = rn; }

    public String getFirstName()   { return firstName; }
    public void   setFirstName(String fn) { this.firstName = fn; }

    public String getLastName()    { return lastName; }
    public void   setLastName(String ln) { this.lastName = ln; }

    public String getFullName()    { return firstName + " " + lastName; }

    public String getEmail()       { return email; }
    public void   setEmail(String email) { this.email = email; }

    public String getPhone()       { return phone; }
    public void   setPhone(String phone) { this.phone = phone; }

    public String getDepartment()  { return department; }
    public void   setDepartment(String dept) { this.department = dept; }

    public int    getSemester()    { return semester; }
    public void   setSemester(int sem) { this.semester = sem; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void          setCreatedAt(LocalDateTime ts) { this.createdAt = ts; }

    @Override
    public String toString() {
        return String.format("Student[ID=%d, Roll=%s, Name=%s, Dept=%s, Sem=%d]",
                studentId, rollNumber, getFullName(), department, semester);
    }
}
