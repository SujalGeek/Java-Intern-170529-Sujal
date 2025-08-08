/* To understand the concept of array of two deminsional array
* addition of matrices and multiplication of two matrices
 * Author: Sujal Morwani
 * Created On: 08/08/2025
 */

public class TwoDimensionalArray {
  public static void main(String[] args) {
    int A[][] = new int[5][5];
    int B[][] = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
    for (int i = 0; i < B.length; i++) {
      for (int j = 0; j < B[0].length; j++) {
        System.out.print(B[i][j] + " ");
      }
      System.out.println("");

    }
    for (int x[] : B) {
      for (int y : x) {
        System.out.print(y + " ");
      }
      System.out.println("");
    }

    // Adding 2 matrices
    int C[][] = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
    int D[][] = new int[3][3];
    for (int i = 0; i < B.length; i++) {
      for (int j = 0; j < C.length; j++) {
        D[i][j] = B[i][j] + C[i][j];
      }
    }
    for (int y[] : D) {
      for (int z : y) {
        System.out.print(z + " ");
      }
      System.out.println("");
    }
    int E[][] = new int[3][3];
    for (int i = 0; i < B.length; i++) {
      for (int j = 0; j < C.length; j++) {
        E[i][j] = B[i][j] * C[i][j];
      }
    }
    for (int z[] : E) {
      for (int a : z) {
        System.out.print(a + " ");
      }
      System.out.println("");
    }
  }
}
