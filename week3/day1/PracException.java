
/* To understand the concept of exception handling in java
  Author: Sujal Morwani
 * Created On: 18/08/2025
 */
import java.util.*;

public class PracException {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the first number: ");
    int number1 = sc.nextInt();
    System.out.println("Enter the second number: ");
    int number2 = sc.nextInt();
    try {
      float result = number1 / number2;
      System.out.println("The result is: " + result);
    } catch (ArithmeticException e) {
      System.out.println("The second number should not be zero");
    }

    // Nested catch if there any exception
    int a[] = { 3, 4, 5, 6, 7, 0 };
    try {
      int c = a[0] / a[5];
      System.out.println(c);
      System.out.println(a[7]);
    } catch (ArithmeticException e) {
      System.out.println(e.getMessage());
      System.out.println("The toString returns: " + e.toString());
      System.out.println("The print Stack Trace result");
      e.printStackTrace();
      System.out.println("The denominator shoud not be zero");
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Please check the size of the array");
    }
    System.out.println("Done with programmmmm!");
    sc.close();
  }
}
