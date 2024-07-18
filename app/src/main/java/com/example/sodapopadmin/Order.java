package com.example.sodapopadmin;

public class Order {
    public String id;
    public String name;
    public String drink;
    public String branch;
    public String amount;
    public String status;

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(String id, String name, String drink, String branch, String amount, String status) {
        this.id = id;
        this.name = name;
        this.drink = drink;
        this.branch = branch;
        this.amount = amount;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDrink() { return drink; }
    public String getBranch() { return branch; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDrink(String drink) { this.drink = drink; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setAmount(String amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
}