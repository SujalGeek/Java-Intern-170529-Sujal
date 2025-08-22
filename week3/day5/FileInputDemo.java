/* To understand the concept of File Input Stream in Java
 * Author: Sujal Morwani
 * Created On: 22/08/2025
 */

import java.io.FileInputStream;

public class FileInputDemo {
  public static void main(String[] args) throws Exception {

    try (FileInputStream fis = new FileInputStream(
        "C:/Users/USER/Documents/Java-internship/week3/day5/Files/Test.txt")) {
      // 1 method

      // byte b[] = new byte[fis.available()];
      // fis.read(b);
      // String str = new String(b);
      // System.out.println(str);

      // 2nd method
      // int x;
      // do {
      // x = fis.read();
      // if (x != -1)
      // System.out.print((char) x);
      // } while (x != -1);
      // }

      // 3rd method

      int x;
      while ((x = fis.read()) != -1) {
        System.out.println((char) x);
      }
    }
  }
}
