/* To understand the services of the Exceutor Framework in Java
 * Author: Sujal Morwani
 * Created On: 28/08/2025
 */

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorDemo {
  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    // Thread[] threads = new Thread[9];
    ExecutorService executor = Executors.newFixedThreadPool(9);
    for (int i = 1; i < 10; i++) {
      int finalI = i;
      executor.submit(() -> {
        long result = factorail(finalI);
        System.out.println(result);
      });

      // Syste[]m.out.println(factorail(i));
    }
    executor.shutdown();
    // System.out.println("Total time: " + (System.currentTimeMillis() -
    // startTime));
    try {
      executor.awaitTermination(100, TimeUnit.SECONDS);
    } catch (Exception e) {

    }
  }

  private static long factorail(int n) {

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    long result = 1;
    for (int i = 1; i <= n; i++) {
      result *= i;
    }
    return result;
  }
}
