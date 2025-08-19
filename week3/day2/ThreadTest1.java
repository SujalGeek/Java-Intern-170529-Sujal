class MyThread extends Thread {
  public void run() {
    int count = 1;
    while (true) {
      // System.out.println(count++);
      System.out.println(count++ + " My thread");
      // Thread.yield();
    }
  }
}

public class ThreadTest1 {
  public static void main(String[] args) throws Exception {
    MyThread t1 = new MyThread();
    t1.start();
    int count = 1;
    while (true) {
      System.out.println(count++ + " Main");
      Thread.yield();
    }
    // t1.setDaemon(true);
    /*
     * t1.start();
     * Thread mainThread = Thread.currentThread();
     * mainThread.join();
     */
    // try {
    // Thread.sleep(1000);
    // } catch (Exception e) {
    // }
  }
}
