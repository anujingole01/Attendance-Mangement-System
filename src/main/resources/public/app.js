document.addEventListener('DOMContentLoaded', () => {
    // Init values
    document.getElementById('attendance-date').valueAsDate = new Date();
    document.getElementById('current-date').textContent = new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

    // Initial Load
    loadDashboard();

    // Sidebar Navigation
    const navItems = document.querySelectorAll('.nav-links li');
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            // Active Class
            navItems.forEach(n => n.classList.remove('active'));
            item.classList.add('active');

            // View Switching
            const target = item.dataset.target;
            document.querySelectorAll('.content-section').forEach(sec => sec.classList.remove('active'));
            document.getElementById(target).classList.add('active');

            // Layout Title
            document.getElementById('page-title').textContent = item.querySelector('span').textContent;

            // Data Refresh
            if (target === 'dashboard') loadDashboard();
            if (target === 'reports') loadReport();
        });
    });

    // Mark Attendance Load 
    document.getElementById('load-students-btn').addEventListener('click', loadMarkingList);

    // Toggle logic for delegated events
    document.getElementById('student-list').addEventListener('click', (e) => {
        const toggle = e.target.closest('.toggle-switch');
        if (toggle) {
            toggle.classList.toggle('active');
            const card = toggle.closest('.student-card');
            if (toggle.classList.contains('active')) {
                card.classList.remove('absent');
                card.classList.add('present');
            } else {
                card.classList.remove('present');
                card.classList.add('absent');
            }
        }
    });

    // Submit Attendance
    document.getElementById('submit-attendance').addEventListener('click', submitAttendance);

    // Refresh Report
    document.getElementById('refresh-report').addEventListener('click', loadReport);
});

async function loadDashboard() {
    try {
        const res = await fetch('/api/report');
        const students = await res.json();

        // Calculate Avg Attendance
        let totalPct = 0;
        students.forEach(s => totalPct += s.attendancePercentage);
        const avg = students.length ? Math.round(totalPct / students.length) : 0;

        // ANIMATE NUMBER
        animateValue(document.getElementById('avg-attendance'), 0, avg, 1000);

        // Calculate Real Daily Stats (Last 7 Days)
        const dailyStats = [];
        const days = [];

        for (let i = 6; i >= 0; i--) {
            const d = new Date();
            d.setDate(d.getDate() - i);
            const dateStr = d.toISOString().split('T')[0];
            const dayName = d.toLocaleDateString('en-US', { weekday: 'short' });

            // Count present for this date across all students
            let presentCount = 0;
            let totalRecords = 0;

            students.forEach(s => {
                // Find record for this date
                // Note: In a real app, backend should aggregat, but we can do it here for 300 students easily
                // The student.records is a list, we need to find matches
                if (s.records) {
                    const record = s.records.find(r => r.date === dateStr);
                    if (record) {
                        totalRecords++;
                        if (record.present) presentCount++;
                    }
                }
            });

            const pct = totalRecords > 0 ? Math.round((presentCount / totalRecords) * 100) : 0;
            dailyStats.push(pct);
            days.push(dayName);
        }

        renderChart(dailyStats, days);
    } catch (e) { console.error(e); }
}

function renderChart(data, labels) {
    const chart = document.getElementById('yearly-chart');
    chart.innerHTML = '';

    data.forEach((val, index) => {
        const bar = document.createElement('div');
        bar.className = 'chart-bar';
        bar.style.height = `${val}%`;
        bar.title = `${val}%`; // Tooltip

        // Color coding
        if (val < 50) bar.style.background = 'linear-gradient(to top, #ff9a9e, #fecfef)';
        else if (val < 80) bar.style.background = 'linear-gradient(to top, #f6d365, #fda085)';

        bar.innerHTML = `<span class="bar-label">${labels[index]}</span>`;
        chart.appendChild(bar);
    });
}

