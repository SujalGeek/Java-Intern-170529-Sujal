
/* Here in this challenge need to get the area of a traingle
 * using the base and height first method and other method need to get the
 * area using the semi permieter formula
 * Author: Sujal Morwani
 * Created On: 06/08/2025
 */
import java.util.*;

public class Challenge1 {
  public static void main(String[] args) {

    // First method
    // Taking the inputs from the user
    Scanner sc = new Scanner(System.in);
    // Taking the base input
    System.out.println("Enter the base: ");
    int base = sc.nextInt();
    // Taking the height as input
    System.out.println("Enter the height: ");
    int height = sc.nextInt();

    // Storing the result in the float using the formula 1/2*(base*height)
    float result = 1 / 2f * (base * height);
    System.out.println("Using the formula 1 the result we got: " + result);

    // Second method
    // Getting the input as sides
    System.out.println("Enter the first side: ");
    int a = sc.nextInt();
    System.out.println("Enter the second side: ");
    int b = sc.nextInt();
    System.out.println("Enter the third side: ");
    int c = sc.nextInt();

    // Need to calcuate the semi permeter
    float s = 1 / 2f * (a + b + c);
    // Need to calculate the area
    double area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
    System.out.println("The result we got after second method: " + area);
    sc.close();
  }
}
