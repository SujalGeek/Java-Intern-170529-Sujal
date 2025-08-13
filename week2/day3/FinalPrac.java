/* To undserstand the final keyword in Java
 * Author: Sujal Morwani
 * Created On: 13/08/2025
 */

// How to intialize the final variable there are three ways
class Demo {
  // 1st way
  final int min = 1;
  final int max;
  final int normal;

  // 2nd way in the block
  {
    normal = 8;
  }

  // Inside the constructor
  Demo() {
    max = 10;
  }
}

// Final methods can not be override
// Final classes can not be extended/inhertied
public class FinalPrac {
  public static void main(String[] args) {
    Demo d = new Demo();
    System.out.println(d.min);
    System.out.println(d.normal);
    System.out.println(d.max);
  }
}
