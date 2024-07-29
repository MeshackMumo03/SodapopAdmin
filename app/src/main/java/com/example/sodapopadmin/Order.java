package com.example.sodapopadmin;

public class Order {
    private String id;
    private String orderId;
    private String name;
    private String drink;
    private String branch;
    private String amount;
    private String status;

    public Order() {

    }

    public Order(String id, String orderId, String name, String drink, String branch, String amount, String status) {
        this.id = id;
        this.name = name;
        this.drink = drink;
        this.branch = branch;
        this.amount = amount;
        this.status = status;
        this.orderId = orderId;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDrink() { return drink; }
    public void setDrink(String drink) { this.drink = drink; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}
