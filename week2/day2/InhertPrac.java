/* To understand the concept of Inheritance 
 *  Author: Sujal Morwani
 * Created On: 12/08/2025
 */
// class Parent {
//   public Parent() {
//     System.out.println("Parent class Constructor");
//   }
// }

// class Child extends Parent {
//   public Child() {
//     System.out.println("Child class constructor will be called");
//   }
// }

// class GrandChild extends Child {
//   public GrandChild() {
//     System.out.println("Grand Child constructor will be called ");
//   }
// }

class Rectangle {
  int length;
  int breadth;

  Rectangle() {
    length = breadth = 1;
  }

  Rectangle(int l, int b) {
    length = l;
    breadth = b;
  }
}

class Cuboid extends Rectangle {
  int height;

  Cuboid() {
    height = 1;
  }

  Cuboid(int h) {
    height = h;
  }

  Cuboid(int l, int b, int h) {
    super(l, b);
    height = h;
  }

  int volume() {
    return length * breadth * height;
  }
}

class InherPrac {
  public static void main(String[] args) {
    // GrandChild c1 = new GrandChild();
    Cuboid c1 = new Cuboid(3, 4, 5);
    System.out.println(c1.volume());
  }

}