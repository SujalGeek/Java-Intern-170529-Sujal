/* To understand the concept of the Method Overrriding and Dynamic Method 
  Dispatch(Runtime Polymorphism) in Java
  Author: Sujal Morwani
 * Created On: 12/08/2025
 */
// class Super {
//   public void display() {
//     System.out.println("Super class method is called");
//   }
// }

// class Sub extends Super {

//   public void display() {
//     System.out.println("Sub class method is called");
//   }
// }
class TV {
  public void switchOn() {
    System.out.println("TV is Switched ON");
  }

  public void changeChannel() {
    System.out.println("TV Channel is Changed. ");
  }
}

class SmartTv extends TV {
  @Override
  public void switchOn() {
    System.out.println("Smart TV is switched On");
  }

  @Override
  public void changeChannel() {
    System.out.println("Smart TV Channel is Changed");
  }

  public void browse() {
    System.out.println("Smart TV Browsing");
  }
}

class Super {
  public void meth1() {
    System.out.println("Super Meth1");
  }

  public void meth2() {
    System.out.println("Super Meth2");
  }
}

class Sub extends Super {
  public void meth2() {
    System.out.println("Sub meth2 is called");
  }

  public void meth3() {
    System.out.println("Sub meth3 is called");
  }
}

public class OverrdingPrac {
  public static void main(String[] args) {

    // Super s2 = new Super();
    // s2.display();

    // Sub s1 = new Sub();
    // s1.display();

    // // Dynamic Method Dispatch :- the object will be there for Sub one and it
    // will
    // // call
    // // its display method not the Super class reference will be there
    // Super s3 = new Sub();
    // s3.display();

    SmartTv t = new SmartTv();
    t.switchOn();
    t.changeChannel();
    t.browse();

    TV t1 = new TV();
    t1.changeChannel();
    t1.switchOn();

    TV t2 = new SmartTv();

    // this is the gives the error so if you have the same methods in both parent
    // and child class
    // t2.browse();

    /*
     * Dynamic method dispatch means what super class reference holding an object
     * of the sub class and calling override method and the method of object will
     * be called and method based on the object will be called.
     * This is runtime polymorphism
     */

    Super s1 = new Sub();
    s1.meth2();
    s1.meth1();
    // s1.meth3();
  }
}
