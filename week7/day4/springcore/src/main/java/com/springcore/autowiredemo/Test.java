package com.springcore.autowiredemo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Traveller traveller = context.getBean(Traveller.class);
        traveller.startJourney();
    }
}
