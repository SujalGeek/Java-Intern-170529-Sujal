/* To understand the concept of Local Inner class or Anonymous Class and Static
 * class in Java
 * Author: Sujal Morwani
 * Created On: 13/08/2025
 */
class Outer {
  void Display() {
    class Inner {
      void innerDisplay() {
        System.out.println("The inner Display will be called!!");
      }
    }

    Inner i = new Inner();
    i.innerDisplay();
  }
}

// Anonymous class
// can use it for interface also
abstract class Outer2 {
  abstract void Display1();
}

class Outer3 {
  public void meth() {
    Outer2 oi = new Outer2() {
      public void Display1() {
        System.out.println("The display method is there!!");
      }
    };
    oi.Display1();
  }
}

class Outer4 {
  static int x = 10;
  int y = 20;

  static class Inner {
    void display() {
      System.out.println(x);
      // System.out.println(y); error here because static class can only access
      // static data members and methods
    }

  }
}

public class LocalInner {
  public static void main(String[] args) {
    Outer4.Inner i1 = new Outer4.Inner();
    i1.display();

    Outer o1 = new Outer();
    o1.Display();

  }
}
