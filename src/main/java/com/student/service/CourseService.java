package com.student.service;

import com.student.dao.CourseDAO;
import com.student.model.Course;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Business logic layer for Course operations.
 */
public class CourseService {

    private final CourseDAO courseDAO = new CourseDAO();

    public Course addCourse(String code, String name, int credits) throws SQLException {
        if (courseDAO.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Course with code '" + code + "' already exists.");
        }
        Course c = new Course(code, name, credits);
        return courseDAO.create(c);
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.findAll();
    }

    public Optional<Course> findById(int id) throws SQLException {
        return courseDAO.findById(id);
    }

    public Optional<Course> findByCode(String code) throws SQLException {
        return courseDAO.findByCode(code);
    }

    public boolean updateCourse(Course c) throws SQLException {
        return courseDAO.update(c);
    }

    public boolean deleteCourse(int id) throws SQLException {
        return courseDAO.delete(id);
    }
}
