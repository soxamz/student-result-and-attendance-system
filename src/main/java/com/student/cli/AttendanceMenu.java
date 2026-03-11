package com.student.cli;

import com.student.model.Attendance;
import com.student.model.Course;
import com.student.model.Student;
import com.student.service.AttendanceService;
import com.student.service.CourseService;
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
 * CLI Menu for Attendance management.
 */
public class AttendanceMenu {

    private final AttendanceService attendanceService = new AttendanceService();
    private final StudentService    studentService    = new StudentService();
    private final CourseService     courseService     = new CourseService();

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println(ConsoleColors.BOLD_YELLOW + "╔══════════════════════════════╗");
            System.out.println("║    ATTENDANCE MANAGEMENT     ║");
            System.out.println("╚══════════════════════════════╝" + ConsoleColors.RESET);
            System.out.println("  1. Mark Attendance (Single Student)");
            System.out.println("  2. Mark Bulk Attendance (Whole Class)");
            System.out.println("  3. View Attendance by Student");
            System.out.println("  4. View Attendance by Course & Date");
            System.out.println("  5. View Attendance Summary (Student)");
            System.out.println("  6. View Student-Course Attendance Detail");
            System.out.println("  0. Back to Main Menu");
            int choice = InputValidator.readInt("  Enter choice: ", 0, 6);

