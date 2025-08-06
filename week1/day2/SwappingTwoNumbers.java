
/* To undertand how the swapping can works with the bitwise operators
 * Author: Sujal Morwani
 * Created On: 06/08/2025
 */
import java.util.*;

public class SwappingTwoNumbers {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the first number: ");
    int number1 = sc.nextInt();
    System.out.println("Enter the second number: ");
    int number2 = sc.nextInt();

    System.out.println("Before swapping two numbers: " + number1 + " " + number2);
    number1 = number1 ^ number2;
    number2 = number1 ^ number2;
    number1 = number1 ^ number2;
    System.out.println("After swapping two numbers: " + number1 + " " + number2);

    // Performing masking
    byte number3 = 9;
    byte number4 = 12;
    byte mask;
    mask = (byte) (number3 << 4);
    mask = (byte) (mask | number4);
    int upper = (mask & 0b11110000) >> 4;
    System.out.println(upper);
    sc.close();
  }
}
