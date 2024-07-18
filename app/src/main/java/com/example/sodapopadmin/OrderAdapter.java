package com.example.sodapopadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        holder.nameTextView.setText("Name: " + order.name);
        holder.drinkTextView.setText("Drink: " + order.drink);
        holder.branchTextView.setText("Branch: " + order.branch);
        holder.amountTextView.setText("Amount: " + order.amount);
        holder.statusTextView.setText("Status: " + order.status);

        holder.updateStatusButton.setOnClickListener(v -> updateOrderStatus(order));
        holder.deleteButton.setOnClickListener(v -> deleteOrder(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }

    private void updateOrderStatus(Order order) {
        // Toggle between "Pending" and "Completed"
        String newStatus = order.status.equals("Pending") ? "Completed" : "Pending";
        ordersRef.child(order.id).child("status").setValue(newStatus);
    }

    private void deleteOrder(Order order) {
        ordersRef.child(order.id).removeValue();
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