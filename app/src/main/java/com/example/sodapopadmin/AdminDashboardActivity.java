package com.example.sodapopadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference ordersRef;
    private FirebaseAuth auth;
    private Spinner branchSpinner, statusSpinner, drinkSpinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        auth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        orderList = new ArrayList<>();

        branchSpinner = findViewById(R.id.branchSpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        drinkSpinner = findViewById(R.id.drinkSpinner);
        orderRecyclerView = findViewById(R.id.orderRecyclerView);

        orderAdapter = new OrderAdapter(orderList);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderRecyclerView.setAdapter(orderAdapter);

        findViewById(R.id.applyFiltersButton).setOnClickListener(v -> applyFilters());

        loadOrders();
    }

    private void loadOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        order.setId(snapshot.getKey());
                        orderList.add(order);
                    }
                }
                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void applyFilters() {
        String selectedBranch = branchSpinner.getSelectedItem().toString();
        String selectedStatus = statusSpinner.getSelectedItem().toString();
        String selectedDrink = drinkSpinner.getSelectedItem().toString();

        List<Order> filteredList = new ArrayList<>();
        for (Order order : orderList) {
            if ((selectedBranch.equals("All") || order.getBranch().equals(selectedBranch)) &&
                    (selectedStatus.equals("All") || order.getStatus().equals(selectedStatus)) &&
                    (selectedDrink.equals("All") || order.getDrink().equals(selectedDrink))) {
                filteredList.add(order);
            }
        }

        orderAdapter.updateOrders(filteredList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
        finish();
    }
}