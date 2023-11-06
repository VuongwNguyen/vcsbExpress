package com.example.aexpress.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aexpress.R;
import com.example.aexpress.databinding.DetailsProductDialogBinding;
import com.example.aexpress.databinding.ItemProductBinding;
import com.example.aexpress.model.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<Product> products;
    Cart cart;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);
        holder.binding.label.setText(product.getName());
        holder.binding.price.setText(product.getPrice()-product.getDiscount()+" VNĐ");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailsProductDialogBinding detailsProductDialogBinding = DetailsProductDialogBinding.inflate(LayoutInflater.from(context));
                AlertDialog dialog = new AlertDialog.Builder(context).
                        setView(detailsProductDialogBinding.getRoot()).create();
                detailsProductDialogBinding.tvNameProduct.setText(product.getName());
                detailsProductDialogBinding.productDescription.setText(Html.fromHtml(product.getDescription()));
                detailsProductDialogBinding.tvPriceProduct.setText(product.getPrice() - product.getDiscount() + " VNĐ");
                detailsProductDialogBinding.tvDiscountProduct.setText(Html.fromHtml("<s>" + product.getPrice() + " VNĐ</s>"));
                Glide.with(detailsProductDialogBinding.getRoot()).load(product.getImage())
                        .into(detailsProductDialogBinding.productImage);
                detailsProductDialogBinding.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (checkContainKey(product) != null) {
                    detailsProductDialogBinding.addToCartBtn.setText("Added in cart");
                    detailsProductDialogBinding.addToCartBtn.setEnabled(false);
                } else {
                    detailsProductDialogBinding.addToCartBtn.setText("Add to cart");
                    detailsProductDialogBinding.addToCartBtn.setEnabled(true);
                }
                detailsProductDialogBinding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cart.addItem(product,1);
                        detailsProductDialogBinding.addToCartBtn.setEnabled(false);
                        detailsProductDialogBinding.addToCartBtn.setText("Added in cart");
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ItemProductBinding binding;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductBinding.bind(itemView);
        }
    }

    private Product checkContainKey(Product product) {
        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Product product1 = (Product) item.getKey();
            if (product1.getId() == product.getId()) {
                return product1;
            }
        }
        return null;
    }
}
