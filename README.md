# Attendance Management System (Web Edition)

A **Web-Based** application for managing student attendance. Built with **Java**, **Maven**, and **Javalin**.

## Features
- **Web Dashboard**: Modern UI for marking attendance and viewing reports.
- **REST API**: Backend endpoints for data management.
- **DevOps Integration**: CI/CD with GitHub Actions.

## How to Run

1. **Build:**
   ```bash
   mvn clean package
   ```

2. **Run:**
   - **Windows**: Double-click `run.bat`
   - **Terminal**: `java -jar target/attendance-management-system-1.0-SNAPSHOT.jar`

3. **Open:**
   Go to **[http://localhost:7000](http://localhost:7000)** in your browser.

## API Endpoints
- `GET /api/report`: Get all student data.
- `POST /api/attendance`: Mark attendance (`?studentId=...&day=...&present=...`).

## Tech Stack
- **Backend**: Java, Javalin (Jetty)
- **Frontend**: HTML5, CSS3, JavaScript
- **Build**: Maven
- **CI**: GitHub Actions
