/* To understand the concept of Serialization in Java can be done using InputStreams
 * and OutputStream (basically using the PrintStreams) achieving the Serialization
 * Author: Sujal Morwani
 * Created On: 25/08/2025
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

class Student {
  int rollno;
  String name;
  String dept;

}

public class InputStreamDemo {
  public static void main(String[] args) throws Exception {
    FileOutputStream fos = new FileOutputStream(
        "C:\\Users\\USER\\Documents\\Java-internship\\week4\\day1\\Files\\Student1.txt");
    PrintStream ps = new PrintStream(fos);

    FileInputStream fis = new FileInputStream(
        "C:\\Users\\USER\\Documents\\Java-internship\\week4\\day1\\Files\\Student1.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

    Student s1 = new Student();
    s1.rollno = 10;
    s1.name = "Sujal";
    s1.dept = "CSE";

    ps.println(s1.rollno);
    ps.println(s1.name);
    ps.println(s1.dept);

    s1.rollno = Integer.parseInt(br.readLine());
    s1.name = br.readLine();
    s1.dept = br.readLine();

    System.out.println("Roll no: " + s1.rollno);
    System.out.println("Name: " + s1.name);
    System.out.println("Dept: " + s1.dept);

    ps.close();
    fos.close();
  }
}