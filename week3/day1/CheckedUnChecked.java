/* To understand the concept of the unchecked and checked exception in Java
 * Author: Sujal Morwani
 * Created On: 18/08/2025
 */

import java.io.FileReader;

class LowBalanceException extends Exception {

  public String toString() {
    return "Balance should not be less than 5000";
  }
}

public class CheckedUnChecked {
  static void fun1() {
    try {
      System.out.println(10 / 0);
      // It will check at the complie time not the runtime like arithmetic Exception
      // FileReader fr = new FileReader("myfile.txt");
      throw new LowBalanceException();
    } catch (ArithmeticException e) {
      // Handle exception
      System.out.println(e.getMessage());
      System.out.println(e.toString());
      e.printStackTrace();
      // Now for the userdefined exception the class should be throws and one should
      // catch block
    } catch (LowBalanceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  static void fun2() {
    fun1();
  }

  static void fun3() {
    fun2();
  }

  public static void main(String[] args) {
    fun3();
  }
}
