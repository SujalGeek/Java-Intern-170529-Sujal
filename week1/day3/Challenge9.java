
/* The task is to make a menu driven program for the arithmetic operations
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */
import java.util.*;

public class Challenge9 {
  public static void main(String[] args) {
    // Menu Program
    System.out.println("1. Addition");
    System.out.println("2. Subtraction");
    System.out.println("3. Multiplication");
    System.out.println("4. Division");

    Scanner sc = new Scanner(System.in);
    System.out.println("Enter two numbers: ");
    int number1 = sc.nextInt();
    int number2 = sc.nextInt();
    sc.nextLine();
    System.out.println("Enter the option in UPPER CASE(ADD,SUB,MUL,DIV): ");
    String option = sc.nextLine();

    switch (option) {
      case "ADD":
        System.out.println(number1 + number2);
        break;
      case "SUB":
        System.out.println(number1 - number2);
        break;
      case "MUL":
        System.out.println(number1 * number2);
        break;
      case "DIV":
        if (number2 == 0) {
          System.out.println("Cannot divide by zero");
        } else {
          float result = (float) number1 / number2;
          System.out.println(result);
        }
        break;
      default:
        System.out.println("Incorrect option");
        break;
    }
  }
}
