import java.net.*;
import java.io.*;

class Client { // <-- no public here
  public static void main(String[] args) throws Exception {
    Socket stk = new Socket("127.0.0.1", 2000);

    BufferedReader keyb = new BufferedReader(new InputStreamReader(System.in));
    BufferedReader br = new BufferedReader(new InputStreamReader(stk.getInputStream()));
    PrintStream ps = new PrintStream(stk.getOutputStream());

    String msg;
    do {
      msg = keyb.readLine();
      ps.println(msg);
      msg = br.readLine();
      System.out.println("From Server: " + msg);
    } while (!msg.equals("dne"));

    stk.close();
  }
}

public class NetworkExample {
  public static void main(String[] args) throws Exception {
    ServerSocket ss = new ServerSocket(2000);
    System.out.println("Server started on port 2000...");
    Socket stk = ss.accept();

    BufferedReader br = new BufferedReader(new InputStreamReader(stk.getInputStream()));
    PrintStream ps = new PrintStream(stk.getOutputStream());

    String msg;
    StringBuilder sb;
    do {
      msg = br.readLine();
      sb = new StringBuilder(msg);
      sb.reverse();
      msg = sb.toString();
      ps.println(msg);
    } while (!msg.equals("dne"));

    stk.close();
    ss.close();
  }
}
