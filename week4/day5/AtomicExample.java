import java.util.concurrent.atomic.AtomicInteger;

public class AtomicExample {
  public static void main(String[] args) {
    AtomicInteger count = new AtomicInteger(2);

    count.incrementAndGet();
    System.out.println(count.get());
  }
}
