public class Student {
   
    private int rollNo;
    private String name;
    private int marks;

    Student(int rollNo, String Name, int marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.marks = marks;
    }

    public int getRollno() {
        return rollNo;
    }

    public String getName() {
        return name;
    }

    public int getMarks() {
        return marks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String toString() {
        return rollNo + " : " + name + " : " + marks;
    }
}
