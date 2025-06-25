import java.util.*;

public class studentDatabase {
    Map<Integer, Student> db = new LinkedHashMap<>();

    void addStudent(int rollNo, String name, int marks) {
        if (db.containsKey(rollNo)) {
            System.out.println("Roll number already exists");
            return;
        }
        Student s = new Student(rollNo, name, marks);
        db.put(rollNo, s);
        System.out.println("Student added");
    }

    void viewAll() {
        if (db.isEmpty()) {
            System.out.println("No students found");
            return;
        } else {
            for (Student s : db.values()) {
                System.out.println(s);
            }
        }
    }

    void searchStudent(int rollNo) {
        if (db.containsKey(rollNo)) {
            Student s = db.get(rollNo);
            System.out.println(s);
        } else {
            System.out.println("Student not found");
        }
    }

    void updateStudent(int rollNo, String newName, int newMarks){
        if(db.containsKey(rollNo)){
            Student s = db.get(rollNo);
            s.setName(newName);
            s.setMarks(newMarks);
            System.out.println("Student Updated");
        }
        else{
            System.out.println("Student not found , can't update");
        }
    }

    void deleteStudent(int rollNo){
        if(db.containsKey(rollNo)){
            db.remove(rollNo);
            System.out.println("Student removed Successfully");
        }
        else{
            System.out.println("Student not found, can't delete");
        }
    }
}