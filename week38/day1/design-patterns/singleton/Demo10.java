package singleton;

import java.io.Serializable;

public class Demo10 implements  Serializable,Cloneable{
    public static void main(String[] args) throws Exception {
        
    //   Samosa samosa1 = Samosa.getSamosa();
    // System.out.println(samosa1.hashCode());

    // Samosa samosa2 = Samosa.getSamosa();
    // System.out.println(samosa2.hashCode());

    // System.out.println(Jalebi.getJalebi().hashCode());
    // System.out.println(Jalebi.getJalebi().hashCode());

    /*
    1. REFLECTION API
      solution-> if there is the object then we will throw as the Exception
      from (i) inside the constructor or (ii) use the enum

    2. Deserialization
    solution: implement readResolve method

    3. cloning


    
    */
   // While doing the reflection api check out the below code
  //  Samosa s1 = Samosa.getSamosa();
  //  System.out.println(s1.hashCode());

  //  Constructor<Samosa> constructor= Samosa.class.getDeclaredConstructor();
  //  constructor.setAccessible(true); // As the constructor is private
  //    Samosa s2 = constructor.newInstance();
  //   System.out.println(s2.hashCode());

  // Samosa s1 = Samosa.INSTANCE;
  // System.out.println(s1.hashCode());
  // Constructor<Samosa> constructor= Samosa.class.getDeclaredConstructor();
  //  constructor.setAccessible(true); // As the constructor is private
  //   Samosa s2 = constructor.newInstance();
  //   System.out.println(s2.hashCode());

  Samosa samosa = Samosa.getSamosa();
  System.out.println(samosa.hashCode());
  // ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("abc.ob"));
  // oos.writeObject(samosa); 

  // System.out.println("Serailzation done...");
  // ObjectInputStream ois = new ObjectInputStream(new FileInputStream("abc.ob"));
  // Samosa s2 = (Samosa) ois.readObject();
  // System.out.println(s2.hashCode());

 Samosa s3 = (Samosa)samosa.clone();
 System.out.println(s3.hashCode());
     
    
}
}
