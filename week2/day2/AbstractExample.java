/* To understand the concept of the Abstract Classes in Java
 * Author: Sujal Morwani
 * Created On: 12/08/2025
 */

// abstract class Super {
//   public Super() {
//     System.out.println("Super class Constructor is called");
//   }

//   public void meth1() {
//     System.out.println("Meth1 is called");
//   }

//   abstract public void meth2();
// }

// class Sub extends Super {
//   public void meth2() {
//     System.out.println("Meth2 is called");
//   }
// }
abstract class Hospital {
  abstract void emergenency();

  abstract void appointment();

  abstract void admit();

  abstract void billing();
}

class MyHospital extends Hospital {
  MyHospital() {
    System.out.println("My Hospital can be opened");
  }

  public void emergenency() {
    System.out.println("There is emergenency room is there!!");
  }

  public void appointment() {
    System.out.println("There is appointment rooms are there!!");
  }

  public void admit() {
    System.out.println("The are admit rooms available!!");
  }

  public void billing() {
    System.out.println("There is billing counter available!!");
  }
}

public class AbstractExample {
  public static void main(String[] args) {
    // error because the class is abstract
    // Super s2 = new Super();
    // Super s = new Sub();
    // s.meth1();
    // s.meth2();

    Hospital mHospital = new MyHospital();
    mHospital.emergenency();
    mHospital.appointment();
    mHospital.admit();
    mHospital.billing();
  }
}
