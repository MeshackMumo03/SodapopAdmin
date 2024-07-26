package com.example.sodapopadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private DatabaseReference ordersRef;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
        this.ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
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
        if (order == null || order.getId() == null) {
            Toast.makeText(itemView.getContext(), "Invalid order data", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentStatus = order.getStatus();
        if (currentStatus == null) {
            currentStatus = "Pending"; // Default to "Pending" if status is null
        }

        String newStatus = currentStatus.equals("Pending") ? "Completed" : "Pending";

        ordersRef.child(order.getId()).child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    order.setStatus(newStatus); // Update the local object
                    notifyDataSetChanged(); // Refresh the RecyclerView
                    Toast.makeText(itemView.getContext(), "Status updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteOrder(Order order, View itemView) {
        if (order == null || order.getId() == null) {
            Toast.makeText(itemView.getContext(), "Invalid order data", Toast.LENGTH_SHORT).show();
            return;
        }

        ordersRef.child(order.getId()).removeValue()
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