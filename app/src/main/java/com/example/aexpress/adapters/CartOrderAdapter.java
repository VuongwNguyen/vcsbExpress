package com.example.aexpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aexpress.R;
import com.example.aexpress.databinding.ItemCartorderBinding;
import com.example.aexpress.model.ProductOrder;

import java.util.ArrayList;

public class CartOrderAdapter extends RecyclerView.Adapter<CartOrderAdapter.CartOrderViewHolder> {
    Context context;
    ArrayList<ProductOrder> productOrders;

    public CartOrderAdapter(Context context, ArrayList<ProductOrder> productOrders) {
        this.context = context;
        this.productOrders = productOrders;
    }

    @NonNull
    @Override
    public CartOrderAdapter.CartOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cartorder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartOrderAdapter.CartOrderViewHolder holder, int position) {
        holder.binding.tvIDCartOrder.setText(String.valueOf(productOrders.get(position).getId()));
        holder.binding.tvBuyerCartOrder.setText(productOrders.get(position).getBuyer());
        holder.binding.tvCodeCartOrder.setText(productOrders.get(position).getCode());
        holder.binding.tvCreatedAtCartOrder.setText(String.valueOf(productOrders.get(position).getCreated_at()));
        holder.binding.tvTotalFeesCartOrder.setText(String.valueOf(productOrders.get(position).getTotal_fees()));
    }

    @Override
    public int getItemCount() {
        return productOrders.size();
    }

    public class CartOrderViewHolder extends RecyclerView.ViewHolder {
        ItemCartorderBinding binding;
        public CartOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartorderBinding.bind(itemView);
        }
    }
}
