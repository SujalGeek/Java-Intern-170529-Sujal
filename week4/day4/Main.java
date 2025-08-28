public class Main {
  public static void main(String[] args) {
    BankAccount bankAccount = new BankAccount();
    Runnable task = new Runnable() {
      public void run() {
        bankAccount.withdraw(50);
      }
    };

    Thread t1 = new Thread(task, "Thread 1");
    Thread t2 = new Thread(task, "Thread 2");
    t1.start();
    t2.start();
  }
}
