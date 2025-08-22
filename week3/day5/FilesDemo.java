/* To understand the concept of the Files in java
 * Author: Sujal Morwani
 * Created On: 22/08/2025
 */

import java.io.File;
import java.io.FileOutputStream;

public class FilesDemo {
  public static void main(String[] args) throws Exception {

    File f = new File("C:\\Users\\USER\\Documents\\Java-internship\\week3\\day5\\Files");
    System.out.println(f.isDirectory());

    File list[] = f.listFiles();
    for (File x : list) {
      System.out.print(x.getName() + " ");
      System.out.println(x.getCanonicalPath());
      System.out.println(x.getParent());

      File f1 = new File("C:\\Users\\USER\\Documents\\Java-internship\\week3\\day5\\Files\\Data1.txt");
      f1.setReadOnly();

      f1.setWritable(true);
      System.out.println(f1.lastModified());
      FileOutputStream fos = new FileOutputStream(
          "C:\\\\Users\\\\USER\\\\Documents\\\\Java-internship\\\\week3\\\\day5\\\\Files\\Data1.txt");

    }
  }
}
