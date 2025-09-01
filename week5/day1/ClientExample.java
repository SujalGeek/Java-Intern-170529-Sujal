import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientExample {
  public static void main(String[] args) throws Exception {
    InetSocketAddress serverAddress = new InetSocketAddress("localhost", 5000);

    // Open client SocketChannel
    SocketChannel socketChannel = SocketChannel.open(serverAddress);

    System.out.println("Connected to server...");

    // Send data
    String message = "Hello Server from Client!";
    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
    socketChannel.write(buffer);

    // Read response
    buffer.clear();
    int bytesRead = socketChannel.read(buffer);
    System.out.println("Server replied: " + new String(buffer.array(), 0, bytesRead));

    socketChannel.close();
  }
}
