
/* To understand the concept of Object class in Java
 * Author: Sujal Morwani
 * Created On: 20/08/2025
 */
import java.lang.*;

// any class while creating already extended the object class
// Any class directly or indirectly is inherting object
class MyObject {

  public String toString() {
    return "This is Object";
  }

  public int hashCode() {
    return 100;
  }

  public boolean equals(Object o) {
    return this.hashCode() == o.hashCode();
  }
}
/*
 * class MyObject2 extends MyObject
 * 
 */

public class ObjectDemo {
  public static void main(String[] args) {
    MyObject m1 = new MyObject();
    MyObject m2 = new MyObject();
    System.out.println(m1.equals(m2));

    // System.out.println(m1.hashCode());
    // System.out.println(m1.toString());
  }
}
