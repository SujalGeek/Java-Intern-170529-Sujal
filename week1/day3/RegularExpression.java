
/* To undertand how the regular expression works in java 
 * and how the strings are being matched
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */
import java.util.*;

public class RegularExpression {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the string: ");
    String str1 = sc.nextLine();

    // These are for checking the alphabet of it matches or not but only takes one
    // character
    System.out.println("String is being matches or not with alphabets: " + str1.matches("[a-z]"));
    System.out.println("String is being matched or no(if the string exist of one character): " + str1.matches("."));

    String str2 = sc.nextLine();
    System.out.println("After checking the string is exists for single character: " + str2.matches("."));

    // if the string expect abc other than that it will return true
    String str3 = sc.nextLine();
    System.out
        .println("If the string starts with abc then it will return true otherwise false(only for one char): "
            + str3.matches("[^abc]"));

    String str5 = sc.nextLine();
    System.out.println("Start with A to Z alphabet followed by number: " + str5.matches("[A-Z][0-9]"));

    // All these for the single character of it matches or nota
    String str4 = "a";
    System.out.println("To check it is alphabet or digit: " + str4.matches("\\w"));
    System.out.println("To check only for digit: " + str4.matches("\\d"));
    System.out.println("To check if it is not a digit: " + str4.matches("\\D"));
    System.out.println("To check if is space : " + str4.matches("\\s"));
    System.out.println("To check if it is not a space: " + str4.matches("\\S"));

    // Now we can check the whole string
    String str6 = sc.nextLine();
    System.out.println("Any string is accepted here: " + str6.matches(".*"));
    String str7 = "aaaabbbbcccaaaa";
    System.out.println("Any number of time a or b or c: " + str7.matches("[abc]*"));
    String str8 = "abc@gmail.com";
    System.out.println(str8.matches(".*@gmail.com"));

  }

}
