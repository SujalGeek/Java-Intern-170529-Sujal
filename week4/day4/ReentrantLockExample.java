
/* To understand the concept of ReentrantLock in Java how it works
 * 
 * Author: Sujal Morwani
 * Created on: 28/08/2025
 */
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class ReentrantLockExample {

  private final Lock lock = new ReentrantLock();

  public void Outermethod() {
    lock.lock();
    try {
      System.out.println("Outer Method");
      innerMethod();
    } finally {
      lock.unlock();
    }
  }

  public void innerMethod() {
    lock.lock();
    try {
      System.out.println("Inner Method");
      Outermethod();
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    ReentrantLockExample r1 = new ReentrantLockExample();
    r1.Outermethod();
    // r1.innerMethod();

  }
}
