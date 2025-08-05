// Objective: To undertand how the Java program works
// Author: Sujal Morwani
// Creation Date: 05/08/2025

import java.lang.*;

//  class Second 
/*
 * If the class is written as Second then the Second.class file will be made
 * then we have to run that file using java Second so for removing that
 * convention we use the MyFirst so that the developer will not confuse
 * and we are import the lang package but if we have not imported then also the
 * program will work
 * 
 * But if we have written public then the public class Second will not work because
 * then the name should be same as the file name so remember it
 */
class MyFirst {
	public static void main(String args[]) {
		System.out.println("Hello World");
		System.out.println(args[0]);
		System.out.println(args[1]);
	}

}