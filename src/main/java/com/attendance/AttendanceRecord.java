package com.attendance;

import java.time.LocalDate;

public class AttendanceRecord {
    private LocalDate date;
    private String className;
    private boolean isPresent;

    public AttendanceRecord(LocalDate date, String className, boolean isPresent) {
        this.date = date;
        this.className = className;
        this.isPresent = isPresent;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getClassName() {
        return className;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
