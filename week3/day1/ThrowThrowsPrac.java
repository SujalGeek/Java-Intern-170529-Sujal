/* To understand the concept of the throw and throws in Java
 * Author: Sujal Morwani 
 * Created On: 18/08/2025
 */

class NegativeDimesionException extends Exception {
  public String toString() {
    return "Dimensions of a Rectangle cannot be Negative";
  }
}

public class ThrowThrowsPrac {

  static int area(int l, int b) throws NegativeDimesionException {
    if (l < 0 || b < 0) {
      throw new NegativeDimesionException();
    }
    return l * b;
  }

  static void meth1() throws NegativeDimesionException {
    System.out.println("Area is " + area(-10, 5));
  }

  static void meth2() throws NegativeDimesionException {
    meth1();
  }

  public static void main(String[] args) {
    try {
      meth2();
    } catch (NegativeDimesionException e) {
      // TODO: handle exception
      System.out.println(e);
    }
    System.out.println("Done with the program!!");
  }
}
