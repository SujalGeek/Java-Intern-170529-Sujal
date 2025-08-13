/* To understand the concept of static in Java(static variable,static method,static 
    static class and static block)
    Author: Sujal Morwani
 *  Created On: 13/08/2025
 */

// The static is created in inside method area once the class is loaded
// Static variable belongs to the class and it can shared by all the objects
// Can be used as shared data member by all the objects access using the object name
// and can be access using the class name

// Static methods can access only static variable they canbe access using the class name
// and can be access using the class name

// If you have multiple static methods then you can use the static class
// Then the object of that class can be created outside indepedent of the outer class
// It can access only the static member of the class
// can not use the this and super
// only the nested one can be the static class

// Static block will firstly will be executed when you intialize it 
//(create object at that time)

import packages.Test;

class Test {
  static int y = 20;
  int x = 23;

  // Non static methods are allow to use the static variables
  void show() {
    System.out.println(y);
    // It allows
    System.out.println(x);
  }

  static void display() {
    // It does not allows it
    // System.out.println(x);
    System.out.println(y);
  }

  static {
    System.out.println("block 1");
  }
  static {
    System.out.println("block 2");
  }
}

public class StaticPrac {
  public static void main(String[] args) {

    Test t1 = new Test();
    t1.show();
    t1.x = 40;
    Test.y = 43;
    Test t2 = new Test();
    t2.show();
  }
}
