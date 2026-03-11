-- Student Result & Attendance System - Database Schema
-- Run this in pgAdmin to set up the database

CREATE DATABASE student_system;
\c student_system;

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    course_id   SERIAL PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    credits     INT NOT NULL DEFAULT 3,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students table
CREATE TABLE IF NOT EXISTS students (
    student_id   SERIAL PRIMARY KEY,
    roll_number  VARCHAR(20) UNIQUE NOT NULL,
    first_name   VARCHAR(50) NOT NULL,
    last_name    VARCHAR(50) NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    phone        VARCHAR(15),
    department   VARCHAR(50),
    semester     INT NOT NULL DEFAULT 1,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Enrollments (student-course mapping)
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id    INT REFERENCES students(student_id) ON DELETE CASCADE,
    course_id     INT REFERENCES courses(course_id) ON DELETE CASCADE,
    enrolled_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, course_id)
);

-- Results table
CREATE TABLE IF NOT EXISTS results (
    result_id     SERIAL PRIMARY KEY,
    student_id    INT REFERENCES students(student_id) ON DELETE CASCADE,
    course_id     INT REFERENCES courses(course_id) ON DELETE CASCADE,
    marks_obtained DECIMAL(5,2) NOT NULL CHECK (marks_obtained >= 0 AND marks_obtained <= 100),
    max_marks     DECIMAL(5,2) NOT NULL DEFAULT 100,
    exam_type     VARCHAR(30) NOT NULL DEFAULT 'FINAL', -- MIDTERM, FINAL, QUIZ, etc.
    exam_date     DATE,
    remarks       TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, course_id, exam_type)
);

-- Attendance table
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id SERIAL PRIMARY KEY,
    student_id    INT REFERENCES students(student_id) ON DELETE CASCADE,
    course_id     INT REFERENCES courses(course_id) ON DELETE CASCADE,
    attendance_date DATE NOT NULL,
    status        VARCHAR(10) NOT NULL CHECK (status IN ('PRESENT', 'ABSENT', 'LATE')),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, course_id, attendance_date)
);

-- Indexes for performance
CREATE INDEX idx_students_roll ON students(roll_number);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
CREATE INDEX idx_results_student ON results(student_id);
CREATE INDEX idx_attendance_student ON attendance(student_id);

-- View: Student result summary
CREATE OR REPLACE VIEW student_result_summary AS
SELECT
    s.roll_number,
    s.first_name || ' ' || s.last_name AS student_name,
    c.course_code,
    c.course_name,
    r.marks_obtained,
    r.max_marks,
    ROUND((r.marks_obtained / r.max_marks) * 100, 2) AS percentage,
    CASE
        WHEN (r.marks_obtained / r.max_marks) * 100 >= 90 THEN 'A+'
        WHEN (r.marks_obtained / r.max_marks) * 100 >= 80 THEN 'A'
        WHEN (r.marks_obtained / r.max_marks) * 100 >= 70 THEN 'B'
        WHEN (r.marks_obtained / r.max_marks) * 100 >= 60 THEN 'C'
        WHEN (r.marks_obtained / r.max_marks) * 100 >= 50 THEN 'D'
        ELSE 'F'
    END AS grade,
    r.exam_type
FROM results r
JOIN students s ON r.student_id = s.student_id
JOIN courses c ON r.course_id = c.course_id;

-- View: Attendance summary
CREATE OR REPLACE VIEW attendance_summary AS
SELECT
    s.roll_number,
    s.first_name || ' ' || s.last_name AS student_name,
    c.course_code,
    c.course_name,
    COUNT(*) AS total_classes,
    SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
    SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count,
    SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END) AS late_count,
    ROUND(
        (SUM(CASE WHEN a.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END)::DECIMAL / COUNT(*)) * 100,
    2) AS attendance_percentage
FROM attendance a
JOIN students s ON a.student_id = s.student_id
JOIN courses c ON a.course_id = c.course_id
GROUP BY s.roll_number, student_name, c.course_code, c.course_name;
