package com.attendance;

/**
 * Utility class for calculating attendance statistics.
 */
public class AttendanceCalculator {

    /**
     * Calculates the attendance percentage.
     * @param attendedClasses Number of classes attended
     * @param totalClasses Total number of classes
     * @return Percentage (0.0 to 100.0)
     */
    public static double calculatePercentage(int attendedClasses, int totalClasses) {
        if (totalClasses == 0) {
            return 0.0;
        }
        return ((double) attendedClasses / totalClasses) * 100;
    }

    /**
     * Checks if a student has a shortage of attendance.
     * @param percentage Attendance percentage
     * @return true if percentage < 75.0, false otherwise
     */
    public static boolean isShortage(double percentage) {
        return percentage < 75.0;
    }
}
