/* To understand the concept of Strings and how to get the format specified 
   output
 * Author: Sujal Morwani
 * Created On: 06/08/2025
 */

public class Strings {
  public static void main(String[] args) {
    // Using println() the cursor will point towards new line
    System.out.println("Hello world");
    // using print the cursor will point towards that end of the word only
    System.out.print("Hello world");
    // using the print we can format the output and use it
    int x = 10;
    float y = 12.34f;
    char z = 'A';
    String str = "Java Code";

    System.out.printf("Hello %d %.2f %c \n", x, y, z);
    System.out.printf("Hello %x %e %c \n", x, y, z);
    // According to the arguments like we have to print
    System.out.printf("%3$s %2$f %1$d \n", x, y, str);

    // String practice
    // Storing the literal in String pool
    // str1 is the reference to this one
    String str1 = "Java Programming";
    System.out.println(str1);

    // the string created in the heap area and reference holding the object
    String str2 = new String("Java Programming");
    System.out.println(str2);

    System.out.println("Checking if the string is same or not: " + (str1 == str2));
    char c[] = { 'a', 'e', 'i', 'o', 'u' };

    String str3 = new String(c);
    System.out.println(str3);
    if (str2.equals(str3)) {
      System.out.println("Equal");
    } else {
      System.out.println("Not equal");
    }
    byte b[] = { 64, 76, 74, 32, 21 };
    String str4 = new String(b, 1, 2);
    System.out.println(str4);
  }
}
