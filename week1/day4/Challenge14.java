/*  To undertand the copying of an array
 * and reverse the copying array and increasing the size of array
 * Authot: Sujal Morwani
 * Created On: 08/08/2025
 */

public class Challenge14 {
  public static void main(String[] args) {
    int A[] = { 1, 2, 3, 4, 5 };
    int B[] = new int[A.length];
    for (int i = 0; i < A.length; i++) {
      B[i] = A[i];
    }
    for (int x : B) {
      System.out.print(x + " ");
    }
    System.out.println();
    int C[] = { 2, 3, 4, 5, 6 };
    int D[] = new int[C.length];

    for (int j = C.length - 1; j >= 0; j--) {
      D[j] = C[j];
    }
    for (int x : D) {
      System.out.print(x + " ");
    }
    System.out.println();
    int left = 0;
    int right = D.length - 1;
    System.out.println("Reversing an array");
    while (left <= right) {
      int temp = D[left];
      D[left] = D[right];
      D[right] = temp;
      left++;
      right--;
    }
    for (int x : D) {
      System.out.print(x + " ");
    }
    System.out.println();
  }
}
