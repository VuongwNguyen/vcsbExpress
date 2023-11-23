package com.example.aexpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aexpress.R;
import com.example.aexpress.activities.MainActivity;
import com.example.aexpress.databinding.ItemCartBinding;
import com.example.aexpress.model.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<Product> products;
    CartListener cartListener;
    Cart cart;


    public interface CartListener {
        public void onQuantityChanged();
    }


    public CartAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);

        holder.binding.name.setText(product.getName());
        DecimalFormat decimalFormat = new DecimalFormat("#,### VNĐ");
        holder.binding.price.setText(decimalFormat.format(product.getPrice() - product.getDiscount()));
        holder.binding.quantity.setText(product.getQuantity() + " item(s)");
        holder.binding.quantityEdit.setText(String.valueOf(product.getQuantity()));

        holder.binding.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = product.getQuantity();
                quantity++;

                if (quantity > product.getStock()) {
                    Toast.makeText(context, "Max stock available: " + product.getStock(), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    product.setQuantity(quantity);
                }
                notifyDataSetChanged();
                cart.updateItem(product, product.getQuantity());
                cartListener.onQuantityChanged();
                MainActivity.countItem();

            }
        });

        holder.binding.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = product.getQuantity();
                if (quantity > 1)
                    quantity--;
                product.setQuantity(quantity);
                notifyDataSetChanged();
                cart.updateItem(product, product.getQuantity());
                cartListener.onQuantityChanged();
                MainActivity.countItem();
            }
        });
    }

    public void removeItem(int position) {
        Product productToRemove = products.get(position);
        cart.removeItem(productToRemove);
        products.remove(position);
        notifyItemRemoved(position);
        MainActivity.countItem();
        notifyDataSetChanged();
    }

    public void restoreItem(Product product, int position) {
        cart.addItem(product, product.getQuantity());
        products.add(position, product);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, products.size());
        MainActivity.countItem();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        ItemCartBinding binding;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }

}
