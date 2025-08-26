import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * To understand the concept of the List and the Concept of Comparable
 * and Comparator in Java
 * Author: Sujal Morwani
 * Created On: 26/08/2025
 */

class Student implements Comparable<Student> {
  String id;
  String name;
  String age;

  public Student(String id, String name, String age) {
    this.id = id;
    this.name = name;
    this.age = age;
  }

  public String toString() {
    return "Student{" + "id=" + id + "name=" + name + "age=" + age + "}";
  }

  public int compareTo(Student o2) {
    return this.name.compareTo(o2.name);
  }
}

public class ListDemo {
  public static void main(String args[]) throws Exception {

    ArrayList<Integer> a1 = new ArrayList<>(20);
    ArrayList<Integer> a2 = new ArrayList<>(List.of(50, 60, 70, 80));

    a1.add(10);
    a1.add(20);
    a1.add(30);
    a1.addAll(a2);
    System.out.println(a1.get(1));
    System.out.println(a1.indexOf(10));
    System.out.println(a1.contains(10));
    Integer num = a1.getFirst();
    System.out.println(a1.isEmpty());
    a1.addFirst(1);
    // System.out.println(a1.iterator());
    System.out.println(num);
    System.out.println(a1);
    System.out.println(a1.size());

    a1.set(0, 2);
    System.out.println(a1);
    for (int i = 0; i < a1.size(); i++) {
      System.out.println(a1.get(i));
    }
    System.out.println("Using for each loop we have to print it!!");
    for (Integer x : a1) {
      System.out.print(x + " ");
    }

    System.out.println();
    // Iterator allow one direction
    // List Iterator allow bi directional changes can be done so use it
    ListIterator<Integer> it = a1.listIterator();
    while (it.hasNext()) {
      System.out.println(it.next());
    }

    System.out.println("Using for each method");
    a1.forEach(n -> System.out.println(n));

    System.out.println("Using println method");
    a1.forEach(System.out::println); // it the expression

    System.out.println("Using the show method: ");
    a1.forEach(n -> show(n));

    // Converting the arrays into the List how to do that

    System.out.println("This is the way to convert the array into the list");
    int[] arr = { 11, 5, 6, 7, 90 };
    int[] arr2 = Arrays.copyOf(arr, 4);
    System.out.println(arr2);

    System.out.println("After copying all the elements");
    for (int x : arr2) {
      System.out.println(x);
    }
    // Using stream you can convert the arrays into the list
    // List<Integer> list = Arrays.stream(arr).boxed().toList();
    // After applying this method this is immutable so we have to made the mutable
    List<Integer> list = new ArrayList<>(Arrays.stream(arr).boxed().toList());
    System.out.println(list);

    // Now how we can sort it!!
    Collections.sort(list);

    // Another way to do that in the list just create and then sort it
    // When you are adding into the list like list.add(10)
    // In that time you are passing the integer but Java converts into the objects
    // This process is called the autoboxing (int -> object)
    // and reverse process is called auto unboxing(object -> primitve)

    List<Student> students = new ArrayList<>();
    // Student s1 = new Student("12", "Sujal", "18");
    students.add(new Student("12", "Sujal", "18"));
    students.add(new Student("13", "Rohan", "19"));
    students.add(new Student("14", "Ram", "20"));
    students.add(new Student("15", "Laxman", "22"));

    // students.add(null)
    Collections.sort(students);
    System.out.println(students);
  }

  static void show(int n) {
    if (n > 60) {
      System.out.println(n);
    }
  }
}