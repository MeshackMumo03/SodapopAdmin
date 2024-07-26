package com.example.sodapopadmin;

import java.util.HashMap;
import java.util.Map;

public class Stock {
    private String id;
    private String itemName;
    private int totalStock;
    private Map<String, Integer> branchStock;
    private static int lowStockThreshold = 10; // Default value

    public Stock() {
        // Default constructor required for Firebase
        this.branchStock = new HashMap<>();
    }

    public Stock(String id, String itemName, int totalStock) {
        this.id = id;
        this.itemName = itemName;
        this.totalStock = totalStock;
        this.branchStock = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public Map<String, Integer> getBranchStock() {
        if (branchStock == null) {
            branchStock = new HashMap<>();
        }
        return branchStock;
    }

    public void setBranchStock(Map<String, Integer> branchStock) {
        this.branchStock = branchStock != null ? branchStock : new HashMap<>();
    }

    public static void setLowStockThreshold(int threshold) {
        lowStockThreshold = threshold;
    }

    public boolean isLowStock() {
        return totalStock < lowStockThreshold;
    }

    public boolean isLowStockInBranch(String branch) {
        Integer branchAmount = getBranchStock().get(branch);
        return branchAmount != null && branchAmount < lowStockThreshold;
    }

    public void distributeStock(String branch, int amount) {
        if (amount <= totalStock) {
            totalStock -= amount;
            getBranchStock().put(branch, getBranchStock().getOrDefault(branch, 0) + amount);
        }
    }
}