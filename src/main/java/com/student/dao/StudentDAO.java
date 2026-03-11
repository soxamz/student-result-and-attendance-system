package com.student.dao;

import com.student.model.Student;
import com.student.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Student operations.
 */
public class StudentDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public Student create(Student s) throws SQLException {
        String sql = "INSERT INTO students (roll_number, first_name, last_name, email, phone, department, semester) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING student_id, created_at";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, s.getRollNumber());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getDepartment());
            ps.setInt(7, s.getSemester());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                s.setStudentId(rs.getInt("student_id"));
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
        }
        return s;
    }

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY roll_number";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    public Optional<Student> findByRollNumber(String roll) throws SQLException {
        String sql = "SELECT * FROM students WHERE roll_number = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roll);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    public List<Student> findByDepartment(String dept) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE department ILIKE ? ORDER BY roll_number";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, "%" + dept + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean update(Student s) throws SQLException {
        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, phone=?, " +
                     "department=?, semester=? WHERE student_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getDepartment());
            ps.setInt(6, s.getSemester());
            ps.setInt(7, s.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean enrollInCourse(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Student> findByCourse(int courseId) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.* FROM students s JOIN enrollments e ON s.student_id = e.student_id " +
                     "WHERE e.course_id = ? ORDER BY s.roll_number";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setDepartment(rs.getString("department"));
        s.setSemester(rs.getInt("semester"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    }
}
