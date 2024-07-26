package com.example.sodapopadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private List<Stock> stockList;
    private Stock selectedStock;

    public StockAdapter(List<Stock> stockList) {
        this.stockList = stockList;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.tvItemName.setText(stock.getItemName());
        holder.tvTotalStock.setText("Total: " + stock.getTotalStock());

        StringBuilder branchStockText = new StringBuilder();
        for (String branch : stock.getBranchStock().keySet()) {
            branchStockText.append(branch).append(": ").append(stock.getBranchStock().get(branch)).append("\n");
        }
        holder.tvBranchStock.setText(branchStockText.toString());

        holder.itemView.setOnClickListener(v -> {
            selectedStock = stock;
            notifyDataSetChanged();
        });

        holder.itemView.setBackgroundResource(selectedStock == stock ? R.color.selected_item : android.R.color.transparent);
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public Stock getSelectedStock() {
        return selectedStock;
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvTotalStock, tvBranchStock;

        StockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvTotalStock = itemView.findViewById(R.id.tvTotalStock);
            tvBranchStock = itemView.findViewById(R.id.tvBranchStock);
        }
    }
}