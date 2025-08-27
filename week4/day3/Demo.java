/*
 * To understand the concept of Menu Driven Program using the concepts like
 * Serialization , Maps, Sets and List in Java
 * Author: Sujal Morwani
 * Created On: 27/08/2025
 */

/* To create the account
 * to delete the account
 * to view the account
 save all accounts -> .txt file
 exit
 class Account -> take acc no.(object creation ke time pe lo) take name and balance
 create account in the hashmap (accno, Account details(accno, name,balance so and so))
 whenever the account is created you have to store in the hash map and
 delete it from hash map and actually searching for an account

 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;

class Account implements Serializable {
  String accno;
  String name;
  double balance;

  public Account() {

  }

  public Account(String a, String s, double b) {
    accno = a;
    name = s;
    balance = b;
  }

  public String toString() {
    return "Account Details {" + "\nAccount no " + accno + "\nname " + name + "\nbalance " + balance + " }\n";
  }

}

public class Demo {
  public static void main(String[] args) throws Exception {

    // Account o1 = new Account("234567899", "Rohan", 12300);
    Scanner sc = new Scanner(System.in);
    Account acc = null;
    HashMap<String, Account> has1 = new HashMap<>();
    try {
      FileInputStream fis = new FileInputStream("Accounts.txt");
      ObjectInputStream obj1 = new ObjectInputStream(fis);

      int count = obj1.readInt();
      for (int i = 0; i < count; i++) {
        acc = (Account) obj1.readObject();
        System.out.println(acc);
        has1.put(acc.accno, acc);
      }
      fis.close();
      obj1.close();
    } catch (Exception e) {
      // TODO: handle exception
    }

    FileOutputStream fous = new FileOutputStream("Account.txt");
    ObjectOutputStream obj2 = new ObjectOutputStream(fous);

    System.out.println("Menu");
    int choice;
    String accno, name;
    double balance;
    do {
      System.out.println("1. Create Account");
      System.out.println("2. Delete Account");
      System.out.println("3. View Account");
      System.out.println("4. View all Account");
      System.out.println("5. Save Accounts");
      System.out.println("6. Exit");
      System.out.println("Enter your choice");
      choice = sc.nextInt();

      sc.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

      switch (choice) {
        case 1:
          System.out.println("Enter the Account Details Accno, name and balance ");
          accno = sc.nextLine();
          name = sc.nextLine();
          balance = sc.nextDouble();
          acc = new Account(accno, name, balance);
          has1.put(accno, acc);
          System.out.println("Account created for " + name);
          break;
        case 2:
          System.out.println("Enter the Accno");
          accno = sc.nextLine();
          has1.remove(accno);
          break;
        case 3:
          System.out.println("Enter the accno");
          accno = sc.nextLine();
          acc = has1.get(accno);
          System.out.print(acc + " ");
          break;
        case 4:
          for (Account a : has1.values()) {
            System.out.println(a);
          }
          break;
        case 5:
        case 6:
          obj2.writeObject(has1.size());
          for (Account a : has1.values()) {
            obj2.writeObject(a);
          }
      }
    } while (choice != 6);
    obj2.flush();
    obj2.close();
    fous.close();

  }
}
