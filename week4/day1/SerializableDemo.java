/* To understand the concept of the Serializable in Java using the Object Streams like
 * ObjectOutputStream and ObjectInputStream 
 * Author: Sujal Morwani
 * Created On: 25/08/2025
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
// import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class Student implements Serializable {

  private int rollNo;
  private String name;
  private String dept;
  private float avg;
  public static int Data = 10;
  public transient int t;

  public Student() {

  }

  public Student(int r, String n, float a, String d) {
    rollNo = r;
    name = n;
    avg = a;
    dept = d;
    Data = 500;
    t = 500;
  }

  public String toString() {
    return "\n Student Details\n" + "\n Roll " + rollNo + "\nName " + name + "\nAverage " + avg + "\nDept " + dept
        + "\nData " + Data + "\nTransient " + t + "\n";
  }
}

public class SerializableDemo {
  public static void main(String[] args) throws Exception {
    FileOutputStream fos = new FileOutputStream("Student3.txt");
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    Student s1 = new Student(11, "Sujal", 23.55f, "CSE");

    oos.writeObject(s1);

    FileInputStream fis2 = new FileInputStream("Student3.txt");
    ObjectInputStream ois = new ObjectInputStream(fis2);

    Student s2 = (Student) ois.readObject();
    System.out.println(s2);

    fos.close();
    oos.close();
    fis2.close();
    ois.close();
  }
}
