
/* The task is to find the ap series and gp series
 * and need to find the fibancoi series
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */
import java.util.*;

public class Challenge12 {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Program to find the AP Series");
    System.out.println("Enter the value of a,d and n: ");
    int a = sc.nextInt();
    int d = sc.nextInt();
    int n = sc.nextInt();
    for (int i = 1; i <= n; i++) {
      System.out.print(a + ", ");
      a = a + d;
    }
    sc.nextLine();
    System.out.println("Program to find the GP Series");
    System.out.println("Enter the values of a,r and n: ");
    int b = sc.nextInt();
    int r = sc.nextInt();
    int n1 = sc.nextInt();

    for (int i = 1; i <= n1; i++) {
      System.out.print(b + ", ");
      b = b * r;
    }

    // Fibancoi series using loop
    System.out.println("Program of Fibancoi Series");
    System.out.println("Enter the number of terms: ");
    int number4 = sc.nextInt();
    int a1 = 0;
    int b1 = 1;
    int c;
    for (int i = 0; i < number4 - 2; i++) {
      c = a1 + b1;
      System.out.print(c + ", ");
      a1 = b1;
      b1 = c;
    }
  }
}
