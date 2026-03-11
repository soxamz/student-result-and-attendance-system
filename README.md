# Student Result & Attendance System

A **CLI-based Java application** for managing student records, course enrollments, exam results, and attendance — backed by **PostgreSQL**.

---

## 📁 Project Structure

```
StudentSystem/
├── src/main/java/com/student/
│   ├── Main.java                     ← Entry point
│   ├── model/
│   │   ├── Student.java
│   │   ├── Course.java
│   │   ├── Result.java
│   │   └── Attendance.java
│   ├── dao/                          ← Database access layer
│   │   ├── StudentDAO.java
│   │   ├── CourseDAO.java
│   │   ├── ResultDAO.java
│   │   └── AttendanceDAO.java
│   ├── service/                      ← Business logic layer
│   │   ├── StudentService.java
│   │   ├── CourseService.java
│   │   ├── ResultService.java
│   │   └── AttendanceService.java
│   ├── cli/                          ← CLI menus
│   │   ├── StudentMenu.java
│   │   ├── CourseMenu.java
│   │   ├── ResultMenu.java
│   │   └── AttendanceMenu.java
│   └── util/
│       ├── DatabaseConnection.java   ← Singleton DB connection
│       ├── ConsoleColors.java        ← ANSI colors
│       ├── TablePrinter.java         ← ASCII table renderer
│       └── InputValidator.java       ← Safe CLI input
├── lib/
│   └── postgresql.jar                ← JDBC driver (download separately)
├── schema.sql                        ← Run in pgAdmin to set up DB
├── .env                              ← Database credentials
├── build.sh / build.bat              ← Build scripts
└── run.sh  / run.bat                 ← Run scripts
```

---

## ⚙️ OOP Concepts Used

| Concept | Where Used |
|---|---|
| **Encapsulation** | All model classes (private fields + getters/setters) |
| **Abstraction** | Service layer hides DAO complexity from CLI |
| **Separation of Concerns** | Model → DAO → Service → CLI layers |
| **Singleton Pattern** | `DatabaseConnection` — one connection instance |
| **Enum** | `Attendance.Status` (PRESENT, ABSENT, LATE) |
| **Optional** | Null-safe returns in DAO `findBy*` methods |
| **Switch Expressions** | Java 14+ switch in menu routing |
| **Prepared Statements** | SQL injection prevention throughout |

---

## 🚀 Setup Instructions

### Step 1: PostgreSQL Database

1. Open **pgAdmin** and connect to your local PostgreSQL server.
2. Open the **Query Tool** and run `schema.sql`:
   ```sql
   -- In pgAdmin Query Tool:
   -- File > Open > schema.sql  → Execute (F5)
   ```
   This creates the `student_system` database with all tables and views.

### Step 2: Configure `.env`

Edit the `.env` file in the project root:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=student_system
DB_USER=postgres
DB_PASSWORD=your_actual_password
```

### Step 3: Download PostgreSQL JDBC Driver

Download `postgresql-42.7.3.jar` from:
> https://jdbc.postgresql.org/download/postgresql-42.7.3.jar

Place it in the `lib/` folder:
```
StudentSystem/lib/postgresql.jar
```

### Step 4: Build & Run

**Linux/Mac:**
```bash
chmod +x build.sh run.sh
./build.sh
./run.sh
```

**Windows:**
```cmd
build.bat
run.bat
```

**Manual (any OS):**
```bash
# Compile
find src/main/java -name "*.java" > sources.txt
javac -cp "lib/postgresql.jar" -d build/classes @sources.txt

# Run
java -cp "build/classes:lib/postgresql.jar" com.student.Main
```

---

## 📋 Features

### 👨‍🎓 Student Management
- Add / Update / Delete students
- Search by roll number or department
- View all enrolled students

### 📚 Course Management
- Add / Update / Delete courses
- View enrolled students per course

### 📊 Enrollment
- Enroll students in courses
- View a student's course list

### 📝 Result Management
- Add results: MIDTERM, FINAL, QUIZ, ASSIGNMENT, LAB
- Auto-calculated grade (A+, A, B, C, D, F)
- View results by student or course
- Grade summary report per student

### 📅 Attendance Management
- Mark individual attendance (PRESENT / ABSENT / LATE)
- **Bulk attendance** — mark entire class at once
- View attendance by date and course
- **Summary report** with percentage per course
- ⚠️ Automatic warning for attendance below **75%**

---

## 🗄️ Database Schema

```
students ──── enrollments ──── courses
    │                              │
    └── results ──────────────────┘
    └── attendance ───────────────┘
```

**Views:**
- `student_result_summary` — grades with percentage
- `attendance_summary` — attendance % per student per course

---

## 📌 Requirements

- Java 14+ (for switch expressions)
- PostgreSQL 12+
- pgAdmin (for running schema.sql)
- PostgreSQL JDBC Driver 42.x
