/* To understand the concept of the BufferedInputStream and how it is different from 
 * FileInputStream and also BufferedOutputStream and they works same as FileOutputStream
 * Author: Sujal Morwani
 * Created On: 22/08/2025
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;

public class BufferedDemo {
  public static void main(String[] args) throws Exception {

    FileReader fis = new FileReader("C:/Users/USER/Documents/Java-internship/week3/day5/Files/Test.txt");
    BufferedReader bis = new BufferedReader(fis);

    System.out.println("File: " + fis.markSupported());
    System.out.println("Buffer: " + bis.markSupported());

    // Why buffer returning the true
    // Keep the data in the buffer and it supported the mark and reset and not
    // supported this
    // files supported like the audiotapes header will just move forwards
    // You cannot move read backward
    // If you want to move backwards then needed the Buffered InputStream

    System.out.println((char) bis.read());
    System.out.println((char) bis.read());
    System.out.println((char) bis.read());
    bis.mark(10);
    System.out.println((char) bis.read());
    System.out.println((char) bis.read());
    bis.reset();
    // This is the proof that buffered input is holding the data(can go backward and
    // read it)
    System.out.println((char) bis.read());
    System.out.println((char) bis.read());

    // If you have ascii codes then u can use the bytes
    // buffered input stream
    // FileReader fis = new
    // FileReader("C:/Users/USER/Documents/Java-internship/week3/day5/Files/Test.txt");
    System.out.println("String " + bis.readLine());
    bis.close();
    fis.close();
  }
}
