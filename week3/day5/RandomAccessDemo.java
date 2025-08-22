/* To understand the concept of RandomAccess in Java
 * Author: Sujal Morwani
 * Created On: 22/08/2025
 */

import java.io.RandomAccessFile;

public class RandomAccessDemo {
  public static void main(String[] args) throws Exception {

    RandomAccessFile r1 = new RandomAccessFile(
        "C:\\Users\\USER\\Documents\\Java-internship\\week3\\day5\\Files\\Data.txt",
        "rw");

    System.out.println((char) r1.read());
    System.out.println((char) r1.read());
    System.out.println((char) r1.read());
    r1.write('d');
    System.out.println((char) r1.read());
    r1.skipBytes(3);
    System.out.println((char) r1.read());
    r1.seek(1);
    System.out.println((char) r1.read());
    System.out.println(r1.getFilePointer());
    r1.seek(r1.getFilePointer() + 3);
    System.out.println((char) r1.read());
  }
}
