public class BitwiseDemo {
  public static void main(String[] args) {
    // Performing the bitwise operators like and,or,xor, leftshift, rightshift and
    // not operator
    int number1 = 5;
    int number2 = 6, andOperatorResult;
    andOperatorResult = number1 & number2;
    int orOpertaorResylt = number1 | number2;
    int xorOperatorResult = number1 ^ number2;

    System.out.println("The AND operator result is: " + andOperatorResult);
    System.out.println("The OR operator result is: " + orOpertaorResylt);
    System.out.println("The XOR operator result is: " + xorOperatorResult);

    // Left Shift
    int number4 = 5;
    int leftShiftResult;

    leftShiftResult = number4 << 1;
    System.out.println("The left shift result is: " + leftShiftResult);

    // Right Shift
    int number5 = 10;
    int rightShiftResult;
    rightShiftResult = number5 >> 1;
    System.out.println("The right shift result is: " + rightShiftResult);

    int number6 = 5;
    int notOperatorResult;
    notOperatorResult = ~number6;
    System.out.println("The not operator result is: " + notOperatorResult);
  }
}
