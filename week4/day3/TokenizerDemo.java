/* To understand the concept of String Tokenizer in Java
 * Author: Sujal Morwani
 * Created On: 27/08/2025
 */

import java.io.FileInputStream;
import java.util.*;

public class TokenizerDemo {
  public static void main(String[] args) throws Exception {
    String data = "name=Vijay address=delhi country=india dept=ec";

    StringTokenizer stk = new StringTokenizer(data, "= ");
    while (stk.hasMoreTokens()) {
      System.out.println(stk.nextToken());
    }

    FileInputStream fis = new FileInputStream("Student.txt");
    byte b[] = new byte[fis.available()];
    fis.read(b);

    String data2 = new String(b);
    StringTokenizer str3 = new StringTokenizer(data2, "=");

    while (str3.hasMoreTokens()) {
      System.out.println(str3.nextToken());
    }

    StringTokenizer st2 = new StringTokenizer("Java,Python,C++", ",");
    while (st2.hasMoreElements()) {
      System.out.println(st2.nextToken());
    }

    // If i write del false means the del will not be printed
    // But if I write it as true then the delimeter also get printed

    StringTokenizer st3 = new StringTokenizer("A:B:C", ":", false);
    System.out.println("The total number of tokens: " + st3.countTokens());
    while (st3.hasMoreTokens()) {
      System.out.println(st3.nextToken());
    }

    /*
     * String Tokenezier
     * When you need fast parsing of strings into tokens.
     * When you don’t want to use regex (String.split() is regex-based and slower).
     * When working with legacy code where it’s already used.
     * 
     * Major implementation of the BitSet is used to get the common bits
     * and union of bits for major two purposes it
     */
  }
}
