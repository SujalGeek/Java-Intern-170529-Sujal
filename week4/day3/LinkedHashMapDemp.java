/* To understand the concept of LinkedHashMap in Java
 * Author: Sujal Morwani
 * Created On: 27/08/2025
 * 
 */

import java.util.*;

// LinkedHash Map and LinkedHash Set are same in order of insertion 
public class LinkedHashMapDemp {
  public static void main(String[] args) {

    LinkedHashMap<Integer, String> lh1 = new LinkedHashMap<>();
    lh1.put(1, "Ram");
    lh1.put(2, "Shyam");
    lh1.put(3, "Moron");
    lh1.put(1, "RadheShyam");

    Iterator<Map.Entry<Integer, String>> m12 = lh1.entrySet().iterator();
    while (m12.hasNext()) {
      System.out.println(m12.next());
    }

    LinkedHashMap<Integer, String> lruCache = new LinkedHashMap<>(4, 0.75f, true);
    lruCache.put(1, "A");
    lruCache.put(2, "B");
    lruCache.put(3, "C");
    lruCache.put(4, "D");

    lruCache.get(2);
    lruCache.get(3);

    for (Map.Entry<Integer, String> m13 : lruCache.entrySet()) {
      System.out.println(m13.getKey() + " " + m13.getValue());
    }

    // when to use the linked Hash map
    /*
     * Very useful when you want to maintain insertion order
     * (like displaying logs, maintaining user history, or ordered settings).
     * When using access-order mode, it’s handy for implementing LRU caches (Least
     * Recently Used)
     * since the least recently accessed entry can easily be identified and removed.
     */
  }

  /*
   * Performance
   * Basic operations like put(), get(), remove() are O(1) on average, just like
   * HashMap.
   * Extra overhead compared to HashMap because of the doubly-linked list that
   * maintains order.
   * Predictable iteration order (insertion order or access order) makes it more
   * consistent for ordered data handling.
   * 
   */
}
