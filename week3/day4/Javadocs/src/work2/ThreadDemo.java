package work2;

public class ThreadDemo {
    public static void main(String[] args)  {
        // //First Thread
        // Thread - Sujal
        Runnable r1 =() ->{
          // this is the body of thread
            for(int i=1;i<=10;i++)
            {
                System.out.println("Value of i is "+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread t = new Thread(r1);
        t.setName("Sujal");
        t.start();

    Runnable t2 =()->{

        try{
            for(int i=1;i<=10;i++)
            {
                System.out.println(i*2);
                Thread.sleep(2000);
            }
        }catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    };

    Thread thread2 = new Thread(t2);
//    thread2.setName();
    thread2.start();

    }
}
