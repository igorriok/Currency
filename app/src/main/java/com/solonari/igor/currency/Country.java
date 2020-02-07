package com.solonari.igor.currency;

public class Country {

    // I know you think this should be private, but I know what I am doing
    public String name;
    public Double value;

    public Country(){}

    public Country(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}
