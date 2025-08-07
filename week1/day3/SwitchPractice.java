
/* To understand the concept of switch case in java and how it works
 * task is to display the name of day based on number and display the name
 * of a month based on a number and display the type of website
 * Author: Sujal Morwani
 * Created On: 07/08/2025
 */
import java.util.*;

public class SwitchPractice {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the day number: ");
    int dayNumber = sc.nextInt();

    switch (dayNumber) {
      case 1:
        System.out.println("It is Monday");
        break;
      case 2:
        System.out.println("It is Tuesday");
        break;
      case 3:
        System.out.println("It is Wednesday");
        break;
      case 4:
        System.out.println("It is Thursday");
        break;
      case 5:
        System.out.println("It is Friday");
        break;
      case 6:
        System.out.println("It is Saturday");
        break;
      case 7:
        System.out.println("It is Sunday");
        break;
      default:
        System.out.println("Incorrect day number");
        break;
    }

    int monthNumber = sc.nextInt();
    switch (monthNumber) {
      case 1:
        System.out.println("January");
        break;
      case 2:
        System.out.println("February");
        break;
      case 3:
        System.out.println("March");
        break;
      case 4:
        System.out.println("April");
        break;
      case 5:
        System.out.println("May");
        break;
      case 6:
        System.out.println("June");
        break;
      case 7:
        System.out.println("July");
        break;
      case 8:
        System.out.println("August");
        break;
      case 9:
        System.out.println("September");
        break;
      case 10:
        System.out.println("October");
        break;
      case 11:
        System.out.println("November");
        break;
      case 12:
        System.out.println("December");
        break;
      default:
        System.out.println("Incorrect month number");
        break;
    }

    sc.nextLine();
    System.out.println("Enter the website: ");
    String organization = sc.nextLine();
    int i = organization.lastIndexOf(".");
    String organization1 = organization.substring(i + 1);
    System.out.println(organization1);
    switch (organization1) {
      case "com":
        System.out.println("It is com Organization");
        break;
      case "org":
        System.out.println("It is org Organization");
        break;
      case "net":
        System.out.println("It is net Organization");
        break;
      case "gov":
        System.out.println("It is gov Organization");
        break;
      default:
        System.out.println("It is incorrect organization");
        break;

    }
  }
}
