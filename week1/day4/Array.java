/* To understand the concept of Array and task to find the sum of all the 
* elements and other task to search an element in the array
* and other task is find the maximum element
  * Author: Sujal Morwani
 * Created On: 08/08/2025
 */
public class Array {
  public static void main(String[] args) {
    int A[] = new int[5];

    for (int i = 0; i < A.length; i++) {
      A[i] = i * 10;
      System.out.println(A[i]);
    }
    A[0] = 3;
    A[1] = 4;
    A[2] = 5;
    A[3] = 6;
    A[4] = 7;
    for (int y : A) {
      System.out.print(y + " ");
    }
    System.out.println();
    // for each loop
    int B[] = { 2, 4, 6, 8, 10 };
    for (int x : B) {
      System.out.print(x + " ");
    }

    // find sum of all elements
    int sum = 0;
    for (int i = 0; i < A.length; i++) {
      sum += A[i];
    }
    System.out.println("Sum of the array is: " + sum);
    int target = 4;
    for (int i = 0; i < A.length; i++) {
      if (target == A[i]) {
        System.out.println("Element found at index: " + i);
        System.exit(0);
      }
    }
    System.out.println("Element not found");
    int max = A[0];
    for (int i = 0; i < A.length; i++) {
      if (max < A[i]) {
        max = A[i];
      }
    }
    System.out.println("Maximum element is: " + max);

    int max1 = A[0];
    int max2 = -1;

    for (int i = 0; i < A.length; i++) {
      if (max1 < A[i]) {
        max2 = max1;
        max1 = A[i];
      }
      if (max2 < A[i] && max != A[i]) {
        max2 = A[i];
      }

    }
    System.out.println("The second largest element is: " + max2);

  }
}
