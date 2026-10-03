import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CCRM {
    private static final Scanner sc = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();
    private static final CourseService courseService = new CourseService();
    private static final EnrollmentService enrollmentService = new EnrollmentService();

    public static void main(String[] args) {
        System.out.println("\n===============================================");
        System.out.println("       CAMPUS COURSE & RECORDS MANAGER");
        System.out.println("===============================================");
        System.out.println("Welcome to the CCRM Application!");

        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> studentService.listStudents();
                case 3 -> addCourse();
                case 4 -> courseService.listCourses();
                case 5 -> enrollStudent();
                case 6 -> unenrollStudent();
                case 7 -> enrollmentService.listEnrollments();
                case 8 -> System.out.println("\nThank you for using CCRM!\nApplication closed successfully.");
                default -> System.out.println("Invalid choice! Please select 1 to 8.");
            }
        } while (choice != 8);
    }

    private static void printMenu() {
        System.out.println("\n===============================================");
        System.out.println("                  CCRM MENU");
        System.out.println("===============================================");
        System.out.println("1. Add Student");
        System.out.println("2. List Students");
        System.out.println("3. Add Course");
        System.out.println("4. List Courses");
        System.out.println("5. Enroll Student in Course");
        System.out.println("6. Unenroll Student from Course");
        System.out.println("7. List Enrollments");
        System.out.println("8. Exit");
        System.out.println("===============================================");
    }

    private static void addStudent() {
        System.out.println("\n---------- ADD STUDENT ----------");
        int id = readInt("Enter Student ID: ");
        String regNo = readText("Enter Registration Number: ");
        String name = readText("Enter Student Name: ");
        String email = readText("Enter Email: ");

        if (regNo.isBlank() || name.isBlank() || email.isBlank()) {
            System.out.println("Registration number, name and email cannot be empty.");
            return;
        }
        studentService.addStudent(new Student(id, regNo, name, email));
    }

    private static void addCourse() {
        System.out.println("\n---------- ADD COURSE ----------");
        String code = readText("Enter Course Code: ");
        String title = readText("Enter Course Title: ");
        int credits = readInt("Enter Credits: ");
        String instructor = readText("Enter Instructor Name: ");
        String department = readText("Enter Department: ");
        String semesterText = readText("Enter Semester (SPRING/SUMMER/FALL): ");

        if (code.isBlank() || title.isBlank() || instructor.isBlank() || department.isBlank()) {
            System.out.println("Course details cannot be empty.");
            return;
        }
        if (credits <= 0) {
            System.out.println("Credits must be greater than zero.");
            return;
        }

        try {
            Semester semester = Semester.valueOf(semesterText.toUpperCase());
            courseService.addCourse(new Course(code, title, credits, instructor, department, semester));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid semester. Use SPRING, SUMMER or FALL.");
        }
    }

    private static void enrollStudent() {
        System.out.println("\n---------- ENROLL STUDENT ----------");
        String regNo = readText("Enter Student Registration Number: ");
        String courseCode = readText("Enter Course Code: ");

        Student student = studentService.findByRegNo(regNo);
        Course course = courseService.findByCode(courseCode);

        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        if (course == null) {
            System.out.println("Course not found!");
            return;
        }
        enrollmentService.enroll(student, course);
    }

    private static void unenrollStudent() {
        System.out.println("\n---------- UNENROLL STUDENT ----------");
        String regNo = readText("Enter Student Registration Number: ");
        String courseCode = readText("Enter Course Code: ");

        Student student = studentService.findByRegNo(regNo);
        Course course = courseService.findByCode(courseCode);

        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        if (course == null) {
            System.out.println("Course not found!");
            return;
        }
        enrollmentService.unenroll(student, course);
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}

class Student {
    private final int id;
    private final String regNo;
    private final String fullName;
    private final String email;
    private final LocalDate createdDate;
    private final List<Course> enrolledCourses = new ArrayList<>();

