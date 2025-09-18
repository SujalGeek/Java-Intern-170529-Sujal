package com.springcore.auto.wire.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Emp {
    private Address address;

    // Constructor injection
    @Autowired
    public Emp(@Qualifier("address1") Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Emp [address=" + address + "]";
    }
}
