import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioEchoServer {
  public static void main(String[] args) throws Exception {
    Selector selector = Selector.open();
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.bind(new InetSocketAddress(5000));
    serverChannel.configureBlocking(false);

    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    System.out.println("Echo Server started on the port 5000");

    while (true) {
      selector.select();
      Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

      while (keys.hasNext()) {
        SelectionKey key = keys.next();
        keys.remove();

        if (key.isAcceptable()) {
          SocketChannel client = serverChannel.accept();
          client.configureBlocking(false);
          client.register(selector, SelectionKey.OP_READ);
          System.out.println("Client connected");
        } else if (key.isReadable()) {
          SocketChannel client = (SocketChannel) key.channel();
          ByteBuffer buffer = ByteBuffer.allocate(200);

          int bytesRead = client.read(buffer);
          if (bytesRead == -1) {
            client.close();
            System.out.println("Client disconnected....");
            continue;
          }
          buffer.flip();
          // System.out.println();
          String msg = new String(buffer.array(), 0, bytesRead);
          System.out.println("Received" + msg);

          buffer.rewind();
          client.write(buffer);
        }
      }
    }
  }
}
