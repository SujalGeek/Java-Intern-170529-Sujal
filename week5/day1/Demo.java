import java.net.URI;
import java.net.URL;

public class Demo {
  public static void main(String[] args) throws Exception {
    // Some basic examples using the URIs
    URI obj = new URI("http://www.unacademy.com:60/index.html");
    System.out.println(obj.getPort());
    System.out.println(obj.getHost());
    System.out.println(obj.getRawPath());
    System.out.println(obj.getQuery());
    System.out.println(obj.getUserInfo());
    System.out.println(obj.isAbsolute());
    System.out.println(obj.getFragment());

    // System.out.println(obj.getProtocol());
  }
}
