/* To understand the concept of the data hidding
 * understand how the set and get methods works in Java
 * Author: Sujal Morwani
 * Created On: 11/08/2025
 */
class RectangleTest {
  private double length;
  private double breadth;

  public double getLength() {
    return length;
  }

  public double getBreadth() {
    return breadth;
  }

  public void setLength(double length) {
    if (length > 0) {
      this.length = length;
    } else {
      length = 0;
    }
  }

  public void setBreadth(double breadth) {
    if (breadth > 0) {
      this.breadth = breadth;
    } else {
      breadth = 0;
    }
  }

  public double area() {
    return length * breadth;
  }

  public double perimeter() {
    return 2 * (length + breadth);
  }

  public void display() {
    System.out.println("The length and breadth of the rectangle is: " + length + " and " + breadth);
  }
}

public class DataHidden {
  public static void main(String[] args) {

    RectangleTest r1 = new RectangleTest();
    r1.setLength(6.8);
    r1.getLength();

    r1.setBreadth(4.6);
    r1.getBreadth();

    r1.display();
    double area = r1.area();
    System.out.println("The area is: " + area);
    double perimeter = r1.perimeter();
    System.out.println("The perimeter is: " + perimeter);
  }
}
