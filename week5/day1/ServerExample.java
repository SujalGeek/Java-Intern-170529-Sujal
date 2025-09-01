import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerExample {
  public static void main(String[] args) throws Exception {
    // Create server socket channel
    ServerSocketChannel server = ServerSocketChannel.open();
    server.bind(new InetSocketAddress("localhost", 5000));

    System.out.println("Server started... waiting for client");

    // Accept a client connection
    SocketChannel client = server.accept();
    System.out.println("Client connected!");

    // Read client message
    ByteBuffer buffer = ByteBuffer.allocate(256);
    int bytesRead = client.read(buffer);
    buffer.flip();
    String received = new String(buffer.array(), 0, bytesRead);
    System.out.println("Client said: " + received);

    // Send reply
    String reply = "Hello Client from Server!";
    buffer.clear();
    buffer.put(reply.getBytes());
    buffer.flip();
    client.write(buffer);

    client.close();
    server.close();
  }
}
