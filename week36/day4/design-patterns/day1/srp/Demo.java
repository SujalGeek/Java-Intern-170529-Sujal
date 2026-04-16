public class Demo{

     public void register(){
        System.out.println("User Registered!! ");
    }

    public void saveToDB(String name)
    {
        System.out.print("Saved to DB::!!!");
    }

    public static void main(String[] args) {        

    // So here there is one class and two methods 

    // Now the Srp says that one class should have the one method like the given example below
}
}

class Demo1{
    public void register(){
        System.out.println("Waiting this to be registered and using the SRP!!");
    }
}

class Demo2{
    public void saveToDB(String name)
    {
        System.out.println("Saving to the DB using the SRP");
    }
}

/*
Basically the SRP says that A class have only responsibility and one reason to change
This improves maintainability , testability and reduces coupling

*/