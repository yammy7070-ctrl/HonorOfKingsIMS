package util;

public class ConsoleUtil {
    public static void printDivider() {
        System.out.println("========================================");
    }
    public static void printTitle(String title) {
        printDivider();
        System.out.println(title);
        printDivider();
    }
    public static String formatPercent(double rate) {
        return String.format("%.0f%%", rate * 100);
    }
}