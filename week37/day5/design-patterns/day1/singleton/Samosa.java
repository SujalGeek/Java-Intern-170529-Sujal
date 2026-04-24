package singleton;

import java.io.Serializable;

/* public enum Samosa {
    
  INSTANCE
    // private static Samosa samosa;

    // constructor
  // because of doing the enum we have to comment this bro while using the reflection api we have added
  // the code in the constructor(before using the reflection api there is nothing in the constructor bro)
  // private Samosa(){
    //   if(samosa != null)
    //   {
    //     throw new RuntimeException("You are trying to break the singleton pattern");
    //   }
    // }


    // do not need the object creation
    // LAZY Way of creating the Object  
    // while using the enum we have commented the below line of code
    // public static Samosa getSamosa(){

    //     // it will create the object of this class
    //   if(samosa == null)
    //   {
    //     synchronized (Samosa.class) {
    //         if(samosa == null)
    //         {
    //           samosa = new Samosa();
    //         }
    //     }
    //     // samosa = new Samosa();
    //   }
    //   return samosa;
    // }
}
*/


public class Samosa implements Serializable, Cloneable{
  private static Samosa samosa;

  private Samosa(){

  }

  public static Samosa getSamosa(){
      if(samosa == null)
      {
        synchronized (Samosa.class) {
            if(samosa == null)
            {
              samosa = new Samosa();
            }
        }
        // samosa = new Samosa();
      }
       return samosa;
    }
   
    public Object readResolve(){
    return samosa;
    }

    public  Object clone() throws CloneNotSupportedException{
      return super.clone();
    }

}

/*
1. constructor private

2. Object create with the help of method:

3. Create field to store object is private

4. DURING THE LAZY WAY WHEN METHOD IS CALLIN THEN THE OBJECT IS CREATED AND AFTER THEN THE OBJECT WILL NOT 
BE CREATED THEN THE SAME OBJECT WILL BE GETTING IT BRO
 IT IS NOT SPECIFIC TO THE THREAD ENVIRONMENT 
 IF THERE IS ONLY MORE THAN 2 THREADS THEN ARE THE LAZY WAY WILL CREATE THE PROBLEM 
 THEN THE OBJECT CREATED TWICE WHEN (WHILE CALLING DURING THE THREAD) THEN THE SINGLETON PRINCIPLE IS 
 BREAKING SO NEED TO CREATE THE SYNCRONZIED WAY TO CREATING THE THREADS

 IF THE THREAD SAFETY IS NOT CONCERN THEN THE LAZY WAY OF INTIALIZING IS GOOD

 SO WE ARE GOING TO USE THE SYNCROZIED WAY HERE BRO

*/