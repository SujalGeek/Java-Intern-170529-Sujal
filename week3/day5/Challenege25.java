/* The challenege is to read from one file and write to the another file and converting
 * them into the lowercase using FileInput and fileOutputStream
 * Author: Sujal Morwani
 * Created On: 22/08/2025
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.SequenceInputStream;

public class Challenege25 {
  public static void main(String[] args) throws Exception {

    try (FileInputStream fis = new FileInputStream(
        "C:/Users/USER/Documents/Java-internship/week3/day5/Files/source1.txt")) {

      byte b[] = new byte[fis.available()];
      fis.read(b);
      String s = new String(b);
      System.out.println(s);
      String s2 = s.toLowerCase();

      FileOutputStream f2 = new FileOutputStream(
          "C:/Users/USER/Documents/Java-internship/week3/day5/Files/source2.txt");

      f2.write(s2.getBytes());
      System.out.println("Lowercase letters are written");

      try (
          FileInputStream fis1 = new FileInputStream(
              "C:/Users/USER/Documents/Java-internship/week3/day5/Files/source1.txt");
          FileInputStream fis2 = new FileInputStream(
              "C:/Users/USER/Documents/Java-internship/week3/day5/Files/source2.txt");
          SequenceInputStream sis = new SequenceInputStream(fis1, fis2);
          FileOutputStream dest = new FileOutputStream(
              "C:/Users/USER/Documents/Java-internship/week3/day5/Files/destination.txt")) {
        int data;
        while ((data = sis.read()) != -1) {
          dest.write((data)); // ensure lowercase
        }
        System.out.println("Both files merged into destination.txt (in lowercase).");
      }

      // 2nd method
      // String str[] = (String) b[];
      // int b2;
      // while ((b2 = fis.read()) != -1) {
      // if (b2 >= 65 && b2 <= 120) {
      // f2.write(b2 + 32);
      // }
      // }

      // fis.close();
      // f2.close();
    }

  }
}
