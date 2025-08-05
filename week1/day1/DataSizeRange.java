/* Objective: To undertand what are the data types and what are their minimum and
  maximum value and getting the size in bytes and bits
  Author: Sujal Morwani
  Creation Date: 05/08/2025
 */

class DataSizeRange {
  public static void main(String args[]) {
    // Get the Integer datatype max,min and size in bytes and bits
    System.out.println("Int min: " + Integer.MIN_VALUE);
    System.out.println("Int max: " + Integer.MAX_VALUE);
    System.out.println("Int Bytes: " + Integer.BYTES);
    System.out.println("Int Bits: " + Integer.SIZE);

    // Get the Byte datatype max,min and size in bytes and bits
    System.out.println("Bytes");
    System.out.println("Byte Min: " + Byte.MIN_VALUE);
    System.out.println("Byte Max: " + Byte.MAX_VALUE);
    System.out.println("Byte bytes: " + Byte.BYTES);
    System.out.println("Bytes bits: " + Byte.SIZE);

  }
}