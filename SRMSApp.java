import java.util.*;
import java.util.regex.Pattern;


// ========= Domain Model =========
final class Student {
    private final int id;               // immutable primary key
    private String name;
    private int age;
    private String email;
    private String course;
    private double gpa;

    public Student(int id, String name, int age, String email, String course, double gpa) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.course = course;
        this.gpa = gpa;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public String getCourse() { return course; }
    public double getGpa() { return gpa; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setEmail(String email) { this.email = email; }
    public void setCourse(String course) { this.course = course; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    @Override
    public String toString() {
        return String.format("#%d | %-20s | Age: %-2d | GPA: %.2f | %-18s | %s",
                id, name, age, gpa, course, email);
    }
}

// ========= Abstraction (Service Contract) =========
interface StudentService {
    boolean addStudent(Student s);
    Student getStudent(int id);
    List<Student> getAllStudents();
    boolean updateStudent(int id, Student updated);
    boolean deleteStudent(int id);
    List<Student> searchByName(String query);
}

// ========= Concrete Implementation (In-memory) =========
class InMemoryStudentService implements StudentService {
    private final LinkedHashMap<Integer, Student> store = new LinkedHashMap<>();

    @Override
    public boolean addStudent(Student s) {
        if (store.containsKey(s.getId())) return false;
        store.put(s.getId(), s);
        return true;
    }

    @Override
    public Student getStudent(int id) {
        return store.get(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean updateStudent(int id, Student updated) {
        if (!store.containsKey(id)) return false;
        Student current = store.get(id);
        // keep primary key stable; copy fields
        current.setName(updated.getName());
        current.setAge(updated.getAge());
        current.setEmail(updated.getEmail());
        current.setCourse(updated.getCourse());
        current.setGpa(updated.getGpa());
        return true;
    }

    @Override
    public boolean deleteStudent(int id) {
        return store.remove(id) != null;
    }

    @Override
    public List<Student> searchByName(String query) {
        String q = query.toLowerCase(Locale.ROOT).trim();
        List<Student> out = new ArrayList<>();
        for (Student s : store.values()) {
            if (s.getName().toLowerCase(Locale.ROOT).contains(q)) out.add(s);
        }
        return out;
    }
}

// ========= Utilities =========
final class ValidationUtils {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private ValidationUtils() {}

    public static int requirePositiveInt(String label, String raw) {
        int v;
        try { v = Integer.parseInt(raw.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(label + " must be an integer."); }
        if (v <= 0) throw new IllegalArgumentException(label + " must be > 0.");
        return v;
    }

    public static double requireGpa(String raw) {
        double g;
        try { g = Double.parseDouble(raw.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("GPA must be a number."); }
        if (g < 0.0 || g > 10.0) throw new IllegalArgumentException("GPA must be between 0.0 and 10.0.");
        return g;
    }

    public static String requireName(String raw) {
        String s = raw.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
        return s;
    }

    public static String requireCourse(String raw) {
        String s = raw.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Course cannot be empty.");
        return s;
    }

    public static String requireEmail(String raw) {
        String s = raw.trim();
        if (!EMAIL.matcher(s).matches()) throw new IllegalArgumentException("Invalid email format.");
        return s;
    }
}

// ========= CLI Layer =========
class SRMSCli {
    private final Scanner in;
    private final StudentService service;

    SRMSCli(Scanner in, StudentService service) {
        this.in = in;
        this.service = service;
    }

    public void run() {
        seedDemoData();
        while (true) {
            printMenu();
            System.out.print("Choose an option: ");
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> create();
                    case "2" -> listAll();
                    case "3" -> viewById();
                    case "4" -> update();
                    case "5" -> delete();
                    case "6" -> search();
                    case "7" -> sortAndList();
                    case "0" -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid choice. Try again.\n");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("[Input Error] " + e.getMessage() + "\n");
            } catch (Exception e) {
                System.out.println("[Error] " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n==== Student Record Management ====");
        System.out.println("1) Create student");
        System.out.println("2) List all students");
        System.out.println("3) View student by ID");
        System.out.println("4) Update student");
        System.out.println("5) Delete student");
        System.out.println("6) Search students by name");
        System.out.println("7) Sort & list (by Name/GPA)");
        System.out.println("0) Exit");
    }

    private void create() {
        System.out.println("\n-- Create Student --");
        int id = readPositiveInt("ID");
        if (service.getStudent(id) != null) {
            throw new IllegalArgumentException("Student with ID " + id + " already exists.");
        }
        String name = readName();
        int age = readPositiveInt("Age");
        String email = readEmail();
        String course = readCourse();
        double gpa = readGpa();

        boolean ok = service.addStudent(new Student(id, name, age, email, course, gpa));
        System.out.println(ok ? "Created successfully." : "Create failed.");
    }

    private void listAll() {
        System.out.println("\n-- All Students --");
        List<Student> all = service.getAllStudents();
        if (all.isEmpty()) { System.out.println("No records found."); return; }
        all.forEach(System.out::println);
    }

    private void viewById() {
        System.out.println("\n-- View Student --");
        int id = readPositiveInt("ID");
        Student s = service.getStudent(id);
        if (s == null) System.out.println("Not found.");
        else System.out.println(s);
    }

    private void update() {
        System.out.println("\n-- Update Student --");
        int id = readPositiveInt("ID to update");
        Student existing = service.getStudent(id);
        if (existing == null) { System.out.println("Not found."); return; }

        System.out.println("(Leave input blank to keep existing value)");
        String name = prompt("Name [" + existing.getName() + "]: ");
        String ageStr = prompt("Age [" + existing.getAge() + "]: ");
        String email = prompt("Email [" + existing.getEmail() + "]: ");
        String course = prompt("Course [" + existing.getCourse() + "]: ");
        String gpaStr = prompt("GPA [" + existing.getGpa() + "]: ");

        String newName = name.isBlank() ? existing.getName() : ValidationUtils.requireName(name);
        int newAge = ageStr.isBlank() ? existing.getAge() : ValidationUtils.requirePositiveInt("Age", ageStr);
        String newEmail = email.isBlank() ? existing.getEmail() : ValidationUtils.requireEmail(email);
        String newCourse = course.isBlank() ? existing.getCourse() : ValidationUtils.requireCourse(course);
        double newGpa = gpaStr.isBlank() ? existing.getGpa() : ValidationUtils.requireGpa(gpaStr);

        boolean ok = service.updateStudent(id, new Student(id, newName, newAge, newEmail, newCourse, newGpa));
        System.out.println(ok ? "Updated successfully." : "Update failed.");
    }

    private void delete() {
        System.out.println("\n-- Delete Student --");
        int id = readPositiveInt("ID to delete");
        boolean ok = service.deleteStudent(id);
        System.out.println(ok ? "Deleted successfully." : "Not found.");
    }

    private void search() {
        System.out.println("\n-- Search by Name --");
        String q = prompt("Query: ");
        List<Student> results = service.searchByName(q);
        if (results.isEmpty()) System.out.println("No matches.");
        else results.forEach(System.out::println);
    }

    private void sortAndList() {
        System.out.println("\n-- Sort & List --");
        System.out.println("1) By Name (A→Z)\n2) By GPA (High→Low)");
        String c = prompt("Choose: ");
        List<Student> all = service.getAllStudents();
        if (all.isEmpty()) { System.out.println("No records."); return; }
        switch (c) {
            case "1" -> all.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));
            case "2" -> all.sort(Comparator.comparingDouble(Student::getGpa).reversed());
            default -> { System.out.println("Invalid choice."); return; }
        }
        all.forEach(System.out::println);
    }

    // ---- input helpers ----
    private int readPositiveInt(String label) {
        String raw = prompt(label + ": ");
        return ValidationUtils.requirePositiveInt(label, raw);
    }

    private String readName() { return ValidationUtils.requireName(prompt("Name: ")); }
    private String readCourse() { return ValidationUtils.requireCourse(prompt("Course: ")); }
    private String readEmail() { return ValidationUtils.requireEmail(prompt("Email: ")); }
    private double readGpa() { return ValidationUtils.requireGpa(prompt("GPA (0-10): ")); }

    private String prompt(String msg) {
        System.out.print(msg);
        return in.nextLine();
    }

    private void seedDemoData() {
        // Initial demo data so the app doesn't feel empty on first run
        service.addStudent(new Student(101, "Aisha Verma", 20, "aisha@uni.edu", "CSE", 8.7));
        service.addStudent(new Student(102, "Rohan Patel", 21, "rohan@uni.edu", "ECE", 7.9));
        service.addStudent(new Student(103, "Neha Singh", 19, "neha@uni.edu", "ME", 9.1));
    }
}

// ========= Application Bootstrap =========
public class SRMSApp {
    public static void main(String[] args) {
        StudentService service = new InMemoryStudentService();
        try (Scanner scanner = new Scanner(System.in)) {
            new SRMSCli(scanner, service).run();
        }
    }
}
