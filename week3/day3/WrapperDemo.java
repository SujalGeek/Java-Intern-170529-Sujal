/* To understand the concept of Wrapper classes
 * Author: Sujal Morwani
 * Created On: 20/08/2025 
 */

/* Why we need the Wrapper classes
 * Wrapper classes in Java are object representations of primitive data types
 * To represent it using the Object 
 */

public class WrapperDemo {
  public static void main(String[] args) {
    // Deprecated one notation here
    // Integer a = new Integer(10);
    Integer a = 10;
    Integer b = Integer.valueOf(10);
    // It will result true
    // as the both values are equal
    System.out.println(a.equals(b));
    // Must be a number should be passed in or you can use
    // pass on A-F and use the raidx
    // Integer number3 = Integer.parseInt("HEllo WOrld");
    // Binary number
    Integer number3 = Integer.valueOf("1010", 2);
    System.out.println(number3);

    Integer number4 = Integer.valueOf("A7", 16);
    System.out.println(number4);
    System.out.println(Integer.toString(20));
    System.out.println(Integer.reverse(2));
    System.out.println(Integer.toBinaryString(30));

    // Boxing and unboxing
    int j = 10;
    Integer i = 20;
    int k = j;

    float floatnum = 12.5f;
    Float floatnum2 = 12.5f;
    System.out.println(floatnum2.equals(floatnum));

    // Checking if the number is infinite Number
    Float floatnum3 = 12.5f / 0;
    System.out.println(floatnum3.isInfinite());
    System.out.println(Float.POSITIVE_INFINITY == floatnum3);
    // it will result false
    Float float3 = -12.5f / 0;
    System.out.println(float3 == Float.NEGATIVE_INFINITY);
    System.out.println(float3 != Float.NaN);

    // For Character
    Short f = Short.valueOf("112");
    System.out.println(f);
    //
    Character k2 = Character.valueOf('A');
    Boolean l = Boolean.valueOf("true");
    System.out.println(k2);
    System.out.println(l);
  }
}
