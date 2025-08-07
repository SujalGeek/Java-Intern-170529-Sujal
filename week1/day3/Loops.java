
/* Here is to practice the loops and display muliplication table
* and to find sum of n numbers and find the factorial of a number
* and display the digits
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */

import java.util.*;

public class Loops {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the number: ");
    int number1 = sc.nextInt();

    for (int i = 1; i <= 10; i++) {
      System.out.println(number1 + " * " + i + " = " + number1 * i);
    }
    System.out.println("Enter the number: ");
    int number2 = sc.nextInt();
    int sum = 0;
    for (int i = 1; i <= number2; i++) {
      sum = sum + i;
    }
    System.out.println(sum);
    System.out.println("Enter the number: ");
    int number3 = sc.nextInt();
    int factorial = 1;
    for (int i = 1; i <= number3; i++) {
      factorial = factorial * i;
    }
    System.out.println(factorial);
    int number4 = sc.nextInt();

    while (number4 > 0) {
      int remainder = number4 % 10;
      number4 = number4 / 10;
      System.out.println(remainder);
    }
  }
}
