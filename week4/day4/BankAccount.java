/* To understand the concept of Java explict when the synchronized will not useful
 * Author: Sujal Morwani
 * Created On: 28/08/2025
 */

// Thread 1 if having sleep of 10000 seconds or 
// if is fetching from the database then it will take more time
// so due to that thread 2 will not be any exceution
// so now we have to use the explict way

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import javax.management.RuntimeErrorException;

public class BankAccount {

  private int balanace = 100;
  private final Lock lock = new ReentrantLock();

  public void withdraw(int amount) {
    System.out.println(Thread.currentThread().getName() + " attempting to withdraw " + amount);
    // if (balanace >= amount) {
    // System.out.println(Thread.currentThread().getName() + " proceeding with
    // withdrawal");
    // try {
    // Thread.sleep(1000);
    // } catch (InterruptedException e) {
    // //
    // // throw new RuntimeErrorException(e);
    // }
    // balanace -= amount;
    // } else {
    // System.out.println(Thread.currentThread().getName() + " insufficeient
    // balanace ");
    // }
    // }
    try {
      if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
        if (balanace >= amount) {
          System.out.println(Thread.currentThread().getName() + " proceeding with withdrawal");
          try {
            Thread.sleep(1000);
            balanace = -amount;
            System.out
                .println(Thread.currentThread().getName() + " completed withdrawal. Remaining balance: " + balanace);
          } catch (Exception e) {
            // System.out.println(e);
            Thread.currentThread().interrupt();
          } finally {
            lock.unlock();
          }
        } else {
          System.out.println(Thread.currentThread().getName() + " insufficeient balance");
        }
      } else {
        System.out.println(Thread.currentThread().getName() + " could not acquire the lock, will try later");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      // e.printStackTrace();
    }
  }

  public static void main(String[] args) {

  }
}
