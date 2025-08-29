import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Task with a return value (Callable)
        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(1000); // simulate work
            return 42;
        });

        System.out.println("Task submitted...");

        // Wait for result
        System.out.println("Result = " + future.get()); // blocks until ready

        executor.shutdown();
    }
}
