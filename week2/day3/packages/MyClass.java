/* Understanding the concept of the Packages in Java
 * Author: Sujal Morwani
 * Created On: 13/08/2025
 */
package mypack;

public class MyClass {
  public int publicVar = 10; // Public: accessible everywhere
  protected int protectedVar = 20; // Protected: accessible in same package & subclasses
  int defaultVar = 30; // Default: accessible only in same package
  private int privateVar = 40; // Private: accessible only in same class

  public void display() {
    System.out.println("Public: " + publicVar);
    System.out.println("Protected: " + protectedVar);
    System.out.println("Default: " + defaultVar);
    System.out.println("Private: " + privateVar);
  }
}
