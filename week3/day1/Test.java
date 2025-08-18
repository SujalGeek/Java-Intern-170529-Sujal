/* To understand the concept of the MultiThreading in Java
  Author: Sujal Morwani
 * Created On: 18/08/2025
 */

class MyThread extends Thread {
  public void run() {
    int i = 1;
    while (true) {
      System.out.println(i + " Hello");
      i++;
    }
  }
}

public class Test {

  /*
   * public void run(){
   * int i=1;
   * while(true)
   * {
   * System.out.println(i+ "Hello");
   * i++;
   * }
   * }
   */
  public static void main(String[] args) {
    MyThread t = new MyThread();
    t.start();
    int i = 1;
    while (true) {
      System.out.println(i + " World");
      i++;
    }
  }
}
