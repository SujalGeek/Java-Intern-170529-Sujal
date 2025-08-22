/* To understand the concept of Files 
 * Author: Sujal Morwani
 * Created on: 22/08/2025
 */

import java.io.*;

public class FileDemo {
  public static void main(String[] args) throws Exception {
    try (FileOutputStream fos = new FileOutputStream(
        "C:/Users/USER/Documents/Java-internship/week3/day5/Files/Test.txt")) {
      String str = "Hello Java Programming";
      // fos.write(str.getBytes());
      byte b[] = str.getBytes();
      fos.write(b);
      fos.close();
      // } catch (FileNotFoundException e) {
      // e.printStackTrace();
      // } catch (IOException e) {
      // System.out.println(e);
      // }
    }
  }
}
