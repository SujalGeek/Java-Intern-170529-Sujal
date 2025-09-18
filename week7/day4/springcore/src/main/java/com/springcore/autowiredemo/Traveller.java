package com.springcore.autowiredemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Traveller {

    @Autowired
    @Qualifier("car")   // choose which Vehicle to inject (car/bike)
    private Vehicle vehicle;

    public void startJourney() {
        vehicle.move();
        System.out.println("Traveller started the journey!");
    }
}
