package com.student.cli;

import com.student.model.Course;
import com.student.model.Result;
import com.student.model.Student;
import com.student.service.CourseService;
import com.student.service.ResultService;
import com.student.service.StudentService;
import com.student.util.ConsoleColors;
import com.student.util.InputValidator;
import com.student.util.TablePrinter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CLI Menu for Result management.
 */
public class ResultMenu {

    private final ResultService  resultService  = new ResultService();
    private final StudentService studentService = new StudentService();
    private final CourseService  courseService  = new CourseService();

    private static final String[] EXAM_TYPES = {"MIDTERM", "FINAL", "QUIZ", "ASSIGNMENT", "LAB"};

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println(ConsoleColors.BOLD_GREEN + "╔══════════════════════════════╗");
            System.out.println("║      RESULT MANAGEMENT       ║");
            System.out.println("╚══════════════════════════════╝" + ConsoleColors.RESET);
            System.out.println("  1. Add Result for Student");
            System.out.println("  2. View Results by Student");
            System.out.println("  3. View Results by Course");
            System.out.println("  4. View Student Grade Summary");
            System.out.println("  5. Update Result");
            System.out.println("  6. Delete Result");
            System.out.println("  0. Back to Main Menu");
            int choice = InputValidator.readInt("  Enter choice: ", 0, 6);

            try {
                switch (choice) {
                    case 1 -> addResult();
                    case 2 -> viewByStudent();
                    case 3 -> viewByCourse();
                    case 4 -> viewGradeSummary();
                    case 5 -> updateResult();
                    case 6 -> deleteResult();
                    case 0 -> running = false;
                }
            } catch (SQLException e) {
                System.out.println(ConsoleColors.RED + "[ERROR] " + e.getMessage() + ConsoleColors.RESET);
            } catch (IllegalArgumentException e) {
                System.out.println(ConsoleColors.YELLOW + "[WARN] " + e.getMessage() + ConsoleColors.RESET);
            }
        }
    }

    private void addResult() throws SQLException {
        System.out.println(ConsoleColors.CYAN + "\n--- Add Result ---" + ConsoleColors.RESET);

        Student student = pickStudent();
        if (student == null) return;

        Course course = pickCourse();
        if (course == null) return;

        System.out.println("  Exam Types: ");
        for (int i = 0; i < EXAM_TYPES.length; i++) {
            System.out.printf("    [%d] %s%n", i + 1, EXAM_TYPES[i]);
        }
        int examChoice = InputValidator.readInt("  Select Exam Type: ", 1, EXAM_TYPES.length);
        String examType = EXAM_TYPES[examChoice - 1];

        double marks    = InputValidator.readDouble("  Marks Obtained (0-100): ", 0, 100);
        double maxMarks = InputValidator.readDouble("  Max Marks [100]: ", 1, 1000);
        String dateStr  = InputValidator.readOptional("  Exam Date (YYYY-MM-DD) [today]: ");
        String remarks  = InputValidator.readOptional("  Remarks (optional)            : ");

        LocalDate date;
        try {
            date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println(ConsoleColors.YELLOW + "  Invalid date format. Using today." + ConsoleColors.RESET);
            date = LocalDate.now();
        }

        Result r = resultService.addResult(student.getStudentId(), course.getCourseId(),
                marks, maxMarks, examType, date, remarks.isEmpty() ? null : remarks);
        System.out.printf(ConsoleColors.GREEN +
                "\n  ✔ Result added! Grade: %s (%.2f%%)%n" + ConsoleColors.RESET,
                r.getGrade(), r.getPercentage());
    }

    private void viewByStudent() throws SQLException {
        Student student = pickStudent();
        if (student == null) return;

        List<Result> results = resultService.getResultsByStudent(student.getStudentId());
        System.out.println(ConsoleColors.CYAN + "\n--- Results for " + student.getFullName() + " ---" + ConsoleColors.RESET);
        printResultTable(results);
    }

    private void viewByCourse() throws SQLException {
        Course course = pickCourse();
        if (course == null) return;

        List<Result> results = resultService.getResultsByCourse(course.getCourseId());
        System.out.println(ConsoleColors.CYAN + "\n--- Results for " + course.getCourseName() + " ---" + ConsoleColors.RESET);
        printResultTable(results);
    }

    private void viewGradeSummary() throws SQLException {
        Student student = pickStudent();
        if (student == null) return;

        List<String[]> summary = resultService.getStudentResultSummary(student.getStudentId());
        System.out.println(ConsoleColors.CYAN + "\n--- Grade Summary: " + student.getFullName() + " ---" + ConsoleColors.RESET);
        TablePrinter.print(
            new String[]{"Code", "Course Name", "Exam Type", "Obtained", "Max", "Percentage", "Grade"},
            summary
        );
    }

    private void updateResult() throws SQLException {
        int resultId = InputValidator.readInt("  Enter Result ID to update: ");
        double marks    = InputValidator.readDouble("  New Marks Obtained: ", 0, 1000);
        double maxMarks = InputValidator.readDouble("  New Max Marks     : ", 1, 1000);
        String dateStr  = InputValidator.readOptional("  New Exam Date (YYYY-MM-DD): ");
        String remarks  = InputValidator.readOptional("  New Remarks               : ");

        LocalDate date = null;
        if (!dateStr.isEmpty()) {
            try { date = LocalDate.parse(dateStr); } catch (DateTimeParseException ignored) {}
        }

        if (resultService.updateResult(resultId, marks, maxMarks, date, remarks.isEmpty() ? null : remarks)) {
            System.out.println(ConsoleColors.GREEN + "  ✔ Result updated!" + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.RED + "  Result not found." + ConsoleColors.RESET);
        }
    }

    private void deleteResult() throws SQLException {
        int resultId = InputValidator.readInt("  Enter Result ID to delete: ");
        if (InputValidator.confirm("  Delete this result?")) {
            if (resultService.deleteResult(resultId)) {
                System.out.println(ConsoleColors.GREEN + "  ✔ Result deleted." + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "  Result not found." + ConsoleColors.RESET);
            }
        }
    }

    private Student pickStudent() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Student Roll Number: ");
        Optional<Student> opt = studentService.findByRoll(roll);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return null;
        }
        return opt.get();
    }

    private Course pickCourse() throws SQLException {
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  No courses available." + ConsoleColors.RESET);
            return null;
        }
        System.out.println("  Available Courses:");
        for (Course c : courses) {
            System.out.printf("    [%d] %s - %s%n", c.getCourseId(), c.getCourseCode(), c.getCourseName());
        }
        int id = InputValidator.readInt("  Enter Course ID: ");
        Optional<Course> opt = courseService.findById(id);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Course not found." + ConsoleColors.RESET);
            return null;
        }
        return opt.get();
    }

    private void printResultTable(List<Result> results) {
        List<String[]> rows = new ArrayList<>();
        for (Result r : results) {
            rows.add(new String[]{
                String.valueOf(r.getResultId()),
                r.getRollNumber() != null ? r.getRollNumber() : "—",
                r.getStudentName() != null ? r.getStudentName() : "—",
                r.getCourseCode() != null ? r.getCourseCode() : "—",
                r.getExamType(),
                String.format("%.1f / %.1f", r.getMarksObtained(), r.getMaxMarks()),
                String.format("%.2f%%", r.getPercentage()),
                r.getGrade()
            });
        }
        TablePrinter.print(new String[]{"ID", "Roll", "Student", "Course", "Exam", "Marks", "%", "Grade"}, rows);
    }
}
