package work;

public class MasterLambda {
    public static void main(String[] args) {
        // by implemeting it 1st method

//    MyInter myinter = new MyInterImpl();
//    myinter.sayHii();

        // By anonymous one
//        MyInter myinter2 = new MyInter() {
//            @Override
//            public void sayHii() {
//                System.out.println("This is first anonymous class  ");
//            }
//        };
//        myinter2.sayHii();
//
//        MyInter myinter3 = new MyInter() {
//            @Override
//            public void sayHii() {
//                System.out.println("This is from second anonymous class");
//            }
//        };
//
//        myinter3.sayHii();

        // using our functional interface with the help of lambda
        MyInter i =()-> System.out.println("This is my first lambda expression");
        i.sayHii();

        MyInter i2 = () -> System.out.println("This is the second time using lambda");
        i2.sayHii();

        SumInter sumInter=(a,b)-> a+b;

        int ans=sumInter.sum(10,5);
        System.out.println("After calculating the answer is: "+ans);

        LenghtStr l1 =(str) -> str.length();

        int strlenghth = l1.getLength("Sujal");
        System.out.println("Length of the String: "+strlenghth);
    }
}
