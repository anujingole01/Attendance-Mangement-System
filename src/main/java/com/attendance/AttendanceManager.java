package com.attendance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages the list of students and attendance operations.
 */
public class AttendanceManager {
    private List<Student> students;

    public AttendanceManager() {
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public Optional<Student> findStudentById(String id) {
        return students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public void markAttendance(String studentId, java.time.LocalDate date, String className, boolean isPresent) {
        findStudentById(studentId).ifPresentOrElse(
                student -> student.markAttendance(date, className, isPresent),
                () -> System.out.println("Student not found: " + studentId));
    }
}
