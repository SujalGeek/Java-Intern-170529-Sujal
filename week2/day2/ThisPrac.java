/* To understand the concept of this in JAVA
  Author: Sujal Morwani
 * Created On: 12/08/2025
 */
class Rectangle1 {
  int length;
  int breadth;

  Rectangle1(int length, int breadth) {
    // name conflict so how to refer to the length and breadth of this class
    // so this is used to refer the current object
    this.length = length;
    this.breadth = breadth;
  }

  void display() {
    System.out.println("Length: " + this.length);
    System.out.println("Breadth: " + this.breadth);
  }
}

public class ThisPrac {
  public static void main(String[] args) {
    Rectangle1 r1 = new Rectangle1(4, 5);
    r1.display();
  }
}
