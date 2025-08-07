
/* The task is to display a number in words even with the tailing 0
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */
import java.util.*;

public class Challenge11 {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    int n = sc.nextInt();
    String s = "";
    while (n > 0) {
      int remainder = n % 10;
      n = n / 10;
      s = s + remainder;
    }
    System.out.println(s);
    char c;
    for (int i = s.length() - 1; i >= 0; i--) {
      c = s.charAt(i);

      switch (c) {
        case '0':
          System.out.println("Zero");
          break;
        case '1':
          System.out.println("One");
          break;
        case '2':
          System.out.println("Two");
          break;
        case '3':
          System.out.println("Three");
          break;
        case '4':
          System.out.println("Four");
          break;
        case '5':
          System.out.println("Five");
          break;
        case '6':
          System.out.println("Six");
          break;
        case '7':
          System.out.println("Seven");
          break;
        case '8':
          System.out.println("Eight");
          break;

        case '9':
          System.out.println("Nine");
          break;
        default:
          System.out.println("Incorrect number");
          break;
      }
    }
  }
}
