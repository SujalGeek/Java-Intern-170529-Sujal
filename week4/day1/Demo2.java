/* To understand the concept of Serializable and implement it and by example understanding
the concept it and write down the code 
 * 
 */
// class Customer -> string custid ->  name -> phone no and need to store and 
// retieve the data and the id should automitically generate

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class Customer implements Serializable {
  String custId;
  String name;
  String phone;

  static int count = 0;

  public Customer() {

  }

  public Customer(String n, String ph) {
    custId = "C" + count;
    count++;
    name = n;
    phone = ph;
  }

  public String toString() {
    return "Customer Id: " + custId + "\nName :" + name + "\nPhone: " + phone + "\n";
  }

}

public class Demo2 {
  public static void main(String[] args) throws Exception {

    java.util.Scanner sc = new java.util.Scanner(System.in);

    Customer list2[] = { new Customer("Smith", "1234561223"), new Customer("John", "1234567233"),
        new Customer("Sujal", "111133443") };
    FileOutputStream fos = new FileOutputStream("StudentChallenege2.txt");
    ObjectOutputStream oos = new ObjectOutputStream(fos);

    oos.writeInt(list2.length);
    for (Customer c : list2) {
      oos.writeObject(c);
    }
    oos.close();
    fos.close();

    FileInputStream fis = new FileInputStream("StudentChallenege2.txt");
    ObjectInputStream ois = new ObjectInputStream(fis);

    int length = ois.readInt();
    Customer list[] = new Customer[length];

    for (int i = 0; i < length; i++) {
      list[i] = (Customer) ois.readObject();
    }
    System.out.println("ENter the name of Customer");
    String name = sc.nextLine();

    for (int i = 0; i < length; i++) {
      if (name.equalsIgnoreCase((list[i]).name)) {
        System.out.println(list[i]);
      }
    }
  }
}
