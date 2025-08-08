/* To understand the concept of Methods in java
* In Java it is called as Parameter Passing
  Author: Sujal Morwani
 * Created On: 08/08/2025
 */

import java.util.*;

public class MethodPractice {
  static int max(int x, int y) {
    if (x > y) {
      return x;
    } else {
      return y;
    }
  }

  static void change1(int A[], int index, int value) {
    A[index] = value;
  }

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the values: ");
    int a = sc.nextInt();
    int b = sc.nextInt();
    // make the static so this line is can be used
    System.out.println(max(a, b));
    System.out.println();
    int A[] = { 2, 4, 6, 8, 10 };

    change1(A, 2, 20);
    for (int x : A) {
      System.out.print(x + " ");
    }
    System.out.println();
    sc.close();
  }
}
