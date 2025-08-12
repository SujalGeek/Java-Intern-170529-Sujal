// Objective: To undertand the concept of literal
// Author: Sujal Morwani
// Creation Date: 05/08/2025

import java.lang.*;

class Literal {
	public static void main(String args[]) {
		byte b1 = 10;
		byte b2 = 0b1010;
		byte b3 = 012;
		byte b4 = 0XA;

		System.out.println(b1);
		System.out.println(b2);
		System.out.println(b3);
		System.out.println(b4);

		// Integer literal to long

		long l = 125;
		long l2 = 99999999999l;
		long l3 = 9999999999L;
		// Throws the error
		// int i1=22322L;

		float num1 = 12.56f;
		double num2 = 1234.5334;
		// For large numbers can use _ to separate them and read easily
		long l5 = 999_999_999;
		float f5 = 32_2121.32f;
	}

}