// Objective: To undertand how to read the input variables in java
// Author: Sujal Morwani
// Creation Date: 05/08/2025

import java.util.*;

class InputRead {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    System.out.println("Enter the first number: ");
    int number1 = input.nextInt();
    System.out.println("Enter the second number: ");
    int number2 = input.nextInt();
    int result = number1 + number2;
    System.out.println("The sum of two numbers is: " + result);

    input.nextLine();

    System.out.println("Enter the word: ");
    String inputChar = input.next();
    input.nextLine();

    System.out.println("Enter the string:");
    String inputString = input.nextLine();

    System.out.println("After reading the word: " + inputChar);
    System.out.println("After reading the string: " + inputString);

    System.out.println("Welcome to here!! " + inputChar);
    input.close();

  }
}
