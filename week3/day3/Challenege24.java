/**
 * To understand the concept of the inter thread commuincation using one example
 * 
 */

class WhiteBoard {
  String test;
  int no_of_students = 0;
  int count = 0;

  public void attendance() {
    no_of_students++;
  }

  synchronized public void write(String msg) {
    System.out.println("Teacher is writing " + msg);
    while (count != 0) {
      try {
        wait();
      } catch (Exception e) {
      }
    }
    test = msg;
    count = no_of_students;
    notifyAll();
  }

  synchronized public String read() {
    while (count == 0) {
      try {
        wait();
      } catch (Exception e) {
      }
    }
    String t = test;
    count--;
    if (count == 0) {
      notify();
    }
    return t;
  }
  // When count 0 teacher will be writing
  // String read(){
  // When count is not 0 then students will be reading
  // attendance() { noofSTudents++;}
  // }
}

class Teacher extends Thread {
  WhiteBoard wb;
  String notes[] = {
      "Java is language",
      "It is OOPS",
      "It is Platform Independent",
      "It supports Thread",
      "end"
  };

  public Teacher(WhiteBoard w) {
    wb = w;
  }

  public void run() {
    for (int i = 0; i < notes.length; i++) {
      wb.write(notes[i]);
    }
  }
}

class Student extends Thread {
  String name;
  WhiteBoard wb;

  public Student(String n, WhiteBoard w) {
    name = n;
    wb = w;
  }

  public void run() {
    String text;
    wb.attendance();

    do {
      text = wb.read();
      System.out.println(name + " Reading " + text);
      System.out.flush();
    } while (!text.equals("end"));
  }
}

/*
 * class Teacher extends Thread
 * run() -> whiteboard
 * 
 * class Student extends Thread{
 * String name;
 * // construcntor
 * run method then -> read()
 * }
 */
public class Challenege24 {
  public static void main(String[] args) {
    WhiteBoard wb = new WhiteBoard();
    Teacher t = new Teacher(wb);

    Student s1 = new Student("1. Sujal", wb);
    Student s2 = new Student("2. Karan", wb);
    Student s3 = new Student("3. Rohan", wb);
    Student s4 = new Student("4. Soham", wb);

    t.start();

    s1.start();
    s2.start();
    s3.start();
    s4.start();
  }
}
