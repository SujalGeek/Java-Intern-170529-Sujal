import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UnFairLock {

  private final Lock unfair = new ReentrantLock(true);

  public void accessResource() {
    unfair.lock();
    try {
      System.out.println(Thread.currentThread().getName() + " acquired the lock");
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      unfair.unlock();
      System.out.println(Thread.currentThread().getName() + " released the lock");
    }
  }

  public static void main(String[] args) {
    UnFairLock example = new UnFairLock();

    Runnable task = new Runnable() {
      public void run() {
        example.accessResource();
      }
    };

    Thread thread1 = new Thread(task, "Thread 1");
    Thread thread2 = new Thread(task, "Thread 2");
    Thread thread3 = new Thread(task, "Thread 3");

    thread1.start();
    thread2.start();
    thread3.start();
  }
}
