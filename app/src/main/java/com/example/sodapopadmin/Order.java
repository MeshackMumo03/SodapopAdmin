package com.example.sodapopadmin;

public class Order {
    public String id;
    public String name;
    public String drink;
    public String branch;
    public String amount;
    public String status;

    public Order() {

    }

    public Order(String id, String name, String drink, String branch, String amount, String status) {
        this.id = id;
        this.name = name;
        this.drink = drink;
        this.branch = branch;
        this.amount = amount;
        this.status = status;
    }


}