            try {
                switch (choice) {
                    case 1 -> markSingle();
                    case 2 -> markBulk();
                    case 3 -> viewByStudent();
                    case 4 -> viewByCourseDate();
                    case 5 -> viewSummary();
                    case 6 -> viewStudentCourseDetail();
                    case 0 -> running = false;
                }
            } catch (SQLException e) {
                System.out.println(ConsoleColors.RED + "[ERROR] " + e.getMessage() + ConsoleColors.RESET);
            }
        }
    }

    private void markSingle() throws SQLException {
        System.out.println(ConsoleColors.CYAN + "\n--- Mark Attendance ---" + ConsoleColors.RESET);
        String roll = InputValidator.readNonEmpty("  Student Roll Number: ");
        Optional<Student> sOpt = studentService.findByRoll(roll);
        if (sOpt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return;
        }

        Course course = pickCourse();
        if (course == null) return;

        String dateStr = InputValidator.readOptional("  Date (YYYY-MM-DD) [today]: ");
        LocalDate date;
        try {
            date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            date = LocalDate.now();
        }

        Attendance.Status status = pickStatus();
        attendanceService.markAttendance(sOpt.get().getStudentId(), course.getCourseId(), date, status);
        System.out.printf(ConsoleColors.GREEN + "\n  ✔ Marked %s as %s on %s%n" + ConsoleColors.RESET,
                sOpt.get().getFullName(), status, date);
    }

    private void markBulk() throws SQLException {
        System.out.println(ConsoleColors.CYAN + "\n--- Bulk Attendance for Entire Class ---" + ConsoleColors.RESET);
        Course course = pickCourse();
        if (course == null) return;

        String dateStr = InputValidator.readOptional("  Date (YYYY-MM-DD) [today]: ");
        LocalDate date;
        try {
            date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            date = LocalDate.now();
        }

        List<Student> students = studentService.getStudentsInCourse(course.getCourseId());
        if (students.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  No students enrolled in this course." + ConsoleColors.RESET);
            return;
        }

        System.out.printf("  Marking attendance for %s on %s%n", course.getCourseName(), date);
        System.out.println("  Status options: 1=PRESENT  2=ABSENT  3=LATE");
        int marked = 0;
        for (Student s : students) {
            System.out.printf("  %-15s %-30s -> ", s.getRollNumber(), s.getFullName());
            int statusChoice = InputValidator.readInt("", 1, 3);
            Attendance.Status status = switch (statusChoice) {
                case 1 -> Attendance.Status.PRESENT;
                case 2 -> Attendance.Status.ABSENT;
                default -> Attendance.Status.LATE;
            };
            attendanceService.markAttendance(s.getStudentId(), course.getCourseId(), date, status);
            marked++;
        }
        System.out.printf(ConsoleColors.GREEN + "\n  ✔ Attendance marked for %d students.%n" + ConsoleColors.RESET, marked);
    }

    private void viewByStudent() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Student Roll Number: ");
        Optional<Student> opt = studentService.findByRoll(roll);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return;
        }
        List<Attendance> records = attendanceService.getAttendanceByStudent(opt.get().getStudentId());
        System.out.println(ConsoleColors.CYAN + "\n--- Attendance for " + opt.get().getFullName() + " ---" + ConsoleColors.RESET);
        printAttendanceTable(records);
    }

    private void viewByCourseDate() throws SQLException {
        Course course = pickCourse();
        if (course == null) return;

        String dateStr = InputValidator.readNonEmpty("  Date (YYYY-MM-DD): ");
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println(ConsoleColors.YELLOW + "  Invalid date." + ConsoleColors.RESET);
            return;
        }

        List<Attendance> records = attendanceService.getAttendanceByCourseAndDate(course.getCourseId(), date);
        System.out.println(ConsoleColors.CYAN + "\n--- Attendance: " + course.getCourseName() + " on " + date + " ---" + ConsoleColors.RESET);
        printAttendanceTable(records);
    }

    private void viewSummary() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Student Roll Number: ");
        Optional<Student> opt = studentService.findByRoll(roll);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return;
        }
        List<String[]> summary = attendanceService.getAttendanceSummary(opt.get().getStudentId());
        System.out.println(ConsoleColors.CYAN + "\n--- Attendance Summary: " + opt.get().getFullName() + " ---" + ConsoleColors.RESET);
        TablePrinter.print(
            new String[]{"Course", "Course Name", "Total", "Present", "Absent", "Late", "Attendance %"},
            summary
        );

        // Warn about low attendance
        for (String[] row : summary) {
            String pctStr = row[6].replace("%", "");
            try {
                double pct = Double.parseDouble(pctStr);
                if (pct < 75) {
                    System.out.printf(ConsoleColors.RED +
                        "  ⚠ LOW ATTENDANCE in %s: %.1f%% (Minimum required: 75%%)%n" + ConsoleColors.RESET,
                        row[0], pct);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private void viewStudentCourseDetail() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Student Roll Number: ");
        Optional<Student> sOpt = studentService.findByRoll(roll);
        if (sOpt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return;
        }
        Course course = pickCourse();
        if (course == null) return;

        List<Attendance> records = attendanceService.getAttendanceByStudentAndCourse(
                sOpt.get().getStudentId(), course.getCourseId());
        System.out.printf(ConsoleColors.CYAN + "\n--- %s in %s Attendance ---%n" + ConsoleColors.RESET,
                sOpt.get().getFullName(), course.getCourseName());
        printAttendanceTable(records);
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

    private Attendance.Status pickStatus() {
        System.out.println("  Status: 1=PRESENT  2=ABSENT  3=LATE");
        int c = InputValidator.readInt("  Choose: ", 1, 3);
        return switch (c) {
            case 1 -> Attendance.Status.PRESENT;
            case 2 -> Attendance.Status.ABSENT;
            default -> Attendance.Status.LATE;
        };
    }

    private void printAttendanceTable(List<Attendance> records) {
        List<String[]> rows = new ArrayList<>();
        for (Attendance a : records) {
            String statusColor = switch (a.getStatus()) {
                case PRESENT -> "PRESENT";
                case ABSENT  -> "ABSENT";
                case LATE    -> "LATE";
            };
            rows.add(new String[]{
                String.valueOf(a.getAttendanceId()),
                a.getRollNumber() != null ? a.getRollNumber() : "—",
                a.getStudentName() != null ? a.getStudentName() : "—",
                a.getCourseCode() != null ? a.getCourseCode() : "—",
                String.valueOf(a.getAttendanceDate()),
                statusColor
            });
        }
        TablePrinter.print(new String[]{"ID", "Roll", "Student", "Course", "Date", "Status"}, rows);
    }
}
