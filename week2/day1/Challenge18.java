
/* To understand the concept of Java Object Oriented Programming
 * Author: Sujal Morwani
 * Created On: 11/08/2025
 */
import java.util.*;

class Circle {
  public double radius;

  public double area(double radius) {
    return Math.PI * radius * radius;
  }

  public double perimeter(double radius) {
    return 2 * Math.PI * radius;
  }

  public double circumference(double radius) {
    return perimeter(radius);
  }
}

class Rectangle {
  public double length;
  public double breadth;

  public double area() {
    return length * breadth;
  }

  public double perimeter() {
    return 2 * (length + breadth);
  }

  public boolean isSquare() {
    return length == breadth;
  }
}

public class Challenge18 {
  public static void main(String[] args) {
    Circle obj1 = new Circle();
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the radius of the circle: ");
    double radius = sc.nextDouble();
    // obj1.radius = 5;

    System.out.println("The area of the circle is : " + obj1.area(radius));
    System.out.println("The perimter or circurmference of the circle is: " + obj1.circumference(radius));

    Rectangle r1 = new Rectangle();
    r1.length = 5;
    r1.breadth = 10;
    System.out.println("The area of the rectangle is: " + r1.area());
    System.out.println("The perimeter of the rectangle is: " + r1.perimeter());
    System.out.println("It is square or not: " + r1.isSquare());
  }
}
