package com.student.service;

import com.student.dao.AttendanceDAO;
import com.student.model.Attendance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Business logic layer for Attendance operations.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    /** Mark attendance for a student (upserts existing record) */
    public boolean markAttendance(int studentId, int courseId,
                                  LocalDate date, Attendance.Status status) throws SQLException {
        Attendance a = new Attendance(studentId, courseId, date, status);
        return attendanceDAO.upsert(a);
    }

    public List<Attendance> getAttendanceByStudent(int studentId) throws SQLException {
        return attendanceDAO.findByStudent(studentId);
    }

    public List<Attendance> getAttendanceByCourseAndDate(int courseId, LocalDate date) throws SQLException {
        return attendanceDAO.findByCourseAndDate(courseId, date);
    }

    public List<Attendance> getAttendanceByStudentAndCourse(int studentId, int courseId) throws SQLException {
        return attendanceDAO.findByStudentAndCourse(studentId, courseId);
    }

    public List<String[]> getAttendanceSummary(int studentId) throws SQLException {
        return attendanceDAO.getAttendanceSummary(studentId);
    }

    public boolean deleteRecord(int attendanceId) throws SQLException {
        return attendanceDAO.delete(attendanceId);
    }
}
