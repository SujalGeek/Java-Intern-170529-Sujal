/* To understand the concept of the Outer and Inner class in Java
 * Author: Sujal Morwani
 * Created On: 13/08/2025
 */

class Outer {
  int x = 10;
  Inner i = new Inner();

  class Inner {
    int y = 20;

    public void innerDisplay() {
      System.out.println(x + " " + y);
    }
  }

  public void outerDisplay() {
    i.innerDisplay();
    System.out.println(i.y);
  }
}

public class InnerExample {
  public static void main(String[] args) {
    Outer.Inner o1 = new Outer().new Inner();
    o1.innerDisplay();
  }
}
