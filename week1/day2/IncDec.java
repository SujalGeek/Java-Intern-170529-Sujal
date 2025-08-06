/* To understand how increment and decrement in the program
 * Author: Sujal Morwani
 * Created On: 06/08/2025
 */

public class IncDec {
  public static void main(String[] args) {

    int num1 = 5, num2;
    num2 = num1++;
    System.out.println("The value of num1 after post increment in num1: " + num1);
    System.out.println("The value of num2 after post increment in num2: " + num2);

    int num3 = 5, num4;
    num4 = ++num3;
    System.out.println("The value of num3 after pre increment in num3: " + num3);
    System.out.println("The value of num4 after pre increment in num4: " + num4);

    int num5 = 7, num6 = 5, result;
    result = 2 * num5++ + 3 * ++num6;
    System.out.println("The value of x and y are: " + num5 + " " + num6);
    System.out.println("The result we got! " + result);
    // check 2*num5++ + 3 * ++num5

    // Works on the float,byte and character also
    float number1 = 23.5f;
    number1++;
    System.out.println(number1);

    byte byte_var = 21;
    byte_var++;
    // byte_var=byte_var+1 not works
    System.out.println(byte_var);
    char is_value = 'A';
    is_value++;
    System.out.println(is_value);
  }
}
