import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorDemo {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<?> future = executorService.submit(() -> System.out.println("Hello")); // runnable parameter
    System.out.println(future.get()); // blocking call ( null )
    if (future.isDone()) {
      System.out.println("Task is done !");
    }
    executorService.shutdown();
  }
}
