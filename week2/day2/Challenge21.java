/* To understand the concept of the interface more of a example practice
* Author: Sujal Morwani
 * Created On: 12/08/2025
 */
// interface Member with callback() method
// Store and Customer class which implements the Member
// Need to create the array of objects Member mem[] = new Member[100];
// int count = 0
// Store method(register member m)
// mem[count++] = m

// void inviteSale() for loop(to count) mem[i].callback()

// class Customer implements Member (string name) customer(string s)
// name =s;
// void callback() sop("ok i sure visit"+name)

interface Member {
  void callback();
}

class Customer implements Member {
  String name;

  public void callback() {
    System.out.println("Ok Sure I will visit!" + name);
  }

  Customer(String name) {
    this.name = name;
  }
}

class Store {
  int count = 0;
  Member mem[] = new Member[100];

  void register(Member m) {
    mem[count++] = m;
  }

  void inviteSale() {
    for (int i = 0; i < count; i++) {
      mem[i].callback();
    }
  }
}

public class Challenge21 {
  public static void main(String[] args) {

    Store s = new Store();
    // Member m = new Customer("Sujal");
    Customer c1 = new Customer("Karan");
    // s.register();
    s.register(c1);
    s.inviteSale();

  }
}
