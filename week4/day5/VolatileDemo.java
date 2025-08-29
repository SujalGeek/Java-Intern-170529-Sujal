class SharedObj {
  volatile boolean flag = false;

  void setFlagTrue() {
    System.out.println("Writer thread made the flag true !!");
    flag = true;
  }

  void printIfFlagTrue() {
    while (!flag) {
      // keeps looping
    }
    System.out.println("Flag is true !!");
  }
}

public class VolatileDemo {
  public static void main(String[] args) throws Exception {
    SharedObj obj = new SharedObj();

    Thread writerThread = new Thread(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
      obj.setFlagTrue();
    });

    Thread readerThread = new Thread(
        obj::printIfFlagTrue);

    writerThread.start();
    readerThread.start();
  }
}
