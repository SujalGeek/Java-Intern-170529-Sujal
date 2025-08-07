
/* To understand the relational operator in java
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */
import java.util.*;

public class Relational {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the first number: ");
    int number1 = sc.nextInt();
    System.out.println("Enter the second number: ");
    int number2 = sc.nextInt();
    System.out.println("Enter the third number: ");
    int number3 = sc.nextInt();

    if (number1 > number2 && number1 > number3) {
      System.out.println("Number1 is the largest");
    } else if (number2 > number1 && number2 > number3) {
      System.out.println("Number2 is the largest");
    } else {
      System.out.println("Number3 is the largest");
    }

    System.out.println("After using relational OR operator: " + (number1 < number2 || number2 > number3));

    int number5 = -20;
    if (number5 >= 0) {
      System.out.println("Postive number");
    } else {
      System.out.println("Negative number");
    }

    sc.close();
  }

}
