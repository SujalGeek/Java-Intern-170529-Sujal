
/* Here is the task to radix of a number 
 * and find the given year is leap year or not
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */
import java.util.*;

public class Challenge7 {
  public static void main(String[] args) {
    // radix
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the string: ");
    String str1 = sc.nextLine();
    // binary one
    // System.out.println(str1.matches("[01]+"));
    if (str1.matches("[01]+")) {
      System.out.println("It is binary with radix=2");
    } else if (str1.matches("[0-7]+")) {
      System.out.println("It is octal with radix=8");
    } else if (str1.matches("[0-9]+")) {
      System.out.println("It is decimal with radix=10");
    } else if (str1.matches("[0-9A-F]+")) {
      System.out.println("It is hexadecimal with radix=16");
    } else {
      System.out.println("It is not a number");
    }
    // print the radix
    // System.out.println(str1.matches("[0-7]+"));
    // System.out.println(str1.matches("[0-9]+"));

    // To check if it is leap year or not

    Scanner sc1 = new Scanner(System.in);
    System.out.println("Enter the year: ");
    int year = sc1.nextInt();
    if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
      System.out.println("It is a leap year");
    } else {
      System.out.println("It is not a leap year");
    }
    sc.close();
    sc1.close();
  }
}
