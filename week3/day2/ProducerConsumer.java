/* To understand the probelem of Producer and Consumer and solving it using
 * Inter Thread Commuincation Concept by extending the Thread class
 * Author: Sujal Morwani
 * Created On: 19/08/2025
 */

class MyData {
  int value;
  boolean flag = true;

  synchronized public void set(int v) {

    while (flag != true) {
      try {
        wait();
      } catch (Exception e) {
      }
    }
    value = v;
    flag = false;
    notify();
  }

  synchronized public int get() {
    int x = 0;
    while (flag != false) {
      try {
        wait();
      } catch (Exception e) {
      }
    }
    x = value;
    flag = true;
    notify();
    return x;
  }

}

class Producer extends Thread {
  MyData data;

  public Producer(MyData data) {
    this.data = data;
  }

  public void run() {
    int count = 1;
    while (true) {
      data.set(count);
      System.out.println("Producer " + count);
      count++;
    }
  }
}

class Consumer extends Thread {
  MyData data;

  public Consumer(MyData data) {
    this.data = data;
  }

  public void run() {
    int count = 1;
    while (true) {
      data.get();
      System.out.println("Consumer " + count);
      count++;
    }
  }
}

public class ProducerConsumer {
  public static void main(String[] args) {
    MyData m = new MyData();
    Producer p1 = new Producer(m);
    Consumer c1 = new Consumer(m);

    p1.start();
    c1.start();
  }
}
