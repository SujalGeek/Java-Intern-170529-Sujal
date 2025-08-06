
/* Here in this challenge one need to find the roots of the quadratic equation
 * find their roots should be real numbers
 * Author: Sujal Morwani
 * Created On: 06/08/2025
 */
import java.util.*;

public class Challenge2 {
  public static void main(String[] args) {
    // The quadratic equation ax^2 + bx +c = 0
    // Let get their coefficents and constant c
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the value of a(coefficient of x^2): ");
    int a = sc.nextInt();
    System.out.println("Enter the value of b(coefficient of x): ");
    int b = sc.nextInt();
    System.out.println("Enter the value of c(constant): ");
    int c = sc.nextInt();

    double root1 = (-b + Math.sqrt((b * b) - (4 * a * c))) / (2 * a);
    double root2 = (-b - Math.sqrt((b * b) - (4 * a * c))) / (2 * a);

    System.out.println("The roots of the quadratic equation are: " + root1 + " and " + root2);
    sc.close();
  }
}
