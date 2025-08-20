/* To understand the concept of Math in Java
 * Author: Sujal Morwani
 * Created On: 20/08/2025
 */
public class MathDemo {
  public static void main(String[] args) {

    System.out.println("Absolute: ");
    System.out.println(Math.abs(-123));

    System.out.println("Absolute: ");
    System.out.println(StrictMath.abs(-123));

    System.out.println("Cube Root: ");
    System.out.println(Math.cbrt(27));

    System.out.println("Exact Decrement: ");
    // System.out.println(Math.decrementExact(Integer.MIN_VALUE));
    int i = Integer.MIN_VALUE;
    i--;
    System.out.println(i);

    System.out.println("Exponent value in Floating Point Rep : ");
    System.out.println(Math.getExponent(123.45));

    System.out.println("Round Division :");
    System.out.println(Math.floorDiv(50, 9));

    System.out.println("e power x");
    System.out.println(Math.exp(1));

    System.out.println("e power x");
    System.out.println(StrictMath.exp(1));

    System.out.println("Log base 10: ");
    System.out.println(Math.log10(100));

    System.out.println("Maximum: ");
    System.out.println(Math.max(100, 40));

    System.out.println("Tan :");
    System.out.println(Math.tan(45 * Math.PI / 100));

    System.out.println("Convert to Radius: ");
    System.out.println(Math.toRadians(90));

    System.out.println("Random");
    System.out.println(Math.random());

    System.out.println("Power: ");
    System.out.println(Math.pow(2, 3));

    System.out.println("Convert to Degree: ");
    System.out.println(Math.atan(1));

    System.out.println("Next FLoat Value :");
    System.out.println(Math.nextAfter(12.5, 13));
  }
}
