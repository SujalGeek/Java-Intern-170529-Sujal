
public class StringPractice {
  public static void main(String[] args) {

    // String method 1
    String str = new String("vs code");
    System.out.println(str.length());
    str = str.toUpperCase();
    System.out.println(str);
    String str3 = new String("SUJAL MORWANI");
    str3 = str3.toLowerCase();
    System.out.println(str3);
    String str5 = new String("         sujal        ");
    str5 = str5.trim();
    System.out.println(str5);

    String str6 = str5.replace('u', 'a');
    System.out.println(str6);

    System.out.println(str6.substring(4));

    // String method 2
    String str7 = new String("Hello");
    System.out.println(str7.startsWith("He"));
    System.out.println(str7.endsWith("lo"));
    System.out.println(str7.charAt(4));
    for (int i = 0; i < str7.length(); i++) {
      System.out.print(str7.charAt(i));
    }
    System.out.println();
    String str8 = new String("https://github.com/GeekGod22");
    System.out.println("The index of the github is: " + str8.indexOf("github"));

    // String Method 3
    String str10 = "Hello World";
    String st11 = "hello world";
    System.out.println(str10.equals(st11));
    System.out.println(str10.equalsIgnoreCase(st11));
    System.out.println(str10.compareTo(st11));

  }
}
