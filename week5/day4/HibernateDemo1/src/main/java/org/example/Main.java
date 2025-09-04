package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        System.out.println("Hello");


        Alien a1 = new Alien();
        Laptop l1 = new Laptop();

        l1.setLid(1);
        l1.setBrand("Asus1235");
        l1.setModel("Rog2S");
        l1.setRam(17);

        Laptop l2 = new Laptop();
        l2.setLid(19);
        l2.setBrand("Dell2");
        l2.setModel("XPS1");
        l2.setRam(33);

        Laptop l3 = new Laptop();
        l3.setLid(3);
        l3.setBrand("Apple");
        l3.setModel("Macbook Air");
        l3.setRam(8);


        a1.setAid(110);
        a1.setAname("Radhe");
        a1.setTech("Java");
//        a1.setLaptop(l1);

        Alien a2 = new Alien();
        a2.setAid(111);
        a2.setAname("Harsh");
        a2.setTech("Python");

        Alien a3 = new Alien();
        a3.setAid(112);
        a3.setAname("Kiran");
        a3.setTech("AI");

        a1.setLaptop(Arrays.asList(l1,l2));
        a2.setLaptop(Arrays.asList(l2,l3));
        a3.setLaptop(Arrays.asList(l1));



        l1.setAliens(Arrays.asList(a1,a3));
        l2.setAliens(Arrays.asList(a1,a2));
        l2.setAliens(Arrays.asList(a2));


        SessionFactory sf1 = new Configuration()
                .configure()
                .addAnnotatedClass(org.example.Alien.class)
                .addAnnotatedClass(org.example.Laptop.class)
                .buildSessionFactory();

        Session session2 = sf1.openSession();
        Transaction transaction1 = session2.beginTransaction();

        session2.persist(l1);
        session2.persist(l2);
        session2.persist(l3);

        session2.persist(a1);
        session2.persist(a2);
        session2.persist(a3);


        transaction1.commit();
        session2.close();
        sf1.close();

        System.out.println("The code is working");
    }

}