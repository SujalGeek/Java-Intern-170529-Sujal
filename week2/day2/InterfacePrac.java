/* To understand the concept of the Interface in Java
 * Author: Sujal Morwani 
 * Created On: 12/08/2025
 */

class Phone {
  public void call() {
    System.out.println("Phone call");
  }

  public void sms() {
    System.out.println("Phone is sending SMS");
  }
}

interface ICamera {
  void click();

  void record();
}

interface IMusicPlayer {
  void play();

  void stop();
}

class SmartPhone extends Phone implements ICamera, IMusicPlayer {
  public void click() {
    System.out.println("The camera is used by Click");
  }

  public void record() {
    System.out.println("The camera is used during recording");
  }

  public void play() {
    System.out.println("The media player is used during play!!");
  }

  public void stop() {
    System.out.println("The media player is stopped using stop!!");
  }
}
// interface Test {
// void method1();

// void method2();
// }

// class Demo implements Test {
// public void method1() {
// System.out.println("The method1 is called!!");
// }

// public void method2() {
// System.out.println("The method2 is called!!");
// }

// public void method3() {
// System.out.println("The method3 is called!!");
// }
// }

public class InterfacePrac {
  public static void main(String[] args) {
    // Test t = new Demo();
    // t.method1();
    // t.method2();
    // // t.method3(); it will show the error

    SmartPhone s1 = new SmartPhone();
    Phone p1 = s1;
    s1.call();
    s1.sms();
    s1.click();
    s1.stop();

    // Camera
  }
}
