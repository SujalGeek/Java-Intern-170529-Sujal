import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Propertydemo {
  public static void main(String[] args) throws Exception {
    Properties ps = new Properties();
    // ps.setProperty("Brand", "Dell");
    // ps.setProperty("Processor", "i7");
    // ps.setProperty("OS", "Windows10");
    // ps.setProperty("Model", "Latitude");

    // ps.storeToXML(new FileOutputStream("MyData.xml"), "Laptop");

    ps.load(new FileInputStream("MyData.txt"));
    ps.store(new FileOutputStream("MyData.txt"), "Laptop");

    /*
     * String-based storage → both key and value are String.
     * File I/O Support → can easily load from or store into .properties files.
     * Default properties → supports providing default values.
     * Often used in Java projects for externalizing configurations instead of
     * hardcoding them.
     * They will not maintain the same order
     */
    // System.out.println(ps);
    ps.loadFromXML(new FileInputStream("MyData.xml"));
    // System.out.println(ps.getProperty("Brand"));
    ps.load(new FileInputStream("MyData.txt"));
    System.out.println(ps);
  }
}
