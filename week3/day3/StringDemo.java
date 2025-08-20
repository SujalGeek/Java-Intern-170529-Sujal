/* To understand the concepts of String , StringBuffer and StringBuilder in Java 
 * and they are different from each other
 * Author: Sujal Morwani
 * Created On: 20/08/2025
 */

public class StringDemo {
  public static void main(String[] args) {

    String s1 = new String("Hello");
    StringBuffer s2 = new StringBuffer("Hello");
    StringBuilder s3 = new StringBuilder("Hello");

    s1.concat(" World");
    s2.append(" World");
    s3.append(" World");

    System.out.println(s1);
    System.out.println(s2);
    System.out.println(s3);
  }
}
