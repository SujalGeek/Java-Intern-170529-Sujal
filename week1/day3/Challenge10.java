/* The task to count the digits of a number and
 * other one is to finding a number is Armstrong or not
 * and other one is to reverse a number
 * and check a number is pallindrome or not
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */

import java.util.Scanner;

public class Challenge10 {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter a number: ");
    int number1 = sc.nextInt();
    int count = 0;
    while (number1 > 0) {
      number1 = number1 / 10;
      count++;
    }
    System.out.println("The count of digits are: " + count);

    System.out.println("Enter the number to check it is Armstrong or not: ");
    int number2 = sc.nextInt();
    int originalNumber = number2;
    int sum = 0;
    while (number2 > 0) {
      int remainder = number2 % 10;
      number2 = number2 / 10;
      sum = sum + (remainder * remainder * remainder);
    }
    if (sum == originalNumber) {
      System.out.println("It is Armstrong Number");
    } else {
      System.out.println("It is not Armstrong Number");
    }

    System.out.println("Enter the number to reverse it: ");
    int number3 = sc.nextInt();
    int originalNumber2 = number3;
    // number3 = sc.nextInt();
    int reverse = 0;
    while (number3 > 0) {
      int remainder = number3 % 10;
      reverse = reverse * 10 + remainder;
      number3 = number3 / 10;
    }
    System.out.println(reverse);
    if (originalNumber2 == reverse) {
      System.out.println("It is pallindrome number");
    } else {
      System.out.println("It is not pallindrome number");
    }
  }
}
