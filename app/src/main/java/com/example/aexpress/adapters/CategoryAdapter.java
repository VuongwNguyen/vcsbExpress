package com.example.aexpress.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.aexpress.R;
import com.example.aexpress.databinding.ItemCategoriesBinding;
import com.example.aexpress.model.Category;
import com.example.aexpress.model.Product;
import com.example.aexpress.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context context;
    ArrayList<Category> categories;
    RecyclerView rcv;
    TextView tv;
    ArrayList<Product> products;
    ProductAdapter productAdapter;

    public CategoryAdapter(Context context, ArrayList<Category> categories, RecyclerView rcv,TextView tv) {
        this.context = context;
        this.categories = categories;
        this.rcv = rcv;
        this.tv = tv;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_categories, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        products = new ArrayList<>();
        holder.binding.label.setText(Html.fromHtml(category.getName()));
        Glide.with(context)
                .load(category.getIcon())
                .into(holder.binding.image);

        holder.binding.image.setBackgroundColor(Color.parseColor(category.getColor()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                productAdapter = new ProductAdapter(context, products);
                products.clear();
                GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
                getProductsByCategory(category.getId());
                rcv.setLayoutManager(layoutManager);
                rcv.setAdapter(productAdapter);
                tv.setText(category.getName());
                tv.setBackgroundColor(Color.parseColor(category.getColor()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoriesBinding binding;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoriesBinding.bind(itemView);
        }
    }

    void getProductsByCategory(int id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = Constants.GET_PRODUCTS_URL + "?category_id=" + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray productsArray = object.getJSONArray("products");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        Product product = new Product(
                                childObj.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString("image"),
                                childObj.getString("status"),
                                childObj.getDouble("price"),
                                childObj.getDouble("price_discount"),
                                childObj.getInt("stock"),
                                childObj.getInt("id"),
                                childObj.getString("description")
                        );
                        products.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });

        queue.add(request);
    }
}
