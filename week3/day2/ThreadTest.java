/* To understand the concept of thread class and its methods in Java
 * Author: Sujal Morwani
 * Created: 19/08/2025
 */
class MyThread extends Thread {
  public MyThread(String name) {
    super(name);
  }

  public void run() {
    int count = 1;
    while (true) {
      System.out.println(count++);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        System.out.println(e);
      }
    }
  }
}

public class ThreadTest {

  public static void main(String args[]) {
    MyThread t = new MyThread("This is the Thread1");
    t.start();
    System.out.println("Priority: " + t.getPriority());
    System.out.println("The thread is alive: " + t.isAlive());
    System.out.println("Name: " + t.getName());
    // System.out.println("Id: " + t.getId());
    System.out.println(t.getState());
    t.interrupt();

    // Main is waiting for the thread to finish(when doing t.start())
    /*
     * t.setDaemon(true)
     * t.start();
     * Now when setDaemon(true) Daemon threads are dependent threads if the
     * application
     * terminating then daemon thread will also terminate
     * But nothing will be print no matter if it inside a infinite loop
     * try{Thread.sleep(2000)}catch(exception e){}
     */
  }

}
