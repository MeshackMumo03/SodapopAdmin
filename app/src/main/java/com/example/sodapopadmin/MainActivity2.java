package com.example.sodapopadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.admin.v1.Index;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private Button view_reports_button;
    private List<StructuredQuery.Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        db = FirebaseFirestore.getInstance();
        ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(orderList);
        ordersRecyclerView.setAdapter(orderAdapter);

        Button viewReportsButton = findViewById(R.id.view_reports_button);
        viewReportsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, ReportsActivity.class)));

        loadOrders();
    }

    private void loadOrders() {
        db.collection("orders").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            StructuredQuery.Order order = document.toObject(StructuredQuery.Order.class);
                            orderList.add(order);
                        }
                        orderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            loadOrders();
            return true;
        } else if (item.getItemId() == R.id.action_inventory) {
            startActivity(new Intent(this, InventoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}