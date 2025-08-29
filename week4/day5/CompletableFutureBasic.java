import java.util.concurrent.*;

public class CompletableFutureBasic {
  public static void main(String[] args) throws Exception {
    // Run async task
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
      return "Hello from CompletableFuture!";
    });

    // Non-blocking callback
    future.thenAccept(result -> System.out.println("Result: " + result));

    // Block main thread just to see output
    Thread.sleep(2000);
  }
}
