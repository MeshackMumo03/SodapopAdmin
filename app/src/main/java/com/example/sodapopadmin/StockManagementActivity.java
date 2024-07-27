package com.example.sodapopadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StockManagementActivity extends AppCompatActivity {

    private EditText etItemName, etTotalStock, etDistributeAmount, etThreshold;
    private Spinner spinnerBranch;
    private Button btnAddStock, btnDistribute, btnUpdateThreshold;
    private RecyclerView rvStockList;
    private TextView tvWarning;

    private DatabaseReference stockRef;
    private List<Stock> stockList;
    private StockAdapter stockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_management);

        initializeViews();
        setupRecyclerView();
        setupSpinner();
        setupButtons();
        loadStockData();
    }

    private void initializeViews() {
        etItemName = findViewById(R.id.etItemName);
        etTotalStock = findViewById(R.id.etTotalStock);
        etDistributeAmount = findViewById(R.id.etDistributeAmount);
        etThreshold = findViewById(R.id.etThreshold);
        spinnerBranch = findViewById(R.id.spinnerBranch);
        btnAddStock = findViewById(R.id.btnAddStock);
        btnDistribute = findViewById(R.id.btnDistribute);
        btnUpdateThreshold = findViewById(R.id.btnUpdateThreshold);
        rvStockList = findViewById(R.id.rvStockList);
        tvWarning = findViewById(R.id.tvWarning);
    }

    private void setupRecyclerView() {
        stockRef = FirebaseDatabase.getInstance().getReference().child("stock");
        stockList = new ArrayList<>();
        stockAdapter = new StockAdapter(stockList);
        rvStockList.setLayoutManager(new LinearLayoutManager(this));
        rvStockList.setAdapter(stockAdapter);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.branches_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(adapter);
    }

    private void setupButtons() {
        btnAddStock.setOnClickListener(v -> addNewStock());
        btnDistribute.setOnClickListener(v -> distributeStock());
        btnUpdateThreshold.setOnClickListener(v -> updateThreshold());
    }

    private void addNewStock() {
        String itemName = etItemName.getText().toString().trim();
        String totalStockStr = etTotalStock.getText().toString().trim();

        if (itemName.isEmpty() || totalStockStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalStock = Integer.parseInt(totalStockStr);
        String id = stockRef.push().getKey();
        Stock newStock = new Stock(id, itemName, totalStock);

        stockRef.child(id).setValue(newStock)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StockManagementActivity.this, "Stock added successfully", Toast.LENGTH_SHORT).show();
                    etItemName.setText("");
                    etTotalStock.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(StockManagementActivity.this, "Failed to add stock", Toast.LENGTH_SHORT).show());
    }

    private void distributeStock() {
        String selectedBranch = spinnerBranch.getSelectedItem().toString();
        String amountStr = etDistributeAmount.getText().toString().trim();

        if (selectedBranch.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        Stock selectedStock = stockAdapter.getSelectedStock();

        if (selectedStock == null) {
            Toast.makeText(this, "Please select a stock item", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount > selectedStock.getTotalStock()) {
            Toast.makeText(this, "Not enough stock to distribute", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedStock.distributeStock(selectedBranch, amount);
        stockRef.child(selectedStock.getId()).setValue(selectedStock)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StockManagementActivity.this, "Stock distributed successfully", Toast.LENGTH_SHORT).show();
                    etDistributeAmount.setText("");
                    checkLowStock();
                })
                .addOnFailureListener(e -> Toast.makeText(StockManagementActivity.this, "Failed to distribute stock", Toast.LENGTH_SHORT).show());
    }

    private void updateThreshold() {
        String thresholdStr = etThreshold.getText().toString().trim();
        if (thresholdStr.isEmpty()) {
            Toast.makeText(this, "Please enter a threshold value", Toast.LENGTH_SHORT).show();
            return;
        }

        int newThreshold = Integer.parseInt(thresholdStr);
        Stock.setLowStockThreshold(newThreshold);

        Toast.makeText(this, "Stock threshold updated successfully", Toast.LENGTH_SHORT).show();
        checkLowStock();
    }

    private void loadStockData() {
        stockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stockList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Stock stock = snapshot.getValue(Stock.class);
                    if (stock != null) {
                        stockList.add(stock);
                    }
                }
                stockAdapter.notifyDataSetChanged();
                checkLowStock();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StockManagementActivity.this, "Failed to load stock data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLowStock() {
        StringBuilder warningMessage = new StringBuilder();
        for (Stock stock : stockList) {
            if (stock.isLowStock()) {
                warningMessage.append("Low total stock for ").append(stock.getItemName()).append("\n");
            }
            Map<String, Integer> branchStock = stock.getBranchStock();
            if (branchStock != null) {
                for (String branch : branchStock.keySet()) {
                    if (stock.isLowStockInBranch(branch)) {
                        warningMessage.append("Low stock for ").append(stock.getItemName())
                                .append(" in ").append(branch).append("\n");
                    }
                }
            }
        }

        if (warningMessage.length() > 0) {
            tvWarning.setText(warningMessage.toString());
            tvWarning.setVisibility(View.VISIBLE);
        } else {
            tvWarning.setVisibility(View.GONE);
        }
    }
}