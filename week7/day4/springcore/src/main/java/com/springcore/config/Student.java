package com.springcore.config;

public class Student {
    private String name;
    private String city;

    // Constructor
    public Student(String name, String city) {
        this.name = name;
        this.city = city;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Student [name=" + name + ", city=" + city + "]";
    }
}
