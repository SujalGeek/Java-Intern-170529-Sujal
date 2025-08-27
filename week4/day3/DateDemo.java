/* To understand the concept of Date class in Java
 * Author: Sujal Morwani
 * Created On: 27/08/2025
 */

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DateDemo {
  public static void main(String[] args) {
    Date d1 = new Date();
    // d1.setHours(21);
    System.out.println(d1);

    LocalDate dt = LocalDate.now();
    System.out.println(dt);

    // This is the another method to get the proper date in yyyy-mm-dd
    System.out.println("Another method of getting the proper date using the Timezone");

    LocalDate d2 = LocalDate.now(Clock.systemDefaultZone());
    System.out.println(d2);

    System.out.println("In case you are knowing about the Time Zone then also we get the date of today's");
    LocalDate d3 = LocalDate.now(ZoneId.of("Asia/Kolkata"));
    System.out.println(d3);

    System.out.println("Using the of method to get the date");
    // Suppose if we have to find the other date
    LocalDate d4 = LocalDate.of(2025, 8, 25);
    System.out.println(d4);

    // Like now the starting of that day the Java is there
    LocalDate d5 = LocalDate.EPOCH;
    System.out.println(d5);

    LocalDate d6 = LocalDate.ofEpochDay(1000);
    System.out.println(d6);

    System.out.println("You can parse on the date");
    LocalDate d7 = LocalDate.parse("2025-01-06");
    System.out.println(d7);

    // If you need to modify the date in that
    // these are immutable and need to get that
    // They are not mutable thats why you have to store in new LocalDate and time
    System.out.println("After updating the date!!");
    LocalDate l12 = LocalDate.parse("2025-09-01");
    LocalDate l13 = l12.plusMonths(6);
    System.out.println(l13);
  }
}
