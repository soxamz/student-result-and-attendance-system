package com.student.dao;

import com.student.model.Attendance;
import com.student.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Attendance operations.
 */
public class AttendanceDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public Attendance create(Attendance a) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, course_id, attendance_date, status) " +
                     "VALUES (?, ?, ?, ?) RETURNING attendance_id, created_at";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, a.getStudentId());
            ps.setInt(2, a.getCourseId());
            ps.setDate(3, Date.valueOf(a.getAttendanceDate()));
            ps.setString(4, a.getStatus().name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                a.setAttendanceId(rs.getInt("attendance_id"));
                a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
        }
        return a;
    }

    public boolean upsert(Attendance a) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, course_id, attendance_date, status) VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT (student_id, course_id, attendance_date) DO UPDATE SET status = EXCLUDED.status";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, a.getStudentId());
            ps.setInt(2, a.getCourseId());
            ps.setDate(3, Date.valueOf(a.getAttendanceDate()));
            ps.setString(4, a.getStatus().name());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Attendance> findByStudent(int studentId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.roll_number, s.first_name||' '||s.last_name AS student_name, " +
                     "c.course_code, c.course_name FROM attendance a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.student_id = ? ORDER BY a.attendance_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Attendance> findByStudentAndCourse(int studentId, int courseId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.roll_number, s.first_name||' '||s.last_name AS student_name, " +
                     "c.course_code, c.course_name FROM attendance a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.student_id=? AND a.course_id=? ORDER BY a.attendance_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Attendance> findByCourseAndDate(int courseId, LocalDate date) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.roll_number, s.first_name||' '||s.last_name AS student_name, " +
                     "c.course_code, c.course_name FROM attendance a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.course_id=? AND a.attendance_date=? ORDER BY s.roll_number";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Get attendance percentage summary per student per course */
    public List<String[]> getAttendanceSummary(int studentId) throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT course_code, course_name, total_classes, present_count, " +
                     "absent_count, late_count, attendance_percentage FROM attendance_summary " +
                     "WHERE roll_number = (SELECT roll_number FROM students WHERE student_id=?) " +
                     "ORDER BY course_code";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    String.valueOf(rs.getInt("total_classes")),
                    String.valueOf(rs.getInt("present_count")),
                    String.valueOf(rs.getInt("absent_count")),
                    String.valueOf(rs.getInt("late_count")),
                    rs.getDouble("attendance_percentage") + "%"
                });
            }
        }
        return rows;
    }

    public boolean delete(int attendanceId) throws SQLException {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            return ps.executeUpdate() > 0;
        }
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setCourseId(rs.getInt("course_id"));
        a.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
        a.setStatus(Attendance.Status.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) a.setCreatedAt(ts.toLocalDateTime());
        try { a.setRollNumber(rs.getString("roll_number")); } catch (Exception ignored) {}
        try { a.setStudentName(rs.getString("student_name")); } catch (Exception ignored) {}
        try { a.setCourseCode(rs.getString("course_code")); } catch (Exception ignored) {}
        try { a.setCourseName(rs.getString("course_name")); } catch (Exception ignored) {}
        return a;
    }
}
