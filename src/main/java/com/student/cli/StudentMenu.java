package com.student.cli;

import com.student.model.Student;
import com.student.model.Course;
import com.student.service.StudentService;
import com.student.service.CourseService;
import com.student.util.ConsoleColors;
import com.student.util.InputValidator;
import com.student.util.TablePrinter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CLI Menu for Student management.
 */
public class StudentMenu {

    private final StudentService studentService = new StudentService();
    private final CourseService  courseService  = new CourseService();

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println(ConsoleColors.BOLD_BLUE + "╔══════════════════════════════╗");
            System.out.println("║      STUDENT MANAGEMENT      ║");
            System.out.println("╚══════════════════════════════╝" + ConsoleColors.RESET);
            System.out.println("  1. Add New Student");
            System.out.println("  2. View All Students");
            System.out.println("  3. Search Student by Roll Number");
            System.out.println("  4. Search Students by Department");
            System.out.println("  5. Update Student");
            System.out.println("  6. Delete Student");
            System.out.println("  7. Enroll Student in Course");
            System.out.println("  8. View Student's Enrolled Courses");
            System.out.println("  0. Back to Main Menu");
            int choice = InputValidator.readInt("  Enter choice: ", 0, 8);

            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewAll();
                    case 3 -> searchByRoll();
                    case 4 -> searchByDept();
                    case 5 -> updateStudent();
                    case 6 -> deleteStudent();
                    case 7 -> enrollStudent();
                    case 8 -> viewEnrolledCourses();
                    case 0 -> running = false;
                }
            } catch (SQLException e) {
                System.out.println(ConsoleColors.RED + "[ERROR] " + e.getMessage() + ConsoleColors.RESET);
            } catch (IllegalArgumentException e) {
                System.out.println(ConsoleColors.YELLOW + "[WARN] " + e.getMessage() + ConsoleColors.RESET);
            }
        }
    }

    private void addStudent() throws SQLException {
        System.out.println(ConsoleColors.CYAN + "\n--- Add New Student ---" + ConsoleColors.RESET);
        String roll   = InputValidator.readNonEmpty("  Roll Number    : ");
        String first  = InputValidator.readNonEmpty("  First Name     : ");
        String last   = InputValidator.readNonEmpty("  Last Name      : ");
        String email  = InputValidator.readNonEmpty("  Email          : ");
        String phone  = InputValidator.readOptional("  Phone (optional): ");
        String dept   = InputValidator.readNonEmpty("  Department     : ");
        int    sem    = InputValidator.readInt("  Semester (1-8) : ", 1, 8);

        Student s = studentService.addStudent(roll, first, last, email,
                phone.isEmpty() ? null : phone, dept, sem);
        System.out.printf(ConsoleColors.GREEN + "\n  ✔ Student added successfully! [ID: %d]%n" + ConsoleColors.RESET, s.getStudentId());
    }

    private void viewAll() throws SQLException {
        List<Student> students = studentService.getAllStudents();
        System.out.println(ConsoleColors.CYAN + "\n--- All Students ---" + ConsoleColors.RESET);
        printStudentTable(students);
    }

    private void searchByRoll() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Enter Roll Number: ");
        Optional<Student> opt = studentService.findByRoll(roll);
        if (opt.isPresent()) {
            List<Student> list = new ArrayList<>();
            list.add(opt.get());
            printStudentTable(list);
        } else {
            System.out.println(ConsoleColors.YELLOW + "  No student found with roll: " + roll + ConsoleColors.RESET);
        }
    }

    private void searchByDept() throws SQLException {
        String dept = InputValidator.readNonEmpty("  Enter Department: ");
        List<Student> students = studentService.findByDepartment(dept);
        System.out.println(ConsoleColors.CYAN + "\n--- Students in Department: " + dept + " ---" + ConsoleColors.RESET);
        printStudentTable(students);
    }

    private void updateStudent() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Enter Roll Number to update: ");
        Optional<Student> opt = studentService.findByRoll(roll);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return;
        }
        Student s = opt.get();
        System.out.println("  Current: " + s);
        System.out.println("  (Press Enter to keep current value)");

        String first = InputValidator.readOptional("  First Name [" + s.getFirstName() + "]: ");
        String last  = InputValidator.readOptional("  Last Name  [" + s.getLastName() + "]: ");
        String email = InputValidator.readOptional("  Email      [" + s.getEmail() + "]: ");
        String phone = InputValidator.readOptional("  Phone      [" + (s.getPhone() != null ? s.getPhone() : "") + "]: ");
        String dept  = InputValidator.readOptional("  Department [" + s.getDepartment() + "]: ");
        String semStr= InputValidator.readOptional("  Semester   [" + s.getSemester() + "]: ");

        if (!first.isEmpty())  s.setFirstName(first);
        if (!last.isEmpty())   s.setLastName(last);
        if (!email.isEmpty())  s.setEmail(email);
        if (!phone.isEmpty())  s.setPhone(phone);
        if (!dept.isEmpty())   s.setDepartment(dept);
        if (!semStr.isEmpty()) {
            try { s.setSemester(Integer.parseInt(semStr)); } catch (NumberFormatException ignored) {}
        }

        if (studentService.updateStudent(s)) {
            System.out.println(ConsoleColors.GREEN + "  ✔ Student updated successfully!" + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.RED + "  Update failed." + ConsoleColors.RESET);
        }
    }

    private void deleteStudent() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Enter Roll Number to delete: ");
        Optional<Student> opt = studentService.findByRoll(roll);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return;
        }
        System.out.println("  Found: " + opt.get().getFullName());
        if (InputValidator.confirm("  Are you sure you want to delete?")) {
            if (studentService.deleteStudent(opt.get().getStudentId())) {
                System.out.println(ConsoleColors.GREEN + "  ✔ Student deleted." + ConsoleColors.RESET);
            }
        } else {
            System.out.println("  Cancelled.");
        }
    }

    private void enrollStudent() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Student Roll Number: ");
        Optional<Student> sOpt = studentService.findByRoll(roll);
        if (sOpt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return;
        }

        // Show available courses
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  No courses available. Add courses first." + ConsoleColors.RESET);
            return;
        }
        System.out.println("\n  Available Courses:");
        for (Course c : courses) {
            System.out.printf("    [%d] %s - %s%n", c.getCourseId(), c.getCourseCode(), c.getCourseName());
        }

        int courseId = InputValidator.readInt("  Enter Course ID to enroll: ");
        Optional<Course> cOpt = courseService.findById(courseId);
        if (cOpt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Course not found." + ConsoleColors.RESET);
            return;
        }

        studentService.enrollStudent(sOpt.get().getStudentId(), courseId);
        System.out.printf(ConsoleColors.GREEN + "  ✔ %s enrolled in %s%n" + ConsoleColors.RESET,
                sOpt.get().getFullName(), cOpt.get().getCourseName());
    }

    private void viewEnrolledCourses() throws SQLException {
        String roll = InputValidator.readNonEmpty("  Student Roll Number: ");
        Optional<Student> opt = studentService.findByRoll(roll);
        if (opt.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  Student not found." + ConsoleColors.RESET);
            return;
        }
        List<Course> courses = studentService.getEnrolledCourses(opt.get().getStudentId());
        System.out.println(ConsoleColors.CYAN + "\n--- Enrolled Courses for " + opt.get().getFullName() + " ---" + ConsoleColors.RESET);
        List<String[]> rows = new ArrayList<>();
        for (Course c : courses) {
            rows.add(new String[]{
                String.valueOf(c.getCourseId()), c.getCourseCode(),
                c.getCourseName(), String.valueOf(c.getCredits())
            });
        }
        TablePrinter.print(new String[]{"ID", "Code", "Name", "Credits"}, rows);
    }

    private void printStudentTable(List<Student> students) {
        List<String[]> rows = new ArrayList<>();
        for (Student s : students) {
            rows.add(new String[]{
                String.valueOf(s.getStudentId()), s.getRollNumber(),
                s.getFullName(), s.getEmail(),
                s.getDepartment(), String.valueOf(s.getSemester())
            });
        }
        TablePrinter.print(new String[]{"ID", "Roll No.", "Name", "Email", "Department", "Sem"}, rows);
    }
}
