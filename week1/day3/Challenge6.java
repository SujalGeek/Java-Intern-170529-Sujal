/* Here is the task to find a number is odd or even
 * and find person is young ot not young.
 * and find the grades for given marks
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */

import java.util.*;

public class Challenge6 {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the number: ");
    int number1 = sc.nextInt();
    // checking if the number is even or odd

    if (number1 % 2 == 0) {
      System.out.println("The number is even");
    } else {
      System.out.println("The number is odd");
    }

    // Find if a person is young or not
    System.out.println("Enter the age: ");
    int age = sc.nextInt();
    if (age > 18) {
      System.out.println("The person is not young");
    } else {
      System.out.println("The person is young");
    }
    System.out.println("Enter the marks of the subject");
    int marks1 = sc.nextInt();
    int marks2 = sc.nextInt();
    int marks3 = sc.nextInt();

    int avg = (marks1 + marks2 + marks3) / 3;
    if (avg > 70) {
      System.out.println("The grade is A");
    } else if (age >= 60 && age < 70) {
      System.out.println("The grade is B");
    } else if (age >= 50 && age < 60) {
      System.out.println("The grade is C");
    } else if (age >= 40 && age < 50) {
      System.out.println("The grade is D");
    } else {
      System.out.println("The grade is F");
    }
  }
}
