package com.attendance;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.util.Optional;
import java.util.List; // Added import
import java.util.stream.Collectors; // Added import
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class MainApp {
    public static void main(String[] args) {
        AttendanceManager manager = new AttendanceManager();

        // Indian Names Dataset
        String[] firstNames = {
                "Aarav", "Vivaan", "Aditya", "Vihaan", "Arjun", "Sai", "Reyansh", "Ayan", "Krishna", "Ishaan",
                "Diya", "Saanvi", "Ananya", "Aadhya", "Kiara", "Pari", "Riya", "Myra", "Sarah", "Sia"
        };
        String[] lastNames = {
                "Sharma", "Verma", "Gupta", "Malhotra", "Singh", "Kumar", "Mehta", "Patel", "Reddy", "Nair"
        };

        // Initialize Classes 1 to 10
        int studentIdCounter = 1;
        java.util.Random rand = new java.util.Random();
        java.time.LocalDate today = java.time.LocalDate.now();
        String[] subjects = { "Math", "English", "Hindi", "Science", "Social Science" };

        for (int std = 1; std <= 10; std++) {
            for (String section : new String[] { "A", "B" }) {
                // Generate 15 students per section
                for (int roll = 1; roll <= 15; roll++) {
                    String fName = firstNames[rand.nextInt(firstNames.length)];
                    String lName = lastNames[rand.nextInt(lastNames.length)];
                    String fullName = fName + " " + lName;
                    String id = String.format("S%04d", studentIdCounter++);

                    Student s = new Student(id, fullName, roll, std, section);

                    // Generate 30 Days History
                    for (int d = 30; d >= 1; d--) {
                        java.time.LocalDate pastDate = today.minusDays(d);
                        if (pastDate.getDayOfWeek().getValue() >= 6)
                            continue; // Skip weekends

                        // Mark attendance for random subjects (Simulating full day)
                        boolean isPresentDay = rand.nextDouble() > 0.15; // 85% attendance rate
                        if (isPresentDay) {
                            for (String subj : subjects) {
                                s.markAttendance(pastDate, subj, true);
                            }
                        } else if (rand.nextDouble() > 0.8) {
                            // Some valid absence
                            for (String subj : subjects) {
                                s.markAttendance(pastDate, subj, false);
                            }
                        }
                    }

                    manager.addStudent(s);
                }
            }
        }

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            }));
        }).start(7000);

        // API: Get all students (Filtered)
        app.get("/api/report", ctx -> {
            // Basic filtering support
            String stdParam = ctx.queryParam("std");
            String sectionParam = ctx.queryParam("section");

            List<Student> allStudents = manager.getStudents();

            if (stdParam != null && sectionParam != null) {
                int std = Integer.parseInt(stdParam);
                List<Student> filtered = allStudents.stream()
                        .filter(s -> s.getStd() == std && s.getSection().equalsIgnoreCase(sectionParam))
                        .collect(Collectors.toList());
                ctx.json(filtered);
            } else {
                ctx.json(allStudents); // Return all for dashboard stats
            }
        });

        // API: Mark Attendance
        app.post("/api/attendance", ctx -> {
            String studentId = ctx.queryParam("studentId");
            String dateStr = ctx.queryParam("date");
            String className = Optional.ofNullable(ctx.queryParam("class")).orElse("Math"); // Subject
            Boolean isPresent = Optional.ofNullable(ctx.queryParam("present")).map(Boolean::parseBoolean).orElse(false);

            if (studentId == null || dateStr == null) {
                ctx.status(400).result("Missing studentId or date");
                return;
            }

            try {
                java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
                Optional<Student> student = manager.findStudentById(studentId);

                if (student.isPresent()) {
                    student.get().markAttendance(date, className, isPresent);
                    ctx.status(200).result("Success");
                } else {
                    ctx.status(404).result("Student not found");
                }
            } catch (java.time.format.DateTimeParseException e) {
                ctx.status(400).result("Invalid date format. Use YYYY-MM-DD");
            }
        });

        System.out.println("Web Server started at http://localhost:7000");
    }
}
