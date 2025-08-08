/* To understand the concept of the variable arugument in Java
  Author: Sujal Morwani
 * Created On: 08/08/2025
 */

public class VariableArguments {

  static void show(int... A) {
    for (int x : A) {
      System.out.println(x);
    }
  }

  static void showList(int start, String... S) {
    for (int i = 0; i < S.length; i++) {
      System.out.println(i + 1 + ". " + S[i]);
    }
  }

  public static void main(String[] args) {
    show();
    show(10, 20, 30);
    show(new int[] { 3, 4, 5, 6, 6, 7, 8, 9, 10 });
    showList(5, "Ram", "Shyam", "Ganshyam", "Ravi", "Laxman");
  }
}
