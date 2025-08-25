/* To understand the Java Generics
 * Author: Sujal Morwani
 * Created On: 25/08/2025
 */

class MyArray<T extends Number> {
  @SuppressWarnings("unchecked")
  T t1[] = (T[]) new Object[10];
  int length = 0;

  public void append(T value) {
    t1[length++] = value;
  }

  public void display() {
    for (int i = 0; i < length; i++) {
      System.out.println(t1[i]);
    }
  }
}

class MyArray2<T> extends MyArray {

}

public class GenericDemo1 {
  public static void main(String[] args) {

    MyArray2<Integer> t2 = new MyArray2<>();

  }
}