async function loadMarkingList() {
    try {
        const std = document.getElementById('std-select').value;
        const section = document.getElementById('section-select').value;

        const res = await fetch(`/api/report?std=${std}&section=${section}`);
        const students = await res.json();

        // Sorting by Roll No
        students.sort((a, b) => a.rollNo - b.rollNo);

        const container = document.getElementById('student-list');
        container.innerHTML = '';

        if (students.length === 0) {
            container.innerHTML = '<p style="text-align:center;width:100%;color:#888;">No students found for this class.</p>';
            return;
        }

        students.forEach(s => {
            const card = document.createElement('div');
            card.className = 'student-card present'; // Default Green

            // Random avatar color
            const colors = ['#f1c40f', '#e67e22', '#e74c3c', '#9b59b6', '#3498db', '#1abc9c'];
            const color = colors[s.name.length % colors.length];

            card.innerHTML = `
                <div style="display:flex;align-items:center;gap:10px;">
                    <div style="width:35px;height:35px;border-radius:50%;background:${color};color:white;display:flex;justify-content:center;align-items:center;font-weight:bold;font-size:0.8rem;">
                        ${s.rollNo}
                    </div>
                    <div>
                        <div class="student-name">${s.name}</div>
                        <div class="student-id">Roll No: ${s.rollNo}</div>
                    </div>
                </div>
                <div class="toggle-switch active" data-id="${s.id}">
                    <div class="toggle-thumb"></div>
                </div>
            `;
            container.appendChild(card);
        });
    } catch (e) { console.error(e); }
}

async function submitAttendance() {
    const date = document.getElementById('attendance-date').value;
    const std = document.getElementById('std-select').value;
    const section = document.getElementById('section-select').value;
    const subject = document.getElementById('class-select').value;
    const toggles = document.querySelectorAll('.toggle-switch');

    if (toggles.length === 0) {
        showToast("No students loaded to save!", true);
        return;
    }

    // Show saving...
    const submitBtn = document.getElementById('submit-attendance');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Saving...';
    submitBtn.disabled = true;

    const promises = [];
    toggles.forEach(t => {
        const id = t.dataset.id;
        const present = t.classList.contains('active');
        promises.push(fetch(`/api/attendance?studentId=${id}&date=${date}&class=${subject}&present=${present}`, { method: 'POST' }));
    });

    await Promise.all(promises);

    // Reset Button & Show Toast
    submitBtn.innerHTML = originalText;
    submitBtn.disabled = false;
    showToast("Attendance Saved Successfully!");

    // Trigger Confetti
    triggerConfetti();
}

function triggerConfetti() {
    var duration = 3 * 1000;
    var animationEnd = Date.now() + duration;
    var defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 9999 };

    function randomInOut(min, max) {
        return Math.random() * (max - min) + min;
    }

    var interval = setInterval(function () {
        var timeLeft = animationEnd - Date.now();

        if (timeLeft <= 0) {
            return clearInterval(interval);
        }

        var particleCount = 50 * (timeLeft / duration);
        // since particles fall down, start a bit higher than random
        confetti(Object.assign({}, defaults, { particleCount, origin: { x: randomInOut(0.1, 0.3), y: Math.random() - 0.2 } }));
        confetti(Object.assign({}, defaults, { particleCount, origin: { x: randomInOut(0.7, 0.9), y: Math.random() - 0.2 } }));
    }, 250);
}

async function loadReport() {
    const res = await fetch('/api/report');
    const students = await res.json();
    const tbody = document.getElementById('report-body');
    tbody.innerHTML = '';

    students.forEach(s => {
        const tr = document.createElement('tr');
        const statusClass = s.attendancePercentage < 75 ? 'shortage' : 'ok';
        const statusText = s.attendancePercentage < 75 ? 'Low Attendance' : 'Good';

        tr.innerHTML = `
            <td>${s.id}</td>
            <td><span style="font-weight:600">${s.name}</span></td>
            <td>${s.totalClasses}</td>
            <td>${s.attendedClasses}</td>
            <td>${s.attendancePercentage.toFixed(1)}%</td>
            <td><span class="status-badge ${statusClass}">${statusText}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

function showToast(msg, isError = false) {
    const toast = document.getElementById('toast');
    toast.textContent = msg;
    toast.style.background = isError ? '#e74c3c' : '#2d3436';
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3000);
}

function animateValue(obj, start, end, duration) {
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        obj.innerHTML = Math.floor(progress * (end - start) + start) + "%";
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}
