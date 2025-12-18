package com.attendance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a student and their attendance record.
 */
public class Student {
    private String id;
    private String name;
    private int rollNo;
    private int std; // Class 1-10
    private String section; // A or B
    private List<AttendanceRecord> records;

    public Student(String id, String name, int rollNo, int std, String section) {
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
        this.std = std;
        this.section = section;
        this.records = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRollNo() {
        return rollNo;
    }

    public int getStd() {
        return std;
    }

    public String getSection() {
        return section;
    }

    /**
     * Marks attendance for a specific date and class.
     * Updates existing record if found, otherwise adds new one.
     */
    public void markAttendance(LocalDate date, String className, boolean isPresent) {
        for (AttendanceRecord record : records) {
            if (record.getDate().equals(date) && record.getClassName().equals(className)) {
                record.setPresent(isPresent);
                return;
            }
        }
        records.add(new AttendanceRecord(date, className, isPresent));
    }

    public List<AttendanceRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public int getTotalClasses() {
        return records.size();
    }

    public int getAttendedClasses() {
        return (int) records.stream().filter(AttendanceRecord::isPresent).count();
    }

    public double getAttendancePercentage() {
        int total = getTotalClasses();
        return total == 0 ? 0.0 : ((double) getAttendedClasses() / total) * 100.0;
    }
}
