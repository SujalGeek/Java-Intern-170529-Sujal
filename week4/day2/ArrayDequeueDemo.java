/* To understand the concept of the Array Dequeue in Java
 * Author: Sujal Morwani
 * Created On: 26/08/2025
 */

// Insertion from rear and deletion from front side
// Inserting from rear end as well as deleting from this side
// Dequeue can be used for different purposes it can act like stack
// it also acts as queue 

import java.util.ArrayDeque;
import java.util.Iterator;

public class ArrayDequeueDemo {
  public static void main(String[] args) {

    /*
     * What is the difference between the remove first and the poll first
     * While using the remove first it is unable to remove then it will throws
     * Exception
     * While poll first will not throw an Exception it will return null
     * so use offerfirst(in place addfirst), poll first(in place of remove first)
     * and peek first(in place of getFirst)
     * The ArrayDequeue performs operations in constant time it is faster than Stack
     * donot use the linked list for implemeting queue faster
     */

    ArrayDeque<Integer> arrayDequeue1 = new ArrayDeque<>();
    arrayDequeue1.offerLast(10);
    arrayDequeue1.offerLast(20);
    arrayDequeue1.offerLast(30);
    arrayDequeue1.offerLast(40);

    System.out.println(arrayDequeue1);

    arrayDequeue1.offerFirst(1);
    arrayDequeue1.offerFirst(2);
    arrayDequeue1.offerFirst(3);

    System.out.println(arrayDequeue1);

    System.out.println();
    arrayDequeue1.forEach((x) -> System.out.print(x + " "));

    System.out.println();
    System.out.println(arrayDequeue1.peekFirst());
    System.out.println(arrayDequeue1.peekLast());

    Iterator<Integer> i12 = arrayDequeue1.iterator();
    while (i12.hasNext()) {
      System.out.println(i12.next());
    }

    System.out.println("Retrieves the head of the dequeue: ");
    System.out.println(arrayDequeue1.element());
  }

}
