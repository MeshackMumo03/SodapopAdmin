package com.example.sodapopadmin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView inventoryRecyclerView;
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> inventoryList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        db = FirebaseFirestore.getInstance();
        inventoryRecyclerView = findViewById(R.id.inventory_recycler_view);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inventoryAdapter = new InventoryAdapter(inventoryList);
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        loadInventory();
    }

    private void loadInventory() {
        db.collection("inventory").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        inventoryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            InventoryItem item = document.toObject(InventoryItem.class);
                            inventoryList.add(item);
                            if (item.getQuantity() < 20) {
                                showLowStockWarning(item);
                            }
                        }
                        inventoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading inventory", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLowStockWarning(InventoryItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Low Stock Warning")
                .setMessage("Stock for " + item.getName() + " is below 20 units.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}