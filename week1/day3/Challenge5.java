/* The challenge is there to find if a number or not
 * and then other one is find if a number is hexa decimal or not
 * and other is that the data is in Date format ot not(dd/mm/yyyy)
 * and remove the special characters and trim the spaces before 
 * and after the name and count the words after that
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */

public class Challenge5 {
  public static void main(String[] args) {
    int binary_number = 1011000;
    String str1 = binary_number + "";
    System.out.println(str1.matches("[01]+"));

    String str2 = "225AFE";
    System.out.println(str2.matches("[0-9A-F]+"));

    String date = "07/08/2025";
    System.out.println(date.matches("[0-3][0-9]/[01][0-9]/[0-9]{4}"));

    // remove the special characters
    String str3 = "abc@@#443$!()";
    System.out.println(str3.replaceAll("[^a-zA-Z0-9]", ""));

    // removing the extra spaces from the string;
    String str4 = "    sujal           morwani      ";
    // str4.trim();
    System.out.println(str4.replaceAll("\\s+", " ").trim());

    // Find the number of words in a String
    String str5 = "abc    de    fgh    ijk";
    System.out.println(str5.split("\\s+").length);
  }
}
