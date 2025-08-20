public class Book {
  private String name;
  private int issuedTo = -1;

  public Book(String name) {
    this.name = name;
  }

  public void issue(int studentId) {
    this.issuedTo = studentId;
    System.out.println("Book '" + name + "' issued to student " + studentId);
  }

  public boolean available(String requestedName) {
    return issuedTo == -1 && name.equals(requestedName);
  }

  public String getName() {
    return name;
  }

  // main method to run directly
  public static void main(String[] args) {
    Book b = new Book("Java Programming");
    b.issue(101);
    System.out.println("Is available? " + b.available("Java Programming"));
  }
}
