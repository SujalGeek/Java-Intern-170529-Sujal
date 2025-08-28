/* To understand the concept of Optional in java
 * Author: Sujal Morwani
 * Created On: 28/08/2025
 */
package OptionalDemo;

import java.util.List;
import java.util.Optional;

public class Customer {

  private int id;
  private String name;
  private String email;
  private List<String> phoneNumbers;

  public Customer() {
  }

  public Customer(int id, String name, String email, List<String> phoneNumbers) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phoneNumbers = phoneNumbers;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Optional<String> getEmail() {
    return Optional.ofNullable(email);
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<String> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(List<String> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }
}