/* To understand the concept of PipedInput and Output Stream used to 
 * synchroized the inter thread commuincation
 * Author: Sujal Morwani
 * Created On: 22/08/2025
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class Producer extends Thread {
  OutputStream os;

  public Producer(OutputStream o) {
    os = o;
  }

  public void run() {
    int count = 1;

    while (true) {
      try {
        os.write(count);
        os.flush();

        System.out.println("Producer " + count);
        System.out.flush();

        Thread.sleep(10);
        count++;
      } catch (Exception e) {
      }
    }
  }
}

class Consumer extends Thread {
  InputStream i1;

  public Consumer(InputStream i) {
    i1 = i;
  }

  public void run() {
    int x;
    while (true) {
      try {

        x = i1.read();

        System.out.println("Consumer " + x);
        System.out.flush();
        Thread.sleep(10);
      } catch (Exception e) {
      }
    }
  }
}

public class PipedDemo {
  public static void main(String[] args) throws Exception {

    PipedInputStream pis = new PipedInputStream();
    PipedOutputStream pos = new PipedOutputStream();

    pos.connect(pis);

    Producer p = new Producer(pos);
    Consumer c = new Consumer(pis);
    p.start();

    c.start();
  }
}
