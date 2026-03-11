package com.student.service;

import com.student.dao.ResultDAO;
import com.student.model.Result;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Business logic layer for Result operations.
 */
public class ResultService {

    private final ResultDAO resultDAO = new ResultDAO();

    public Result addResult(int studentId, int courseId, double marks, double maxMarks,
                            String examType, LocalDate examDate, String remarks) throws SQLException {
        Optional<Result> existing = resultDAO.findByStudentCourseExam(studentId, courseId, examType);
        if (existing.isPresent()) {
            throw new IllegalArgumentException(
                "Result for this student/course/exam-type already exists. Use update instead.");
        }
        Result r = new Result(studentId, courseId, marks, maxMarks, examType, examDate, remarks);
        return resultDAO.create(r);
    }

    public List<Result> getResultsByStudent(int studentId) throws SQLException {
        return resultDAO.findByStudent(studentId);
    }

    public List<Result> getResultsByCourse(int courseId) throws SQLException {
        return resultDAO.findByCourse(courseId);
    }

    public List<String[]> getStudentResultSummary(int studentId) throws SQLException {
        return resultDAO.getStudentSummary(studentId);
    }

    public boolean updateResult(int resultId, double marks, double maxMarks,
                                LocalDate examDate, String remarks) throws SQLException {
        Result r = new Result();
        r.setResultId(resultId);
        r.setMarksObtained(marks);
        r.setMaxMarks(maxMarks);
        r.setExamDate(examDate);
        r.setRemarks(remarks);
        return resultDAO.update(r);
    }

    public boolean deleteResult(int resultId) throws SQLException {
        return resultDAO.delete(resultId);
    }
}
