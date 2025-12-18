package com.attendance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AttendanceCalculatorTest {

    @Test
    public void testCalculatePercentageNormal() {
        assertEquals(50.0, AttendanceCalculator.calculatePercentage(5, 10), 0.01);
        assertEquals(100.0, AttendanceCalculator.calculatePercentage(10, 10), 0.01);
        assertEquals(0.0, AttendanceCalculator.calculatePercentage(0, 10), 0.01);
    }

    @Test
    public void testCalculatePercentageZeroTotal() {
        assertEquals(0.0, AttendanceCalculator.calculatePercentage(5, 0), 0.01);
    }

    @Test
    public void testIsShortage() {
        assertTrue(AttendanceCalculator.isShortage(74.9));
        assertFalse(AttendanceCalculator.isShortage(75.0));
        assertFalse(AttendanceCalculator.isShortage(80.0));
    }
}