    public Student(int id, String regNo, String fullName, String email) {
        this.id = id;
        this.regNo = regNo;
        this.fullName = fullName;
        this.email = email;
        this.createdDate = LocalDate.now();
    }

    public String getRegNo() { return regNo; }
    public String getFullName() { return fullName; }

    public void enroll(Course course) {
        if (!enrolledCourses.contains(course)) enrolledCourses.add(course);
    }

    public void unenroll(Course course) { enrolledCourses.remove(course); }

    @Override
    public String toString() {
        return id + " | " + regNo + " | " + fullName + " | " + email +
               " | Active | Created: " + createdDate;
    }
}

class Course {
    private final String code;
    private final String title;
    private final int credits;
    private final String instructor;
    private final String department;
    private final Semester semester;

    public Course(String code, String title, int credits, String instructor, String department, Semester semester) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.instructor = instructor;
        this.department = department;
        this.semester = semester;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }

    @Override
    public String toString() {
        return code + " | " + title + " | " + credits + " Credits | Instructor: " +
               instructor + " | Department: " + department + " | Semester: " + semester;
    }
}

class Enrollment {
    private final Student student;
    private final Course course;

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }

    @Override
    public String toString() {
        return student.getRegNo() + " -> " + course.getCode() + " (" + course.getTitle() + ")";
    }
}

enum Semester { SPRING, SUMMER, FALL }

class StudentService {
    private final List<Student> students = new ArrayList<>();

    public boolean addStudent(Student student) {
        if (findByRegNo(student.getRegNo()) != null) {
            System.out.println("A student with this Registration Number already exists!");
            return false;
        }
        students.add(student);
        System.out.println("Student added successfully!");
        return true;
    }

    public void listStudents() {
        System.out.println("\n---------- STUDENT LIST ----------");
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            for (Student student : students) System.out.println(student);
        }
    }

    public Student findByRegNo(String regNo) {
        for (Student student : students) {
            if (student.getRegNo().equalsIgnoreCase(regNo)) return student;
        }
        return null;
    }
}

class CourseService {
    private final List<Course> courses = new ArrayList<>();

    public boolean addCourse(Course course) {
        if (findByCode(course.getCode()) != null) {
            System.out.println("A course with this code already exists!");
            return false;
        }
        courses.add(course);
        System.out.println("Course added successfully!");
        return true;
    }

    public void listCourses() {
        System.out.println("\n---------- COURSE LIST ----------");
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            for (Course course : courses) System.out.println(course);
        }
    }

    public Course findByCode(String code) {
        for (Course course : courses) {
            if (course.getCode().equalsIgnoreCase(code)) return course;
        }
        return null;
    }
}

class EnrollmentService {
    private final List<Enrollment> enrollments = new ArrayList<>();

    public void enroll(Student student, Course course) {
        if (isEnrolled(student, course)) {
            System.out.println("Student is already enrolled in this course.");
            return;
        }
        enrollments.add(new Enrollment(student, course));
        student.enroll(course);
        System.out.println("Student " + student.getFullName() +
                " successfully enrolled in " + course.getTitle() + "!");
    }

    public void unenroll(Student student, Course course) {
        boolean removed = enrollments.removeIf(e ->
                e.getStudent().equals(student) && e.getCourse().equals(course));

        if (removed) {
            student.unenroll(course);
            System.out.println("Student " + student.getFullName() +
                    " successfully unenrolled from " + course.getTitle() + "!");
        } else {
            System.out.println("No such enrollment exists.");
        }
    }

    private boolean isEnrolled(Student student, Course course) {
        for (Enrollment e : enrollments) {
            if (e.getStudent().equals(student) && e.getCourse().equals(course)) return true;
        }
        return false;
    }

    public void listEnrollments() {
        System.out.println("\n---------- ENROLLMENT LIST ----------");
        if (enrollments.isEmpty()) {
            System.out.println("No enrollments found.");
        } else {
            for (Enrollment e : enrollments) System.out.println(e);
        }
    }
}
