/* Here we have task to separate the programmer and gmail.com
 * by using various String methods
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */

public class Challenge4 {
  public static void main(String[] args) {
    String str1 = "programmer@gmail.com";
    // Finding the email id is on gmail or not
    if (str1.matches("\\w+.*@gmail.com")) {
      System.out.println("It contains gmail.com");
    }
    // Find the username and domain name form email
    int i = str1.indexOf("@");
    String username = str1.substring(0, i);
    String domainName = str1.substring(i + 1, str1.length());
    System.out.println("The username is: " + username);
    System.out.println("The domain name is: " + domainName);
  }
}
