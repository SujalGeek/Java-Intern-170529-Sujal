/* To understand the concept of the constructor
 * Author: Sujal Morwani
 * Created On: 11/08/2025
 */
class RectangleTest {
  private double length;
  private double breadth;

  RectangleTest() {
    System.out.println("Default Constructor is called");
  }

  RectangleTest(double length, double breadth) {
    this.length = length;
    this.breadth = breadth;
  }

  public RectangleTest(double s) {
    length = breadth = s;
  }

  public void display() {
    System.out.println("The length and breadth of the rectangle or square is: " + length + " and " + breadth);
  }
}

public class ConstructorPractice {
  public static void main(String[] args) {
    // Every class in Java have the default Constructor
    // Same name as class name and make it public and Constructor can be overloaded
    // there is no return type of Constructor
    RectangleTest r1 = new RectangleTest();
    RectangleTest r2 = new RectangleTest(3, 4);
    RectangleTest r3 = new RectangleTest(3.4);
    r2.display();
    r3.display();
  }
}
