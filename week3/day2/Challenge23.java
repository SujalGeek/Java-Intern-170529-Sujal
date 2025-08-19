/* To understand the concept of ATM using Thread class with below aim:
 *class ATM -> check balance(name) give a message and withdraw(name,amount) give a message  
 * class Customer -> ATM atm;(multiple objects should refer to the same object) string name
 * int amount; usedATM() -> checkBalance() -> withdraw(name,amount)
 * Author: Sujal Morwani
 * Created On: 19/08/2025
 */
class ATM {
  synchronized public void checkBalance(String name) {
    try {
      System.out.println(name + " is checking the Balance");
      Thread.sleep(100);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  synchronized public void withdraw(String name, int amount) {
    try {
      System.out.println(name + " is withdrawing the amount is: " + amount);
      Thread.sleep(1000);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}

class Customer extends Thread {
  ATM atm;
  String name;
  int amount;

  public Customer(String name, int amount, ATM atm) {
    this.name = name;
    this.amount = amount;
    this.atm = atm;
  }

  void useATM() {
    atm.checkBalance(name);
    atm.withdraw(name, amount);
  }

  public void run() {
    useATM();
  }
}

public class Challenge23 {
  public static void main(String[] args) {
    ATM atm = new ATM();
    Customer c1 = new Customer("Sujal", 2000, atm);
    Customer c2 = new Customer("Dhruv", 1000, atm);
    c1.start();
    c2.start();
  }
}
