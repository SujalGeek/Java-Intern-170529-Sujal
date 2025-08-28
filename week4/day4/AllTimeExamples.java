import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class AllTimeExamples {
  public static void main(String[] args) {

    // 1. LocalDate -> Only date (YYYY-MM-DD), no time, no zone
    LocalDate today = LocalDate.now();
    System.out.println("LocalDate (today): " + today); // e.g., 2025-08-21

    LocalDate specificDate = LocalDate.of(2024, 12, 25);
    System.out.println("LocalDate (specific): " + specificDate); // 2024-12-25

    // 2. LocalTime -> Only time (HH:mm:ss), no date, no zone
    LocalTime nowTime = LocalTime.now();
    System.out.println("LocalTime (now): " + nowTime); // e.g., 13:45:30.123456789

    LocalTime specificTime = LocalTime.of(10, 30, 45);
    System.out.println("LocalTime (specific): " + specificTime); // 10:30:45

    // 3. LocalDateTime -> Date + Time, no zone
    LocalDateTime dateTime = LocalDateTime.now();
    System.out.println("LocalDateTime: " + dateTime); // e.g., 2025-08-21T13:46:12.567

    // 4. ZonedDateTime -> Date + Time + Zone
    ZonedDateTime zoned = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
    System.out.println("ZonedDateTime (IST): " + zoned); // with zone offset +05:30

    ZonedDateTime zonedNY = ZonedDateTime.now(ZoneId.of("America/New_York"));
    System.out.println("ZonedDateTime (NY): " + zonedNY); // with zone offset -04:00 or -05:00

    // 5. OffsetDateTime -> Date + Time + fixed offset (not full zone rules)
    OffsetDateTime offset = OffsetDateTime.now(ZoneOffset.ofHours(5));
    System.out.println("OffsetDateTime (+05:00): " + offset);

    // 6. Instant -> Timestamp (machine time, UTC)
    Instant instant = Instant.now();
    System.out.println("Instant (UTC): " + instant); // e.g., 2025-08-21T08:16:15.123Z

    // 7. Duration -> Amount of time (for hours, minutes, seconds)
    Duration duration = Duration.ofHours(5);
    System.out.println("Duration (5 hours): " + duration); // PT5H

    // Difference between two times
    Duration betweenTime = Duration.between(LocalTime.of(9, 0), LocalTime.of(12, 30));
    System.out.println("Duration (between times): " + betweenTime); // PT3H30M

    // 8. Period -> Date-based amount of time (years, months, days)
    Period period = Period.of(1, 2, 15);
    System.out.println("Period (1Y2M15D): " + period);

    // Difference between two dates
    Period betweenDates = Period.between(LocalDate.of(2020, 1, 1), LocalDate.of(2025, 8, 21));
    System.out.println("Period (between dates): " + betweenDates); // P5Y7M20D

    // 9. Year, YearMonth, MonthDay
    Year currentYear = Year.now();
    System.out.println("Year: " + currentYear); // e.g., 2025

    YearMonth yearMonth = YearMonth.now();
    System.out.println("YearMonth: " + yearMonth); // 2025-08

    MonthDay monthDay = MonthDay.of(12, 25);
    System.out.println("MonthDay: " + monthDay); // --12-25

    // 10. Formatting with DateTimeFormatter
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    String formatted = dateTime.format(formatter);
    System.out.println("Formatted LocalDateTime: " + formatted); // e.g., 21-08-2025 13:50:20

    // Parsing from String to LocalDate
    LocalDate parsedDate = LocalDate.parse("2025-01-15");
    System.out.println("Parsed LocalDate: " + parsedDate); // 2025-01-15

    // 11. ChronoUnit example (for difference)
    long daysBetween = ChronoUnit.DAYS.between(LocalDate.of(2025, 1, 1), today);
    System.out.println("Days between 2025-01-01 and today: " + daysBetween);

    // 12. Clock -> Current time with time-zone
    Clock clock = Clock.systemUTC();
    System.out.println("Clock (UTC): " + clock.instant());
  }
}
