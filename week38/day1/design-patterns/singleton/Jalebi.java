package singleton;


public class Jalebi {
 

    // EAGER WAY of creating singleton object 

    // WHAT IS THE CATCH HERE WE CAN IF THERE IS NO OBJECT CREATION ONE THEN ALSO THE OBJECT IS CREATED IN THE
    // CLASS LOADING
    private static Jalebi jalebi= new Jalebi();


    public static Jalebi getJalebi() {
        return jalebi;
    }
}
