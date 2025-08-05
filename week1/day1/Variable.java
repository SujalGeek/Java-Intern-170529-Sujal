// Objective: To undertand how the variables are storing the data and undertanding
// the rules
// Author: Sujal Morwani
// Creation Date: 05/08/2025

class Variable {
	public static void main(String args[]) {
		// Defining variables with storing their respective values that are within the
		// range

		// While declaring the variable there is case sensitive it
		int x;
		int X;
		// In the code the variable should not contain any spaces it will give us error
		// int roll number;
		int roll_number;
		// Starting with alphabet, $ and _ are allowed while declaring in variables
		int _x;
		int $x;

		// if the data type is not in use the it can be used as variable like below in
		// code
		long String = 121;
		// Camel case is allowed
		int rollNumber = 200;

		// No minimum length for declaring the variable
		float averageMarksOfClass;

		byte number1 = 127;
		short number2 = 300;
		int number3 = 2000000000;
		float number4 = 45.23f;
		double number5 = 3232.21;

		System.out.println("The byte variable is: " + number1);
		System.out.println("The short variable is: " + number2);
		System.out.println("The int variable is: " + number3);
		System.out.println("The float variable is: " + number4);
		System.out.println("The double variable is: " + number5);
	}

}