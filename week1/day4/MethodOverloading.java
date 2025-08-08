/* To understand the concept of the method Overloading
 * Author: Sujal Morwani
 *  Created On: 08/08/2025
 */

public class MethodOverloading {
  static int max(int x, int y) {
    if (x > y) {
      return x;
    } else {
      return y;
    }
  }

  static int max(int a, int b, int c) {
    if (a > b && a > c) {
      return a;
    } else if (b > c) {
      return b;
    } else {
      return c;
    }
  }

  static float max(float a, float b) {
    if (a > b) {
      return a;
    } else {
      return b;
    }
  }

  public static void main(String[] args) {
    int x = 5;
    int y = 6;
    int z = 2121;
    int intResult = max(x, y);
    System.out.println(intResult);
    int intResultOfThreeNumbers = max(x, y, z);
    System.out.println(intResultOfThreeNumbers);
    float result = max(intResult, intResultOfThreeNumbers);
    System.out.println(result);
  }
}
