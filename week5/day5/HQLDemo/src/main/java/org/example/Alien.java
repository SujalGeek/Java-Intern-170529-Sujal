package org.example;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
//@Table(name = "alien_table")
public class Alien {
    @Id
    private int aid;

    //    @Column(name = "alien_name")
    private String aname;

    //    @Transient
    private String tech;
    // For one to one relation this is necessary to include
//    @OneToOne
//    private Laptop laptop;
//    @OneToMany(mappedBy = "alien")
    @OneToMany(fetch = FetchType.EAGER)
    private List<Laptop> laptops;

    public List<Laptop> getLaptop() {
        return laptops;
    }

    public void setLaptop(List<Laptop> laptops) {
        this.laptops = laptops;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }

    @Override
    public String toString() {
        return "Alien{" +
                "aid=" + aid +
                ", aname='" + aname + '\'' +
                ", tech='" + tech + '\'' +
                '}';
    }
}
