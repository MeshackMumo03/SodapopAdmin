package com.example.sodapopadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firestore.v1.StructuredQuery;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<StructuredQuery.Order> orderList;

    public OrderAdapter(List<StructuredQuery.Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        StructuredQuery.Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView branchTextView, brandTextView, quantityTextView, customerIdTextView;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            branchTextView = itemView.findViewById(R.id.branch_text_view);
            brandTextView = itemView.findViewById(R.id.brand_text_view);
            quantityTextView = itemView.findViewById(R.id.quantity_text_view);
            customerIdTextView = itemView.findViewById(R.id.customer_id_text_view);
        }

        void bind(StructuredQuery.Order order) {
            branchTextView.setText("Branch: " + order.getBranch());
            brandTextView.setText("Brand: " + order.getBrand());
            quantityTextView.setText("Quantity: " + order.getQuantity());
            customerIdTextView.setText("Customer ID: " + order.getCustomerId());
        }
    }
}
