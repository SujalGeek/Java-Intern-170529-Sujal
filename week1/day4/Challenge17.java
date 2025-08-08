/* The task is to find the maximum of numbers using var args, and other
 * sum of all elements using var args and calculate the discount using the var args
 * Author: Sujal Morwani
 * Created On: 08/08/2025
 */

public class Challenge17 {
  static int max(int... A) {
    if (A.length == 0) {
      return Integer.MIN_VALUE;
    }
    int max = A[0];
    for (int i = 1; i < A.length; i++) {
      if (A[i] > max) {
        max = A[i];
      }
    }
    return max;
  }

  static int sum(int... A) {
    int sum = 0;
    for (int i = 1; i < A.length; i++) {
      sum = sum + A[i];
    }
    return sum;
  }

  static double sum1(double... P) {
    double sum = 0;
    for (int i = 0; i < P.length; i++) {
      sum += P[i];
    }

    if (sum < 500) {
      return sum * 0.10;
    } else if (sum >= 500 && sum <= 1000) {
      return sum * 0.15;
    } else {
      return sum * 0.20;
    }
  }

  public static void main(String[] args) {
    System.out.println(max());
    System.out.println(max(10, 20));
    System.out.println(max(10, 20, 30, 40));

    System.out.println(sum(10, 20));
    System.out.println(sum1(10, 20, 30));
  }
}
