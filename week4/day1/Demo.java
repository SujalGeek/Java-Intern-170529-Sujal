/* To understand the concept using the challenge here is the challenge
 * take the array of float DataOutputStream  and again read the data from Data.txt and 
 * and display in the console use the file output stream and data input stream
 * Author: Sujal Morwani
 * Created On: 25/08/2025
 */

// file output stream data output stream se float hi pass on
// read the data from data.txt using file input and data input 
// firstly count the numbers and then store the data if there is bytes then you can read -1

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

class List2 {
  float list2[] = { 10.5f, 65.35f, 56.67f, 67.89f };

  public int display() {
    return list2.length;
  }
}

public class Demo {
  public static void main(String[] args) throws Exception {

    // ---------- Writing ----------
    FileOutputStream fos = new FileOutputStream("StudentChallenge.txt");
    DataOutputStream dos = new DataOutputStream(fos);

    List2 l2 = new List2();
    int m = l2.display();

    // write the count first
    dos.writeInt(m);

    // write all floats
    for (int i = 0; i < l2.list2.length; i++) {
      dos.writeFloat(l2.list2[i]);
    }

    dos.close();
    fos.close();

    // ---------- Reading ----------
    FileInputStream fis = new FileInputStream("StudentChallenge.txt");
    DataInputStream dis = new DataInputStream(fis);

    int count = dis.readInt(); // read how many floats
    System.out.println("Total floats = " + count);

    for (int j = 0; j < count; j++) {
      float value = dis.readFloat();
      System.out.println("Value " + (j + 1) + " : " + value);
    }

    dis.close();
    fis.close();
  }
}
