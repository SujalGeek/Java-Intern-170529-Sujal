import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteCount {

  private int count = 0;

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private final Lock readLock = lock.readLock();

  private final Lock writeLock = lock.writeLock();

  public void increment() {
    writeLock.lock();
    try {
      count++;
    } finally {
      writeLock.unlock();
    }
  }

  public int getCount() {
    readLock.lock();
    try {
      return count;
    } finally {
      readLock.unlock();
    }
  }

  public static void main(String[] args) throws Exception {

    ReadWriteCount r12 = new ReadWriteCount();

    Runnable readTask = new Runnable() {
      public void run() {
        for (int i = 0; i < 10; i++) {
          System.out.println(Thread.currentThread().getName() + " read: " + r12.getCount());
        }
      }
    };

    Runnable writeTask = new Runnable() {
      public void run() {
        for (int i = 0; i < 10; i++) {
          r12.increment();
          System.out.println(Thread.currentThread().getName() + " incremented");
        }
      }
    };

    Thread writeThread = new Thread(writeTask);
    Thread readThread = new Thread(readTask);
    Thread readThread2 = new Thread(readTask);

    writeThread.start();
    readThread.start();
    readThread2.start();

    writeThread.join();
    readThread.join();
    readThread2.join();
  }
}
