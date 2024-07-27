package com.example.sodapopadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private DatabaseReference ordersRef;
    private DatabaseReference stockRef;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
        this.ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
        this.stockRef = FirebaseDatabase.getInstance().getReference().child("stock");
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.nameTextView.setText("Name: " + (order.getName() != null ? order.getName() : "N/A"));
        holder.drinkTextView.setText("Drink: " + (order.getDrink() != null ? order.getDrink() : "N/A"));
        holder.branchTextView.setText("Branch: " + (order.getBranch() != null ? order.getBranch() : "N/A"));
        holder.amountTextView.setText("Amount: " + (order.getAmount() != null ? order.getAmount() : "N/A"));
        holder.statusTextView.setText("Status: " + (order.getStatus() != null ? order.getStatus() : "N/A"));

        holder.updateStatusButton.setOnClickListener(v -> updateOrderStatus(order, holder.itemView));
        holder.deleteButton.setOnClickListener(v -> deleteOrder(order, holder.itemView));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }

    private void updateOrderStatus(Order order, View itemView) {
        if (order == null) {
            Toast.makeText(itemView.getContext(), "Invalid order data", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = order.getId();
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(itemView.getContext(), "Order ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentStatus = order.getStatus();
        if (currentStatus == null) {
            currentStatus = "Pending";
        }

        String newStatus = currentStatus.equals("Pending") ? "Completed" : "Pending";

        ordersRef.child(orderId).child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    order.setStatus(newStatus);
                    notifyDataSetChanged();
                    Toast.makeText(itemView.getContext(), "Status updated successfully", Toast.LENGTH_SHORT).show();

                    if (newStatus.equals("Completed")) {
                        reduceStock(order);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void reduceStock(Order order) {
        stockRef.child(order.getDrink()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Stock stock = dataSnapshot.getValue(Stock.class);
                if (stock != null) {
                    int orderAmount = Integer.parseInt(order.getAmount());
                    String branch = order.getBranch();


                    int branchStock = stock.getBranchStock().getOrDefault(branch, 0);
                    if (branchStock >= orderAmount) {
                        stock.getBranchStock().put(branch, branchStock - orderAmount);


                        stock.setTotalStock(stock.getTotalStock() - orderAmount);


                        stockRef.child(order.getDrink()).setValue(stock);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteOrder(Order order, View itemView) {
        if (order == null) {
            Toast.makeText(itemView.getContext(), "Invalid order data", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = order.getId();
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(itemView.getContext(), "Order ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ordersRef.child(orderId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    orderList.remove(order);
                    notifyDataSetChanged();
                    Toast.makeText(itemView.getContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Failed to delete order: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, drinkTextView, branchTextView, amountTextView, statusTextView;
        Button updateStatusButton, deleteButton;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            drinkTextView = itemView.findViewById(R.id.drinkTextView);
            branchTextView = itemView.findViewById(R.id.branchTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            updateStatusButton = itemView.findViewById(R.id.updateStatusButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}