package org.example;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import javax.persistence.Entity;

import java.util.List;

@Entity
public class Laptop {

    @Id
    private int lid;
    private String brand;
    private String model;
    private int ram;


    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setRam(int ram)
    {
        this.ram =ram;
    }
    public int getRam(){
        return ram;
    }

    @Override
    public String toString() {
        return "Laptop{" +
                "lid=" + lid +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", ram=" + ram +
                '}';
    }
}
