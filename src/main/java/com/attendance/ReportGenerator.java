package com.attendance;

import java.util.List;

/**
 * Generates reports for student attendance.
 */
public class ReportGenerator {

    public static void generateMonthlyReport(List<Student> students) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("MONTHLY ATTENDANCE REPORT");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-10s %-20s %-10s %-10s %-10s %-10s%n", "ID", "Name", "Total", "Attended", "%", "Status");
        System.out.println("--------------------------------------------------------------------------------");

        for (Student student : students) {
            int total = student.getTotalClasses();
            int attended = student.getAttendedClasses();
            double percentage = AttendanceCalculator.calculatePercentage(attended, total);
            boolean shortage = AttendanceCalculator.isShortage(percentage);
            String status = shortage ? "Shortage" : "Ok";

            System.out.printf("%-10s %-20s %-10d %-10d %-10.2f %-10s%n",
                    student.getId(),
                    student.getName(),
                    total,
                    attended,
                    percentage,
                    status);
        }
        System.out.println("--------------------------------------------------------------------------------");
    }
}
