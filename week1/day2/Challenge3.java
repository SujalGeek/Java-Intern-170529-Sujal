
/* Here in this challenge need to find the area of frontend backside
 * of the cuboid and also need to find the area of right and leftside
 * and find the area of bottom and top
 * so after this add all of it and get the total area of the cuboid
 * and also find the volume of the cuboid and get the perimeter of the cuboid
 * Author: Sujal Morwani
 * Created on: 06/08/2025
 */
import java.util.*;

public class Challenge3 {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    // Get the height of the cuboid
    System.out.println("Enter the height of the cuboid: ");
    int height = sc.nextInt();
    // Get the length of the cuboid
    System.out.println("Enter the length of the cuboid: ");
    int length = sc.nextInt();
    // Get the Breadth of the cuboid
    System.out.println("Enter the breadth of the cuboid: ");
    int breadth = sc.nextInt();

    long front_back_side_area = length * height;
    long right_left_side_area = breadth * height;
    long bottom_top_area = length * breadth;

    System.out.println("The front and back side area is: " + front_back_side_area);
    System.out.println("The right and left side area is: " + right_left_side_area);
    System.out.println("The bottom and top area is: " + bottom_top_area);

    long total_area = 2 * (front_back_side_area + right_left_side_area + bottom_top_area);
    System.out.println("The total area of the cuboid is: " + total_area);

    long volume_area = length * breadth * height;
    System.out.println("The volume of the cuboid is: " + volume_area);
    sc.close();
  }
}
