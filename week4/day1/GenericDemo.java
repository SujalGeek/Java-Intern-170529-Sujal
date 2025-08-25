/* To understand the concept of Generics in Java
 * Author: Sujal Morwani
 * Created On: 25/08/2025
 */

class Data<T> {
  // private T obj;

  // public void setData(T v) {
  // obj = v;
  // }

  // public T getData() {
  // return obj;
  // }
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

public class GenericDemo {
  public static void main(String[] args) {

    // Data<Integer> d = new Data<>();
    // d.setData(20);
    // // d.setData("Hi");
    // System.out.println(d.getData());
    // Data<String> st2 = new Data<>();
    // st2.setData("Hi");
    // System.out.println(st2.getData());

    Data<Integer> ma = new Data<>();
    ma.append(20);
    ma.append(10);
    ma.append(30);
    ma.display();
  }
}
