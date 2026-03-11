package com.student.util;

import java.util.Scanner;

/**
 * Utility for validated CLI input.
 */
public class InputValidator {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readNonEmpty(String prompt) {
        String input;
        do {
            System.out.print(ConsoleColors.BOLD_YELLOW + prompt + ConsoleColors.RESET);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(ConsoleColors.RED + "  Input cannot be empty. Try again." + ConsoleColors.RESET);
            }
        } while (input.isEmpty());
        return input;
    }

    public static String readOptional(String prompt) {
        System.out.print(ConsoleColors.BOLD_YELLOW + prompt + ConsoleColors.RESET);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(ConsoleColors.BOLD_YELLOW + prompt + ConsoleColors.RESET);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "  Invalid number. Try again." + ConsoleColors.RESET);
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        int value;
        do {
            value = readInt(prompt);
            if (value < min || value > max) {
                System.out.printf(ConsoleColors.RED + "  Please enter a number between %d and %d.%n" + ConsoleColors.RESET, min, max);
            }
        } while (value < min || value > max);
        return value;
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(ConsoleColors.BOLD_YELLOW + prompt + ConsoleColors.RESET);
            String line = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(line);
                if (val < min || val > max) {
                    System.out.printf(ConsoleColors.RED + "  Value must be between %.1f and %.1f.%n" + ConsoleColors.RESET, min, max);
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "  Invalid decimal number. Try again." + ConsoleColors.RESET);
            }
        }
    }

    public static boolean confirm(String prompt) {
        System.out.print(ConsoleColors.BOLD_YELLOW + prompt + " [y/N]: " + ConsoleColors.RESET);
        String line = scanner.nextLine().trim().toLowerCase();
        return line.equals("y") || line.equals("yes");
    }

    public static Scanner getScanner() {
        return scanner;
    }
}
