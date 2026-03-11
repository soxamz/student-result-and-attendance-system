package com.student.util;

import java.util.List;

/**
 * Utility to print formatted ASCII tables in the CLI.
 */
public class TablePrinter {

    public static void print(String[] headers, List<String[]> rows) {
        int[] colWidths = new int[headers.length];

        // Calculate max width for each column
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length && i < headers.length; i++) {
                String cell = row[i] == null ? "N/A" : row[i];
                colWidths[i] = Math.max(colWidths[i], cell.length());
            }
        }

        String separator = buildSeparator(colWidths);

        System.out.println(ConsoleColors.CYAN + separator + ConsoleColors.RESET);
        System.out.print(ConsoleColors.BOLD_WHITE + "| ");
        for (int i = 0; i < headers.length; i++) {
            System.out.printf("%-" + colWidths[i] + "s | ", headers[i]);
        }
        System.out.println(ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + separator + ConsoleColors.RESET);

        if (rows.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "| No records found." + ConsoleColors.RESET);
        } else {
            for (String[] row : rows) {
                System.out.print("| ");
                for (int i = 0; i < headers.length; i++) {
                    String cell = (i < row.length && row[i] != null) ? row[i] : "N/A";
                    System.out.printf("%-" + colWidths[i] + "s | ", cell);
                }
                System.out.println();
            }
        }

        System.out.println(ConsoleColors.CYAN + separator + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.GREEN + "  Total records: %d%n" + ConsoleColors.RESET, rows.size());
    }

    private static String buildSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        return sb.toString();
    }
}
