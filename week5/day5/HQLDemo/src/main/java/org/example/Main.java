package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        System.out.println("Hello");


//        Alien a1 = new Alien();
//        Laptop l1 = new Laptop();
//
//        l1.setLid(8);
//        l1.setBrand("Asus1235");
//        l1.setModel("Stripe");
//        l1.setRam(16);

//        l1.setLid(21);



        SessionFactory sf1 = new Configuration()
                .configure()
                .addAnnotatedClass(org.example.Laptop.class)
                .buildSessionFactory();

        Session session2 = sf1.openSession();
//        Transaction transaction1 = session2.beginTransaction();
//        session2.persist(l1);
//        transaction1.commit();

//
//        Laptop l1 = session2.find(Laptop.class,3);
//        System.out.println(l1);

        // select * from laptop where ram =8;
        // from Laptop where ram = 8;

//        Query query = session2.createQuery("from Laptop where ram = 17",Laptop.class);
//        List<Laptop> l2 = query.getResultList();
//        System.out.println(l2);
//
//        Query query1 = session2.createQuery("from Laptop where brand like 'Asus'",Laptop.class);
//        List<Laptop> l3 = query1.getResultList();
//
//        System.out.println(l3);
//
//        String brand = "Asus12";
//        Query query2 = session2.createQuery("select brand, model from Laptop where brand like ?1", Object[].class);
//        query2.setParameter(1,brand);
//
//        List<Object[]> l4 = query2.getResultList();
//
//        for(Object[] a: l4)
//        {
//            System.out.println((String) a[0] + " "+a[1]);
//        }

        Session session3 = sf1.openSession();
        Laptop l4 = session3.find(Laptop.class,4);
        System.out.println(l4);

        Laptop l5 = session3.find(Laptop.class,3);
        System.out.println(l5);
        session3.close();
        session2.close();

        Session s4 = sf1.openSession();
        Laptop l6 = s4.find(Laptop.class,3);
        System.out.println(l6);

        s4.close();
        sf1.close();

        System.out.println("The code is working");
    }

}