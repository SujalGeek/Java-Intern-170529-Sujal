/* To understand the concept of Serialization using the Data Streams and learn the concept
 * Author: Sujal Morwani
 * Created On: 25/08/2025
 */

// import java.io.DataInput;
import java.io.DataInputStream;
// import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

class Student {
  int rollno;
  String name;
  float avg;
  String dept;

}

public class DataStreamDemo {
  public static void main(String[] args) throws Exception {
    FileOutputStream fos = new FileOutputStream("Student2.txt");
    DataOutputStream dos = new DataOutputStream(fos);

    Student s1 = new Student();
    s1.rollno = 11;
    s1.name = "Sujal";
    s1.dept = "CSE";
    s1.avg = 23.5f;
    dos.writeInt(s1.rollno);
    dos.writeUTF(s1.name);
    dos.writeUTF(s1.dept);
    dos.writeFloat(s1.avg);

    // reeading from file
    FileInputStream fis = new FileInputStream("Student2.txt");
    DataInputStream dis2 = new DataInputStream(fis);

    Student s2 = new Student();
    s2.rollno = dis2.readInt();
    s2.name = dis2.readUTF();
    s2.dept = dis2.readUTF();
    s2.avg = dis2.readFloat();

    System.out.println("Roll no: " + s2.rollno);
    System.out.println("Name: " + s2.name);
    System.out.println("Dept: " + s2.dept);
    System.out.println("Average " + s2.avg);

    dos.close();
    fos.close();
    dis2.close();
    fis.close();
  }
}
