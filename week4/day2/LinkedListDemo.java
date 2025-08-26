/* To understand the concept of LinkedList in Java
 * Author: Sujal Morwani
 * Created On: 26/08/2025
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

class Student {
  int id;
  String name;
  String age;

  public Student(int id, String name, String age) {
    this.id = id;
    this.name = name;
    this.age = age;
  }

  public String toString() {
    return "Student{" + "id=" + id + "name=" + name + "age=" + age + "}";
  }
}

class IdComparator implements Comparator<Student> {

  public int compare(Student o1, Student o2) {
    return o1.id - o2.id;
  }
}

class NameComparator implements Comparator<Student> {

  public int compare(Student o1, Student o2) {
    return o1.name.compareTo(o2.name);
  }
}

public class LinkedListDemo {
  public static void main(String[] args) {

    LinkedList<Integer> a12 = new LinkedList<>();
    // LinkedList<Integer>
    // We cannot mention the fixed size of the linked list because when
    // the elements are inserted and the element is gone once when they are deleted

    LinkedList<Integer> a123 = new LinkedList<>(List.of(40, 50, 60, 70));

    a12.add(10);
    a12.add(20);
    a12.add(30);
    a12.add(40);

    a12.addAll(a123);
    System.out.println(a12);
    a12.set(4, 2);
    a12.addFirst(1);
    a12.addLast(100);

    // remove First and last element to remove
    // poll just like remove first and remove last
    System.out.println(a12);
    System.out.println("Get method: " + a12.get(1));

    System.out.println("The first element is: " + a12.peek());
    System.out.println("THe last element is: " + a12.peekLast());
    System.out.println("Using the for each method");
    a12.forEach(n -> System.out.print(n + " "));

    System.out.println();
    System.out.println("Using the println method");
    a12.forEach(System.out::println);

    System.out.println();
    System.out.println("Using the list Iterator method supposed to use this!!");
    ListIterator<Integer> l12 = a12.listIterator();
    while (l12.hasNext()) {
      System.out.print(l12.next() + " ");
    }

    System.out.println("Can it works ??!");
    Object a[] = a12.toArray();
    for (int i = 0; i < a.length; i++) {
      System.out.print(a[i] + " ");
    }
    // System.out.println(a.toString());

    List<Student> students = new ArrayList<>();
    // Student s1 = new Student("12", "Sujal", "18");
    students.add(new Student(12, "Sujal", "18"));
    students.add(new Student(13, "Rohan", "19"));
    students.add(new Student(15, "Ram", "20"));
    students.add(new Student(16, "Laxman", "22"));

    Collections.sort(students, new IdComparator());
    System.out.println(students);

    Collections.sort(students, new NameComparator());
    System.out.println(students);
  }
}
