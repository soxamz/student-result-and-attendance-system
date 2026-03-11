package com.student;

import com.student.cli.AttendanceMenu;
import com.student.cli.CourseMenu;
import com.student.cli.ResultMenu;
import com.student.cli.StudentMenu;
import com.student.util.ConsoleColors;
import com.student.util.DatabaseConnection;
import com.student.util.InputValidator;

/**
 * Main entry point for the Student Result & Attendance System.
 * CLI-based, backed by PostgreSQL.
 */
public class Main {

    public static void main(String[] args) {
        printBanner();

        // Establish DB connection at startup
        try {
            DatabaseConnection.getInstance().getConnection();
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED +
                "[FATAL] Cannot connect to database: " + e.getMessage() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.YELLOW +
                "  ► Check your .env file and ensure PostgreSQL is running." + ConsoleColors.RESET);
            System.exit(1);
        }

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.getInstance().close();
            System.out.println(ConsoleColors.CYAN + "\nGoodbye!" + ConsoleColors.RESET);
        }));

        // Main menu loop
        StudentMenu    studentMenu    = new StudentMenu();
        CourseMenu     courseMenu     = new CourseMenu();
        ResultMenu     resultMenu     = new ResultMenu();
        AttendanceMenu attendanceMenu = new AttendanceMenu();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputValidator.readInt("  Enter choice: ", 0, 4);
            switch (choice) {
                case 1 -> studentMenu.show();
                case 2 -> courseMenu.show();
                case 3 -> resultMenu.show();
                case 4 -> attendanceMenu.show();
                case 0 -> {
                    System.out.println(ConsoleColors.GREEN + "\n  Exiting system..." + ConsoleColors.RESET);
                    running = false;
                }
            }
        }
    }

    private static void printBanner() {
        System.out.println(ConsoleColors.BOLD_CYAN);
        System.out.println("  ╔════════════════════════════════════════════════╗");
        System.out.println("  ║    STUDENT RESULT & ATTENDANCE SYSTEM v1.0     ║");
        System.out.println("  ║         Powered by Java + PostgreSQL           ║");
        System.out.println("  ╚════════════════════════════════════════════════╝");
        System.out.println(ConsoleColors.RESET);
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println(ConsoleColors.BOLD_WHITE + "  ══════════════  MAIN MENU  ══════════════" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BOLD_BLUE   + "  1. " + ConsoleColors.RESET + "Student Management");
        System.out.println(ConsoleColors.BOLD_CYAN + "  2. " + ConsoleColors.RESET + "Course Management");
        System.out.println(ConsoleColors.BOLD_GREEN  + "  3. " + ConsoleColors.RESET + "Result Management");
        System.out.println(ConsoleColors.BOLD_YELLOW + "  4. " + ConsoleColors.RESET + "Attendance Management");
        System.out.println(ConsoleColors.RED         + "  0. " + ConsoleColors.RESET + "Exit");
        System.out.println(ConsoleColors.BOLD_WHITE  + "  ══════════════════════════════════════════" + ConsoleColors.RESET);
    }
}
