package com.attendance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class AttendanceManagerTest {
    private AttendanceManager manager;

    @BeforeEach
    public void setUp() {
        manager = new AttendanceManager();
    }

    @Test
    public void testAddAndFindStudent() {
        Student s1 = new Student("S1", "John", 1, 1, "A");
        manager.addStudent(s1);

        Optional<Student> found = manager.findStudentById("S1");
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }

    @Test
    public void testMarkAttendance() {
        Student s1 = new Student("S1", "John", 1, 1, "A");
        manager.addStudent(s1);

        manager.markAttendance("S1", java.time.LocalDate.now(), "Math", true);
        assertEquals(1, s1.getTotalClasses());
        assertTrue(s1.getRecords().get(0).isPresent());
    }

    @Test
    public void testMarkAttendanceStudentNotFound() {
        manager.markAttendance("S999", java.time.LocalDate.now(), "Math", true);
        // Should catch exception or print error (in our case it prints error, we just
        // ensure no crash)
        assertTrue(true);
    }
}
