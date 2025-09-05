# Student Record Management System (Java)

A simple **console-based Student Record Management System** built with **Java, OOP, and Collections**.  
This project demonstrates object-oriented design principles, modular architecture, and efficient in-memory operations using `LinkedHashMap`.

---

## 🚀 Features
- **CRUD Operations**  
  Create, Read, Update, and Delete student records from the system.
- **OOP Design**  
  Encapsulation (`Student` class), Abstraction (`StudentService` interface), and modular services.
- **Collections**  
  Uses `LinkedHashMap` for efficient in-memory storage and predictable iteration order.
- **CLI Menu**  
  Interactive menu powered by `Scanner`.
- **Search & Sort**  
  - Search students by name.  
  - Sort students by **Name (A→Z)** or **GPA (High→Low)**.
- **Validation**  
  Input validation for IDs, GPA range, emails, etc.
- **Demo Data**  
  System starts with sample student records for convenience.

---

## 📂 Project Structure
Since everything is in one file, all classes are defined in **`SRMSApp.java`**:

- `public class SRMSApp` → Main entry point.  
- `class SRMSCli` → Command-line interface (menu, input, interactions).  
- `class Student` → Domain model with getters/setters.  
- `interface StudentService` → Abstraction for student operations.  
- `class InMemoryStudentService` → Concrete implementation using `LinkedHashMap`.  
- `class ValidationUtils` → Input validation helpers.

---

## 🛠️ Requirements
- Java 17+ (recommended, works with Java 8+)

---

## ▶️ How to Run
1. Clone or download the project.
2. Open a terminal in the project folder.
3. Compile the program:
   ```bash
   javac SRMSApp.java
4. Run the program:
   ```bash
   java SRMSApp