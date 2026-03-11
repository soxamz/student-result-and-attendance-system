package com.student.service;

import com.student.dao.StudentDAO;
import com.student.dao.CourseDAO;
import com.student.model.Student;
import com.student.model.Course;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Business logic layer for Student operations.
 */
public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO  courseDAO  = new CourseDAO();

    public Student addStudent(String roll, String firstName, String lastName,
                              String email, String phone, String dept, int sem) throws SQLException {
        // Validate unique roll
        if (studentDAO.findByRollNumber(roll).isPresent()) {
            throw new IllegalArgumentException("Student with roll number '" + roll + "' already exists.");
        }
        Student s = new Student(roll, firstName, lastName, email, phone, dept, sem);
        return studentDAO.create(s);
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public Optional<Student> findByRoll(String roll) throws SQLException {
        return studentDAO.findByRollNumber(roll);
    }

    public Optional<Student> findById(int id) throws SQLException {
        return studentDAO.findById(id);
    }

    public List<Student> findByDepartment(String dept) throws SQLException {
        return studentDAO.findByDepartment(dept);
    }

    public boolean updateStudent(Student s) throws SQLException {
        return studentDAO.update(s);
    }

    public boolean deleteStudent(int id) throws SQLException {
        return studentDAO.delete(id);
    }

    public void enrollStudent(int studentId, int courseId) throws SQLException {
        studentDAO.enrollInCourse(studentId, courseId);
    }

    public List<Course> getEnrolledCourses(int studentId) throws SQLException {
        return courseDAO.findByStudent(studentId);
    }

    public List<Student> getStudentsInCourse(int courseId) throws SQLException {
        return studentDAO.findByCourse(courseId);
    }
}
