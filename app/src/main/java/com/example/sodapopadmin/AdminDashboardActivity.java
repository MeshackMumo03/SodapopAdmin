package com.example.sodapopadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private DatabaseReference ordersRef;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        orderList = new ArrayList<>();

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
        String selectedBranch = getSelectedBranch();
        String selectedStatus = getSelectedStatus();
        String selectedDrink = getSelectedDrink();

        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : orderList) {
            boolean matchesBranch = selectedBranch.equals("All") || order.getBranch().equals(selectedBranch);
            boolean matchesStatus = selectedStatus.equals("All") || order.getStatus().equals(selectedStatus);
            boolean matchesDrink = selectedDrink.equals("All") || order.getDrink().equals(selectedDrink);

            if (matchesBranch && matchesStatus && matchesDrink) {
                filteredOrders.add(order);
            }
        }

        updateOrderList(filteredOrders);
    }

    private String getSelectedBranch() {
        // Implementation to get selected branch
        return "All"; // Placeholder
    }

    private String getSelectedStatus() {
        // Implementation to get selected status
        return "All"; // Placeholder
    }

    private String getSelectedDrink() {
        // Implementation to get selected drink
        return "All"; // Placeholder
    }

    private void updateOrderList(List<Order> filteredOrders) {
        // Implementation to update the UI with filtered orders
    }
}