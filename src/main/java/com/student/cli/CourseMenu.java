package com.student.cli;

import com.student.model.Course;
import com.student.model.Student;
import com.student.service.CourseService;
import com.student.service.StudentService;
import com.student.util.ConsoleColors;
import com.student.util.InputValidator;
import com.student.util.TablePrinter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CLI Menu for Course management.
 */
public class CourseMenu {

    private final CourseService   courseService   = new CourseService();
    private final StudentService  studentService  = new StudentService();

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println(ConsoleColors.BOLD_CYAN + "╔══════════════════════════════╗");
            System.out.println("║      COURSE MANAGEMENT       ║");
            System.out.println("╚══════════════════════════════╝" + ConsoleColors.RESET);
            System.out.println("  1. Add New Course");
            System.out.println("  2. View All Courses");
            System.out.println("  3. Search Course by Code");
            System.out.println("  4. Update Course");
            System.out.println("  5. Delete Course");
            System.out.println("  6. View Students in a Course");
            System.out.println("  0. Back to Main Menu");
            int choice = InputValidator.readInt("  Enter choice: ", 0, 6);

            try {
                switch (choice) {
                    case 1 -> addCourse();
                    case 2 -> viewAll();
                    case 3 -> searchByCode();
                    case 4 -> updateCourse();
                    case 5 -> deleteCourse();
                    case 6 -> viewStudentsInCourse();
                    case 0 -> running = false;
                }
            } catch (SQLException e) {
                System.out.println(ConsoleColors.RED + "[ERROR] " + e.getMessage() + ConsoleColors.RESET);
            } catch (IllegalArgumentException e) {
                System.out.println(ConsoleColors.YELLOW + "[WARN] " + e.getMessage() + ConsoleColors.RESET);
            }
        }
    }

    private void addCourse() throws SQLException {
        System.out.println(ConsoleColors.CYAN + "\n--- Add New Course ---" + ConsoleColors.RESET);
        String code    = InputValidator.readNonEmpty("  Course Code (e.g. CS101): ");
        String name    = InputValidator.readNonEmpty("  Course Name             : ");
        int    credits = InputValidator.readInt("  Credits (1-6)           : ", 1, 6);

        Course c = courseService.addCourse(code.toUpperCase(), name, credits);
        System.out.printf(ConsoleColors.GREEN + "\n  ✔ Course added! [ID: %d]%n" + ConsoleColors.RESET, c.getCourseId());
    }

    private void viewAll() throws SQLException {
        List<Course> courses = courseService.getAllCourses();
        System.out.println(ConsoleColors.CYAN + "\n--- All Courses ---" + ConsoleColors.RESET);
        printCourseTable(courses);
    }

    private void searchByCode() throws SQLException {
        String code = InputValidator.readNonEmpty("  Course Code: ");
        Optional<Course> opt = courseService.findByCode(code);
        if (opt.isPresent()) {
            List<Course> list = new ArrayList<>();
            list.add(opt.get());
            printCourseTable(list);
        } else {
            System.out.println(ConsoleColors.YELLOW + "  No course found with code: " + code + ConsoleColors.RESET);
        }
    }

    private void updateCourse() throws SQLException {
        int id = InputValidator.readInt("  Enter Course ID to update: ");
        Optional<Course> opt = courseService.findById(id);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Course not found." + ConsoleColors.RESET);
            return;
        }
        Course c = opt.get();
        System.out.println("  Current: " + c);
        System.out.println("  (Press Enter to keep current value)");

        String code = InputValidator.readOptional("  Code    [" + c.getCourseCode() + "]: ");
        String name = InputValidator.readOptional("  Name    [" + c.getCourseName() + "]: ");
        String cr   = InputValidator.readOptional("  Credits [" + c.getCredits() + "]: ");

        if (!code.isEmpty()) c.setCourseCode(code.toUpperCase());
        if (!name.isEmpty()) c.setCourseName(name);
        if (!cr.isEmpty()) {
            try { c.setCredits(Integer.parseInt(cr)); } catch (NumberFormatException ignored) {}
        }

        if (courseService.updateCourse(c)) {
            System.out.println(ConsoleColors.GREEN + "  ✔ Course updated!" + ConsoleColors.RESET);
        }
    }

    private void deleteCourse() throws SQLException {
        int id = InputValidator.readInt("  Enter Course ID to delete: ");
        Optional<Course> opt = courseService.findById(id);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Course not found." + ConsoleColors.RESET);
            return;
        }
        System.out.println("  Found: " + opt.get().getCourseCode() + " - " + opt.get().getCourseName());
        if (InputValidator.confirm("  Delete this course?")) {
            courseService.deleteCourse(id);
            System.out.println(ConsoleColors.GREEN + "  ✔ Course deleted." + ConsoleColors.RESET);
        }
    }

    private void viewStudentsInCourse() throws SQLException {
        int id = InputValidator.readInt("  Enter Course ID: ");
        Optional<Course> cOpt = courseService.findById(id);
        if (cOpt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Course not found." + ConsoleColors.RESET);
            return;
        }
        List<Student> students = studentService.getStudentsInCourse(id);
        System.out.println(ConsoleColors.CYAN + "\n--- Students in " + cOpt.get().getCourseName() + " ---" + ConsoleColors.RESET);
        List<String[]> rows = new ArrayList<>();
        for (Student s : students) {
            rows.add(new String[]{
                String.valueOf(s.getStudentId()), s.getRollNumber(),
                s.getFullName(), s.getDepartment(), String.valueOf(s.getSemester())
            });
        }
        TablePrinter.print(new String[]{"ID", "Roll No.", "Name", "Department", "Sem"}, rows);
    }

    private void printCourseTable(List<Course> courses) {
        List<String[]> rows = new ArrayList<>();
        for (Course c : courses) {
            rows.add(new String[]{
                String.valueOf(c.getCourseId()), c.getCourseCode(),
                c.getCourseName(), String.valueOf(c.getCredits())
            });
        }
        TablePrinter.print(new String[]{"ID", "Code", "Course Name", "Credits"}, rows);
    }
}
