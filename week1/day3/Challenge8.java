
/* Here is the task to display the name of a day based on the number
 * and second one is to find type of website and the protocol used
 * these are tasks need to be performed
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */
import java.util.*;

public class Challenge8 {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the day Number: ");
    int dayNumber = sc.nextInt();

    if (dayNumber == 1) {
      System.out.println("It is Monday");
    } else if (dayNumber == 2) {
      System.out.println("It is Tuesday");
    } else if (dayNumber == 3) {
      System.out.println("It is Wednesday");
    } else if (dayNumber == 4) {
      System.out.println("It is Thursday");
    } else if (dayNumber == 5) {
      System.out.println("It is Friday");
    } else if (dayNumber == 6) {
      System.out.println("It is Saturday");
    } else if (dayNumber == 7) {
      System.out.println("It is Sunday");
    } else {
      System.out.println("Incorrect DayNumber");
    }
    String str1 = "http://www.google.com";
    int i = str1.indexOf(":");
    String protocol = str1.substring(0, i);
    if (protocol.matches("http")) {
      System.out.println("It is a Http protocol");
    } else if (protocol.matches("ftp")) {
      System.out.println("It is ftp protocal");
    } else {
      System.out.println("It is not protocol");
    }
    int j = str1.lastIndexOf(".");
    String domainName = str1.substring(j + 1);
    if (domainName.matches("com")) {
      System.out.println("It is a com domain");
    } else if (domainName.matches("org")) {
      System.out.println("It is a org domain");
    } else if (domainName.matches("net")) {
      System.out.println("It is a net domain");
    } else {
      System.out.println("It is not a domain");
    }
    sc.close();
  }
}
