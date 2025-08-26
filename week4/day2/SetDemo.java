/* To understand the concept of HashSet and TreeSet in Java
 * Author: Sujal Morwani
 * Created On: 26/08/2025
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public class SetDemo {
  public static void main(String[] args) {

    HashSet<Integer> hs = new HashSet<>(20, 0.25f);
    hs.add(10);
    hs.add(20);
    hs.add(30);
    hs.add(10);
    hs.add(40);
    hs.add(50);
    hs.add(60);

    HashSet<Integer> hs1 = new HashSet<>();
    hs1.addAll(hs);

    System.out.println("The another HashSet is: " + hs1);

    System.out.println(hs);
    // System.out.println(hs.toArray());
    System.out.println(hs.toString());
    hs.remove(10);
    System.out.println(hs);

    hs.forEach(e -> {
      System.out.println(e);
    });

    System.out.println("Using the println method: ");
    hs1.forEach(System.out::println);

    // hs1.forEach();
    // System.out.println(hs1.clear());
    hs1.clear();
    System.out.println(hs1.isEmpty());

    System.out.println(hs.getClass());

    Iterator<Integer> i12 = hs.iterator();
    while (i12.hasNext()) {
      System.out.println(i12.next());
    }

    // Tree set
    TreeSet<Integer> t12 = new TreeSet<>();
    t12.add(10);
    t12.add(20);
    t12.add(30);
    t12.add(40);
    t12.add(50);
    t12.add(3);
    t12.add(10);

    System.out.println(t12);

    System.out.println(t12.size());
    // It contains the comparatar and comparable
    System.out.println(t12.first());
    // It will print the 10 value because 12 floor means nearest lower value 10
    System.out.println(t12.floor(12));
    // It will print the 20 value because 12 ceiling means nearest upper value 20
    System.out.println(t12.ceiling(12));
    System.out.println(t12.lower(15));
    System.out.println(t12.higher(15));

    t12.forEach(e -> {
      System.out.println(e);
    });

    System.out.println("Using the println method");
    t12.forEach(System.out::println);

    System.out.println();

    Iterator<Integer> i123 = t12.iterator();
    while (i123.hasNext()) {
      System.out.println(i123.next());
    }
  }
}
