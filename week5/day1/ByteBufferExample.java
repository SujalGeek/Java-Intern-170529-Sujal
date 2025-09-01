import java.nio.ByteBuffer;

public class ByteBufferExample {
  public static void main(String[] args) {
    ByteBuffer buffer = ByteBuffer.allocate(10);
    buffer.put((byte) 'A');
    buffer.put((byte) 'B');
    buffer.put((byte) 'C');
    buffer.put((byte) 'D');

    buffer.flip();

    while (buffer.hasRemaining()) {
      System.out.println((char) buffer.get() + " ");
    }
    System.out.println(buffer.isReadOnly());
    System.out.println(buffer.reset());

  }
}
