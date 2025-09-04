package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        System.out.println("Hello and welcome!");
//        Student s1 = new Student();
//        s1.setName("Sushil");
//        s1.setsAge(25);
//        s1.setRollNo(103);

//        Student s2 = null;
//        Configuration cfg = new Configuration();
//        cfg.addAnnotatedClass(org.example.Student.class);
//        cfg.configure();
//

//        SessionFactory sf = new Configuration()
//                .addAnnotatedClass(org.example.Student.class)
//                .configure()
//                .buildSessionFactory();
//
//        Session session = sf.openSession();


//        To create and save the records in the database
//        this is the logic of the code
//        Transaction transaction = session.beginTransaction();
//        session.persist(s1);
//        transaction.commit();

//          To read from the database this is the logic
//        s2 = session.find(Student.class,102);

//        s1 = session.find(Student.class,105);
        // To update any data in the database
//        Transaction is used mainly for the update in the database
        // create the database

        /*---------To update in the database*/
//        Transaction transaction = session.beginTransaction();
//        session.merge(s1);


        /*---------To delete from the database*/
        // To delete any data from the column from the database
        // we need to get from find and then the remove will be used
        // so firstly need to find the id and then we will remove it

//        session.remove(s1);

//        transaction.commit();

        /*---------Additional point */
        // If there is no data and need to create any record we can use the merge
        // it can also save the data in the database

//        session.close();
//        sf.close();
//        System.out.println(s1);


        Alien a1 = new Alien();
        Laptop l1 = new Laptop();

        l1.setBrand("Asus");
        l1.setModel("Rog");
        l1.setRam(16);

        a1.setAid(101);
        a1.setAname("Ram");
        a1.setTech("Java");
        a1.setLaptop(l1);

        /* Similarly applicable for another one which having the alien as table
        * and aid si primary key and the tech is optional which have been
        * configured using the transient that it will not store into the
        * database but can be used in the code
        *
        * */

        SessionFactory sf1 = new Configuration()
                .configure()
                .addAnnotatedClass(org.example.Alien.class)
                .buildSessionFactory();

        Session session2 = sf1.openSession();
        Transaction transaction1 = session2.beginTransaction();
        session2.persist(a1);
        transaction1.commit();
        session2.close();
        sf1.close();

    }
}