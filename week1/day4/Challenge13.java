/* the task is to understand the rotation of array and 
 * inserting an element in the array 
 * Author: Sujal Morwani
 * Created On: 08/08/2025
 */

public class Challenge13 {
  public static void main(String[] args) {
    int A[] = { 1, 2, 3, 4, 5 };
    int temp = A[0];
    for (int i = 0; i < A.length; i++) {
      A[i - 1] = A[i];
    }
    A[A.length - 1] = temp;
    for (int x : A) {
      System.out.print(x + " ");
    }
    System.out.println();
    int B[] = { 1, 2, 3, 4, 5 };
    B[0] = temp;
    for (int i = B.length; i > 0; i--) {
      // right rotation
      B[i] = B[i - 1];
    }
    for (int x : B) {
      System.out.print(x + " ");
    }

    int C[] = new int[10];
    C[0] = 1;
    C[1] = 2;
    C[2] = 3;
    C[3] = 4;
    C[4] = 5;
    C[5] = 6;

    int n = 6;
    for (int i = 0; i < n; i++) {
      System.out.print(C[i] + " ");
      System.out.println("");

    }
    int x = 20;
    int index = 2;

    for (int i = n; i > index; i--) {
      C[i] = C[i - 1];
      C[index] = x;
    }
    for (int i = 0; i < C.length; i++) {
      System.out.print(C[i] + " ");
    }

  }
}
