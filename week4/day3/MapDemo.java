/* To understand the concept of TreeMap and HashMap in Java
 * Author: Sujal Morwani
 * Created On: 27/08/2025
 */

import java.util.*;

public class MapDemo {
  public static void main(String[] args) {

    HashMap<Integer, String> hashMap1 = new HashMap<>();
    hashMap1.put(1, "Rohan");
    hashMap1.put(2, "Sohan");
    hashMap1.put(3, "Sujal");
    hashMap1.put(4, "Ram");
    hashMap1.put(5, "Shyam");

    System.out.println("Hash Map(No Order): ");

    System.out.println("By using the simple for each loop: ");
    hashMap1.forEach((key, value) -> {
      System.out.println(key + " " + value);
    });

    System.out.println("By usng the Strem and for Each method and how to use it");

    hashMap1.entrySet().stream().forEach(entry -> System.out.println(entry.getKey() + "-> " + entry.getValue()));

    System.out.println("Using the iterator method");
    Iterator<Map.Entry<Integer, String>> i1 = hashMap1.entrySet().iterator();
    while (i1.hasNext()) {
      System.out.println(i1.next());
    }
    // for loop over map we dont loop over map so we need to make Set()
    /*
     * Why entrySet()?
     * entrySet() returns a Set of Map.Entry objects
     * A Set is iterable, so you can use for-each loop
     */

    System.out.println(hashMap1.get(2));
    System.out.println(hashMap1.containsKey(2));

    TreeMap<Integer, String> tr1 = new TreeMap<>(Map.of(0, "A", 1, "B", 2, "C", 3, "D", 4, "E"));

    System.out.println(tr1);

    tr1.forEach((key, value) -> {
      System.out.println(key + " " + value);
    });

    // tr1.entrySet();
    System.out.println(tr1.firstEntry());
    System.out.println(tr1.containsKey(2));

    System.out.println(tr1.size());
    System.out.println(tr1.keySet());
    System.out.println(tr1.ceilingKey(1));
    // System.out.println(tr1.)
    System.out.println(tr1.higherKey(2));
    System.out.println(tr1.lowerKey(2));
    // System.out.println();
    Map.Entry<Integer, String> first = tr1.firstEntry();
    System.out.println(first);

    Map.Entry<Integer, String> second = tr1.lastEntry();
    System.out.println("The last entry is " + second);

    System.out.println(first.getValue());
    System.out.println(first.getKey());

    System.out.println("Using the Map Entry: ");

    System.out.println(second.getKey());
    System.out.println(second.getValue());

  }
}
