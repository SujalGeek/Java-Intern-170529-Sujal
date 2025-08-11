/* To understand the concept of the Producer and Consumer using the Constructor and
*  Data Hiding in Java
 * Author: Sujal Morwani
 * Created On: 11/08/2025
 */
class Producer {
  private String itemNo;
  private String name;
  private double price;
  private short qty;

  public Producer(String itemno) {
    itemNo = itemno;
  }

  public Producer(String itemno, String name) {
    itemNo = itemno;
    this.name = name;
  }

  public Producer(String itemno, String name, double price, short qty) {
    itemNo = itemno;
    this.name = name;
    setPrice(price);
    setQuantity(qty);
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setQuantity(short qty) {
    this.qty = qty;
  }
}

class Consumer {
  private String custId;
  private String name;
  private String phoneNo;
  private String address;

  public Consumer(String custId, String name) {
    this.custId = custId;
    this.name = name;
  }

  public Consumer(String custId, String name, String phoneNo, String address) {
    this.custId = custId;
    this.address = address;
    setAddress(address);
    setPhoneNo(phoneNo);
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getCustId() {
    return custId;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public String getPhoneNo() {
    return phoneNo;
  }
}

public class ProducerConsumer {
  public static void main(String[] args) {

  }
}
