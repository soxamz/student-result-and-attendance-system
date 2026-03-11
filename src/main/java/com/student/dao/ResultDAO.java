package com.student.dao;

import com.student.model.Result;
import com.student.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public Result create(Result r) throws SQLException {
        String sql = "INSERT INTO results (student_id, course_id, marks_obtained, max_marks, exam_type, exam_date, remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING result_id, created_at";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, r.getStudentId());
            ps.setInt(2, r.getCourseId());
            ps.setDouble(3, r.getMarksObtained());
            ps.setDouble(4, r.getMaxMarks());
            ps.setString(5, r.getExamType());
            ps.setDate(6, r.getExamDate() != null ? Date.valueOf(r.getExamDate()) : null);
            ps.setString(7, r.getRemarks());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                r.setResultId(rs.getInt("result_id"));
                r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
        }
        return r;
    }

    public List<Result> findByStudent(int studentId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, s.roll_number, s.first_name||' '||s.last_name AS student_name, " +
                     "c.course_code, c.course_name FROM results r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN courses c ON r.course_id = c.course_id " +
                     "WHERE r.student_id = ? ORDER BY c.course_code, r.exam_type";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Result> findByCourse(int courseId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, s.roll_number, s.first_name||' '||s.last_name AS student_name, " +
                     "c.course_code, c.course_name FROM results r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN courses c ON r.course_id = c.course_id " +
                     "WHERE r.course_id = ? ORDER BY s.roll_number";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<Result> findByStudentCourseExam(int studentId, int courseId, String examType) throws SQLException {
        String sql = "SELECT r.*, s.roll_number, s.first_name||' '||s.last_name AS student_name, " +
                     "c.course_code, c.course_name FROM results r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN courses c ON r.course_id = c.course_id " +
                     "WHERE r.student_id=? AND r.course_id=? AND r.exam_type=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setString(3, examType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    public boolean update(Result r) throws SQLException {
        String sql = "UPDATE results SET marks_obtained=?, max_marks=?, exam_date=?, remarks=? WHERE result_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, r.getMarksObtained());
            ps.setDouble(2, r.getMaxMarks());
            ps.setDate(3, r.getExamDate() != null ? Date.valueOf(r.getExamDate()) : null);
            ps.setString(4, r.getRemarks());
            ps.setInt(5, r.getResultId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int resultId) throws SQLException {
        String sql = "DELETE FROM results WHERE result_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, resultId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<String[]> getStudentSummary(int studentId) throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT course_code, course_name, marks_obtained, max_marks, " +
                     "ROUND((marks_obtained/max_marks)*100,2) AS pct, exam_type FROM student_result_summary " +
                     "WHERE roll_number = (SELECT roll_number FROM students WHERE student_id=?) ORDER BY course_code";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double pct = rs.getDouble("pct");
                rows.add(new String[]{
                    rs.getString("course_code"), rs.getString("course_name"),
                    rs.getString("exam_type"),
                    String.format("%.1f", rs.getDouble("marks_obtained")),
                    String.format("%.1f", rs.getDouble("max_marks")),
                    String.format("%.2f%%", pct), gradeFromPct(pct)
                });
            }
        }
        return rows;
    }

    private String gradeFromPct(double pct) {
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        return "F";
    }

    private Result mapRow(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setResultId(rs.getInt("result_id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setCourseId(rs.getInt("course_id"));
        r.setMarksObtained(rs.getDouble("marks_obtained"));
        r.setMaxMarks(rs.getDouble("max_marks"));
        r.setExamType(rs.getString("exam_type"));
        Date d = rs.getDate("exam_date");
        if (d != null) r.setExamDate(d.toLocalDate());
        r.setRemarks(rs.getString("remarks"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());
        try { r.setRollNumber(rs.getString("roll_number")); } catch (Exception ignored) {}
        try { r.setStudentName(rs.getString("student_name")); } catch (Exception ignored) {}
        try { r.setCourseCode(rs.getString("course_code")); } catch (Exception ignored) {}
        try { r.setCourseName(rs.getString("course_name")); } catch (Exception ignored) {}
        return r;
    }
}
