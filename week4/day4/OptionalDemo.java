// package week4.day4;
/* To understand the concept of Optional class in Java
 * Author: Sujal Morwani
 * Created on: 28/08/2025
 */

import java.util.Optional;

public class OptionalDemo {

  public static Optional<String> getName() {
    String name = "Rohan";
    return Optional.ofNullable(name);
  }

  public static void main(String[] args) {

    // String str = "This is Java language";

    /*
     * Why to use the Optional while return?
     * It will lead to NullPointer Exception so we need to use the Optional
     * The function may return null if while testing so now
     * to check and maange the null value need to use the
     * Optional class so that the message will be printed by using
     * and creating the object of Optional class
     * 
     * In optional there is way to handle the null and there is way
     * to provide the custom exception for null
     * 
     * 
     */
    String str = null;
    Optional<String> str1 = Optional.ofNullable(str);
    System.out.println(str1.isPresent());
    System.out.println(str1.orElse("No value here"));

    // It will throw an Exception as there is null passing on the str1
    // So while get it the str will throw an Exception

    // System.out.println(str1.get());

    Optional<String> op1 = getName();
    System.out.println(op1.orElse("It is returning the null value"));
  }

}
