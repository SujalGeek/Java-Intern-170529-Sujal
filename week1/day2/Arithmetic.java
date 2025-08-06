/* Motive it to understand how the arithmetic operator is working with all
 * data type
 * Author: Sujal Morwani
 * Created On: 06/08/2025
 */

class Arithmetic {
  public static void main(String[] args) {
    int number1 = 20;
    int number2 = 10;
    short number3 = 15;
    // Here is the importance of the () when remove it then the
    // then it gives answer as 20 but correct answer is 15 so () is needed
    int result = (number2 + number1) / 2;
    System.out.println(result);

    // After converting the integer result to the float using (float)
    float result2 = (float) (number1 + number2) / 2;
    System.out.println(result2);

    // When performing the aritemtic operation on byte,short and int
    // you will get the result in the int
    // if you give result the byte or short datatype then it will give error
    // byte a2 = number1 + number3;
    int store = number1 + number3;

    // lossy conversion so should put f in the end;
    float number4 = 12.5f;
    long number5 = 1231;
    // If you store the result in the long it will give lossy conversion
    // long result4 = number4 + number5;
    float ans = number4 * number5;
    System.out.println(ans);

    // When you take float and double you need to store in the double not in float
    double num6 = 322;
    double decimal_numbers_result = number4 * num6;
    System.out.println(decimal_numbers_result);

    // When you are dealing with the char datatype and int
    // then you have to store the result in the integer
    char a = 40;
    int num2 = 60;
    int char_subtraction_integer = num2 - a;
    System.out.println(char_subtraction_integer);

  }
}
