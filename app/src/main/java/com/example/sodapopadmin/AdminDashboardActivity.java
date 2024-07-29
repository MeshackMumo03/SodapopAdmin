package com.example.sodapopadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private DatabaseReference ordersRef;
    private List<Order> orderList;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private Spinner branchSpinner, statusSpinner, drinkSpinner;
    private Button btnManageStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        orderList = new ArrayList<>();

        recyclerView = findViewById(R.id.orderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        btnManageStock = findViewById(R.id.btnManageStock);
        btnManageStock.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, StockManagementActivity.class);
            startActivity(intent);
        });

        setupSpinners();
        loadOrders();
    }

    private void setupSpinners() {
        branchSpinner = findViewById(R.id.branchSpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        drinkSpinner = findViewById(R.id.drinkSpinner);


        ((TextView)findViewById(R.id.branchLabel)).setText("Branch:");
        ((TextView)findViewById(R.id.statusLabel)).setText("Status:");
        ((TextView)findViewById(R.id.drinkLabel)).setText("Drink:");

        ArrayAdapter<CharSequence> branchAdapter = ArrayAdapter.createFromResource(this,
                R.array.branches_array, android.R.layout.simple_spinner_item);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        ArrayAdapter<CharSequence> drinkAdapter = ArrayAdapter.createFromResource(this,
                R.array.drinks_array, android.R.layout.simple_spinner_item);
        drinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkSpinner.setAdapter(drinkAdapter);

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        branchSpinner.setOnItemSelectedListener(filterListener);
        statusSpinner.setOnItemSelectedListener(filterListener);
        drinkSpinner.setOnItemSelectedListener(filterListener);
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
                orderAdapter.notifyDataSetChanged();
                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void applyFilters() {
        String selectedBranch = branchSpinner.getSelectedItem().toString();
        String selectedStatus = statusSpinner.getSelectedItem().toString();
        String selectedDrink = drinkSpinner.getSelectedItem().toString();

        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : orderList) {
            boolean matchesBranch = selectedBranch.equals("All") ||
                    (order.getBranch() != null && order.getBranch().equals(selectedBranch));
            boolean matchesStatus = selectedStatus.equals("All") ||
                    (order.getStatus() != null && order.getStatus().equals(selectedStatus));
            boolean matchesDrink = selectedDrink.equals("All") ||
                    (order.getDrink() != null && order.getDrink().equals(selectedDrink));

            if (matchesBranch && matchesStatus && matchesDrink) {
                filteredOrders.add(order);
            }
        }

        orderAdapter.updateOrders(filteredOrders);
    }

}