package com.example.aexpress.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<Product> products;
    Cart cart;
    DecimalFormat decimalFormat;
    Handler handler;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        cart = TinyCartHelper.getCart();
        this.handler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Collections.shuffle(products);
        holder.binding.shimmerLayout.startShimmer();
        Product product = products.get(position);

        handler.postDelayed(() -> {
            if (product.getDiscount() != 0) {
                holder.binding.ivDiscount.setVisibility(View.VISIBLE);
            }
            Glide.with(context)
                    .load(product.getImage())
                    .into(holder.binding.image);
            holder.binding.label.setText(product.getName());
            double price = product.getPrice() - product.getDiscount();
            decimalFormat = new DecimalFormat("#,### VNĐ"); // Làm tròn số và thêm đơn vị tiền tệ
            String formattedPrice = decimalFormat.format(price);
            holder.binding.price.setText(formattedPrice);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DetailsProductDialogBinding detailsProductDialogBinding = DetailsProductDialogBinding.inflate(LayoutInflater.from(context));
                    AlertDialog dialog = new AlertDialog.Builder(context).setView(detailsProductDialogBinding.getRoot()).create();
                    if (product.getDiscount() == 0) {
                        detailsProductDialogBinding.tvDiscountProduct.setVisibility(View.GONE);
                    }
                    detailsProductDialogBinding.tvNameProduct.setText(product.getName());
                    detailsProductDialogBinding.productDescription.setText(Html.fromHtml(product.getDescription()));
                    detailsProductDialogBinding.tvPriceProduct.setText(product.getPrice() - product.getDiscount() + "");
                    detailsProductDialogBinding.tvDiscountProduct.setText(Html.fromHtml("<s>" + product.getPrice() + "</s>"));
                    detailsProductDialogBinding.tvStock.setText("Current also: " + product.getStock() + " Cups");
                    detailsProductDialogBinding.ratingBar.setRating((float) Math.random() * 5);
                    Glide.with(detailsProductDialogBinding.getRoot()).load(product.getImage()).into(detailsProductDialogBinding.productImage);
                    detailsProductDialogBinding.tvPriceProduct.setText(decimalFormat.format(product.getPrice() - product.getDiscount()));
                    detailsProductDialogBinding.tvDiscountProduct.setText(Html.fromHtml("<s>" + decimalFormat.format(product.getPrice()) + "</s>"));
                    detailsProductDialogBinding.tvStock.setText("Current also: " + product.getStock() + " Cups");
                    detailsProductDialogBinding.ratingBar.setRating((float) Math.random() * 5);
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
                            cart.addItem(product, 1);
                            detailsProductDialogBinding.addToCartBtn.setEnabled(false);
                            detailsProductDialogBinding.addToCartBtn.setText("Added in cart");
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            holder.binding.shimmerLayout.stopShimmer();
            holder.binding.shimmerLayout.setShimmer(null);
        }, 350 * position);
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
