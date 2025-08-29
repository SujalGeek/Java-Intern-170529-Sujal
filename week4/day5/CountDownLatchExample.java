import java.util.concurrent.*;

public class CountDownLatchExample {
  public static void main(String[] args) throws InterruptedException {
    int workers = 3;
    CountDownLatch latch = new CountDownLatch(workers);

    ExecutorService executor = Executors.newFixedThreadPool(workers);

    for (int i = 1; i <= workers; i++) {
      int id = i;
      executor.submit(() -> {
        System.out.println("Worker " + id + " is working...");
        try {
          Thread.sleep(1000 * id);
        } catch (InterruptedException e) {
        }
        System.out.println("Worker " + id + " finished!");
        latch.countDown(); // reduce the count
      });
    }

    latch.await(); // main thread waits until count reaches 0
    System.out.println("All workers are done, proceeding...");

    executor.shutdown();
  }
}
