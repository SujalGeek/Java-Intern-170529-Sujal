import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.channels.DatagramChannel;

public class UDPClient {
  public static void main(String args[]) throws Exception {

    DatagramSocket ds = new DatagramSocket();
    int i = 8;
    // byte[] b = (i + "").getBytes();
    byte[] b = String.valueOf(i).getBytes();
    InetAddress id = InetAddress.getLocalHost();
    DatagramPacket dp = new DatagramPacket(b, b.length, id, 9999);
    ds.send(dp);

    byte[] b1 = new byte[1024];
    DatagramPacket dp1 = new DatagramPacket(b1, b1.length);
    ds.receive(dp1);

    String str = new String(dp1.getData(), 0, dp1.getLength());
    System.out.println("The result is: " + str);
  }
}