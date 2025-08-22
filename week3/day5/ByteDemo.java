
/* To understand the concept of the BytesArray and CharArray in java
 * Author: Sujal Morwani 
 * Created At: 22/08/2025
 */
import java.io.*;

public class ByteDemo {
  public static void main(String[] args) throws Exception {

    byte b[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
    ByteArrayInputStream bis = new ByteArrayInputStream(b);
    // 1st method

    // int x;
    // while ((x = bis.read()) != -1) {
    // System.out.print((char) x + " ");
    // }

    // 2nd method
    String str = new String(bis.readAllBytes());
    System.out.println(str + " ");

    ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
    bos.write('a');
    bos.write('b');
    bos.write('c');
    bos.write('d');
    bos.write('e');

    bos.writeTo(new FileOutputStream("C:/Users/USER/Documents/Java-internship/week3/day5/Files/Test.txt"));
    System.out.println("Done with the written content");

    bis.close();
    bos.close();
  }
}
