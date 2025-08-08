
/* The task is to find a number is prime or not and other one is 
 * find the gcd of two number 
 * Author: Sujal Morwani
 * Created On: 08/08/2025
 */
import java.util.*;

public class Challenge15 {
  static boolean checkPrime(int a) {
    int n = a;
    for (int i = 2; i <= n / 2; i++) {
      if (n % i == 0) {
        return false;
      }
    }
    return true;
  }

  static int gcdNumber(int a, int b) {
    int gcd = 1;
    if (a > b) {
      gcd = a - b;
    } else {
      gcd = b - a;
    }
    return gcd;

  }

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    int number1 = sc.nextInt();
    boolean result = checkPrime(number1);
    if (result) {
      System.out.println("It is prime number");
    } else {
      System.out.println("It is composite number");
    }

    int result2 = gcdNumber(6, 8);
    System.out.println(result2);
  }
}
