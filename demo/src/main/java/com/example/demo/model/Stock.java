package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Stock {

    @Id
    private String symbol;
    private String name;
    private double price;

    public Stock() {}

    public Stock(String symbol,String name, double price) {
        this.symbol = symbol;
        this.name=name;
        this.price = price;
    }

}