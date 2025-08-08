
/* The task is to overload method and to calculate the areas and the other
 * is to overload method to reverse a int or array and other overloaded 
 * method to validate name and age
 * Author: Sujal Morwani
 * Created On: 08/08/2025
 */
import java.util.*;

public class Challenge16 {
  static double area(double l, double b) {
    return l * b;
  }

  static double area(double radius) {
    double result = Math.PI * radius * radius;
    return result;
  }

  static int reverse(int a) {
    int reverse = 0;
    while (a > 0) {
      int remainder = a % 10;
      reverse = reverse * 10 + remainder;
      a = a / 10;
    }
    return reverse;
  }

  static void reverse(int A[]) {
    int left = 0;
    int right = A.length - 1;
    while (left <= right) {
      int temp = A[left];
      A[left] = A[right];
      A[right] = temp;
      left++;
      right--;
    }
  }

  static boolean validate(String name) {
    return name.matches("[a-zA-Z\\s]+");
  }

  static boolean validate(int age) {
    return (age >= 3 && age <= 15);
  }

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the sides of the recatangle: ");
    double num1 = sc.nextDouble();
    double num2 = sc.nextDouble();

    double resultRectangle = area(num1, num2);
    System.out.println("The area of rectangle is " + resultRectangle);

    double radius = sc.nextDouble();
    System.out.println("Enter the radius of the circle: ");
    double resultCircle = area(radius);
    System.out.println("The area of the circle is: " + resultCircle);

    System.out.println("Enter the number to reverse: ");
    int reverseNumber = sc.nextInt();
    int resultOfReverse = reverse(reverseNumber);
    System.out.println("After reversing the number: " + resultOfReverse);

    int A[] = { 1, 2, 3, 4, 5 };
    reverse(A);
    for (int i = 0; i < A.length; i++) {
      System.out.print(A[i] + " ");
    }
    System.out.println();
    sc.nextLine();
    System.out.println("Enter the String: ");
    String name = sc.nextLine();
    boolean resultOfString = validate(name);
    System.out.println("The resukt of the string: " + resultOfString);
    System.out.println();
    int age = sc.nextInt();
    boolean resultOfAge = validate(age);
    System.out.println("The boy is younger or not: " + resultOfAge);
  }
}
