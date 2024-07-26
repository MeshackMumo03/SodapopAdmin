package com.example.sodapopadmin;

import java.util.HashMap;
import java.util.Map;

public class Stock {
    private String id;
    private String itemName;
    private int totalStock;
    private Map<String, Integer> branchStock;

    public Stock() {
        // Default constructor required for calls to DataSnapshot.getValue(Stock.class)
    }

    public Stock(String id, String itemName, int totalStock) {
        this.id = id;
        this.itemName = itemName;
        this.totalStock = totalStock;
        this.branchStock = new HashMap<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }

    public Map<String, Integer> getBranchStock() { return branchStock; }
    public void setBranchStock(Map<String, Integer> branchStock) { this.branchStock = branchStock; }

    public void distributeStock(String branch, int amount) {
        if (amount <= totalStock) {
            branchStock.put(branch, branchStock.getOrDefault(branch, 0) + amount);
            totalStock -= amount;
        }
    }

    public boolean isLowStock() {
        return totalStock < 80;
    }

    public boolean isLowStockInBranch(String branch) {
        return branchStock.getOrDefault(branch, 0) < 20;
    }
}