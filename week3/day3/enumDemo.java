/* To understand the concept of enum in Java
 * Author: Sujal Morwani
 * Created On: 20/08/2025
 */

enum Dept {
  CS("John", "Block A"),
  IT("Smit", "Block B"),
  ECE("Gaurav", "Block C"),
  CIVIL("Varun", "Block D");

  String head;
  String loc;

  private Dept(String head, String loc) {
    this.head = head;
    this.loc = loc;
  }

  public String getHeadName() {
    return head;
  }

  public String getLocation() {
    return loc;
  }
}

public class enumDemo {
  public static void main(String[] args) {

    Dept d1 = Dept.CS;
    System.out.println(d1.getHeadName());
    System.out.println(d1.getLocation());
  }
}
