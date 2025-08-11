/* To understand the concept of the Java OOPs concept and learinig how to implement it
 * Author: Sujal Morwani
 * Created On: 11/08/2025
 */

class Student {
  public int roll_no;
  public String name;
  public String course;
  public int m1, m2, m3;

  public int total() {
    return (m1 + m2 + m3);
  }

  public double average() {
    return total() / 3;
  }

  public String grade() {
    if (average() > 70) {
      return "A";
    } else if (average() > 60 && average() <= 69) {
      return "B";
    } else if (average() > 50 && average() <= 59) {
      return "C";
    } else if (average() > 40 && average() <= 49) {
      return "D";
    } else {
      return "F";
    }
  }

  // If the toString method is there then it automatically by the println
  public String toString() {

    return "Roll No: " + roll_no + "\nName" + name + "\nCourse" + course + "\nMarks1" + m1 + "\nMarks2" + m2
        + "\nMarks3" + m3;
  }
}

public class Challenge19 {
  public static void main(String[] args) {
    Student s1 = new Student();
    s1.roll_no = 20;
    s1.name = "Karan";
    s1.course = "B.Tech";
    s1.m1 = 78;
    s1.m2 = 60;
    s1.m3 = 55;
    System.out.println("The total is: " + s1.total());
    System.out.println("The average is: " + s1.average());
    System.out.println("The grade is: " + s1.grade());
    System.out.println("" + s1);
  }
}
