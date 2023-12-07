package com.example.aexpress.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aexpress.R;
import com.example.aexpress.databinding.ItemRvCategoryProductBinding;
import com.example.aexpress.model.Category;
import com.example.aexpress.model.Product;
import com.example.aexpress.model.ProductCategory;

import java.util.ArrayList;

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ProductCategory> list;

    public ProductCategoryAdapter(Context context, ArrayList<ProductCategory> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_category_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCategoryAdapter.ViewHolder holder, int position) {
        ProductCategory productCategory = list.get(position);
        ArrayList<Product> products = productCategory.getProducts();
//        holder.binding.txtCate.setTypeface(Typeface.createFromAsset(context.getAssets(), "VNPARK.TTF"));
        holder.binding.txtCate.setText(productCategory.getCategory());
        ProductAdapter productAdapter = new ProductAdapter(context, products);
        holder.binding.rvProduct.setAdapter(productAdapter);
        holder.binding.rvProduct.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRvCategoryProductBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRvCategoryProductBinding.bind(itemView);
        }
    }
}
