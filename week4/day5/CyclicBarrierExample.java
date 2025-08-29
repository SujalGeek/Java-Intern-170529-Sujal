import java.util.concurrent.*;

public class CyclicBarrierExample {
  public static void main(String[] args) {
    int players = 3;
    CyclicBarrier barrier = new CyclicBarrier(players, () -> {
      System.out.println("All players are ready. Game starts!");
    });

    ExecutorService executor = Executors.newFixedThreadPool(players);

    for (int i = 1; i <= players; i++) {
      int id = i;
      executor.submit(() -> {
        System.out.println("Player " + id + " is preparing...");
        try {
          Thread.sleep(1000 * id);
        } catch (InterruptedException e) {
        }
        try {
          System.out.println("Player " + id + " is ready!");
          barrier.await(); // wait for others
        } catch (Exception e) {
        }
      });
    }

    executor.shutdown();
  }
}
