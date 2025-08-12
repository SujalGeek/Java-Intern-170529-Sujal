/* To understand the concepts of Inteface
 * Author: Sujal Morwani
 * Created On: 12/08/2025
 */

interface Test {
  final static int X = 10;

  public abstract void meth1();

  public abstract void meth2();

  public static void meth3() {
    System.out.println("Static method of Test");
  }
}

interface Test2 extends Test {
  void meth4();
}

class Demo1 implements Test2 {
  public void meth1() {
    System.out.println("The Meth1 is called!!");
  }

  public void meth2() {
    System.out.println("The Meth2 is called!!");
  }

  public void meth4() {
    System.out.println("The Meth4 is called!!");
  }
}

public class InterfacePractice {
  public static void main(String[] args) {
    System.out.println(Test.X);
    Test.meth3();
    // THE METHODS IN THE INTERFACE CAN BE PRIVATE AND DEFAULT
    // WHEN THE DEFAULT ONE ARE USED U CAN DEFINE IN THE INTERFACE AND AFTER
    // IF YOU HAVE TO OVVERWRIDE IT THEN ALSO IT WORKS BUT IF YOU CAN NOT
    // OVVERWRIDE IT THEN THE DEFAULT WILL EXECUTE AND CAN ALSO WRAP THE
    // PRIVATE METHODS IN THE DEFAULT ONE

  }
}
