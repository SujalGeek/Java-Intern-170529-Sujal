/* To understand the Properties and Constructor while using the Cylinder
 *  Author: Sujal Morwani
 * Created On: 11/08/2025
 */

class Cylinder {
  private double radius;
  private double height;

  Cylinder() {
    radius = 1;
    height = 1;
  }

  Cylinder(double radius) {
    setRadius(radius);
  }

  Cylinder(double radius, double height) {
    setDimensions(radius, height);
  }

  public void setRadius(double radius) {
    if (radius > 0) {
      this.radius = radius;
    } else {
      radius = 0;
    }
  }

  public void setHeight(double height) {
    if (height > 0) {
      this.height = height;
    } else {
      height = 0;
    }
  }

  public double getRadius() {
    return radius;
  }

  public double getHeight() {
    return height;
  }

  public void setDimensions(double radius, double height) {
    setRadius(radius);
    setHeight(height);
  }
}

public class Challenge20 {
  public static void main(String args[]) {
    Cylinder obj1 = new Cylinder();
    obj1.setRadius(8.9);
    obj1.setHeight(5.6);
    double radius = obj1.getRadius();
    double height = obj1.getHeight();
    System.out.println("The radius and height are: " + radius + " and " + height);
    Cylinder obj2 = new Cylinder(4.5);
    Cylinder obj3 = new Cylinder(4.5, 6.7);
    System.out.println(obj2.getRadius());
    System.out.println(obj2.getHeight());
    System.out.println(obj3.getRadius());
    System.out.println(obj3.getHeight());

  }
}
