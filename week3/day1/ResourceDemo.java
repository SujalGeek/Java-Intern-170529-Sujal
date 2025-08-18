
/* To understand the concept of the finally and try with resorces in Java
 * Author: Sujal Morwani
 * Created On: 18/08/2025
 */
import java.io.*;
import java.util.*;

public class ResourceDemo {

  static void Divide() throws Exception {

    try (FileInputStream fi = new FileInputStream("C:\\Users\\USER\\Downloads\\python\\myjavatest.txt");
        Scanner sc = new Scanner(fi)) {
      int a = sc.nextInt();
      int b = sc.nextInt();
      int c = sc.nextInt();
      System.out.println(a / c);
    }
  }

  public static void main(String[] args) throws Exception {
    try {
      Divide();
    } catch (Exception e) {
      System.out.println(e);
    }
    // int x = sc.nextInt();
    // System.out.println(x);
  }
}
