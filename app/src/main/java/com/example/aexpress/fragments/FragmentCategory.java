package com.example.aexpress.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aexpress.R;
import com.example.aexpress.adapters.CategoryAdapter;
import com.example.aexpress.adapters.ProductAdapter;
import com.example.aexpress.databinding.FragmentCategoryBinding;
import com.example.aexpress.model.Category;
import com.example.aexpress.model.Product;
import com.example.aexpress.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentCategory extends Fragment {
    private FragmentCategoryBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;

    ArrayList<Product> products;
    ProductAdapter productAdapter;

    public FragmentCategory() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), products);
        initCategories();
        getRecentProducts();
        binding.listDetailsCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.listDetailsCategories.setAdapter(productAdapter);
    }

    void initCategories() {
        Animation animationIn = AnimationUtils.loadAnimation(getContext(), R.anim.flip_in);
        Animation animationOut = AnimationUtils.loadAnimation(getContext(),R.anim.flip_out);
        categories = new ArrayList<>();
        getCategories();
        categoryAdapter = new CategoryAdapter(getContext(), categories, binding.listDetailsCategories, binding.tvNameCategory);
        binding.tvNameCategory.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               binding.viewflipper.setInAnimation(animationIn);
               binding.viewflipper.setOutAnimation(animationOut);
               binding.viewflipper.showNext();
            }
        });
        binding.viewflipper.setInAnimation(animationIn);
        binding.viewflipper.setOutAnimation(animationOut);
        binding.viewflipper.showNext();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
    }

    public void getCategories() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObj = new JSONObject(response);
                    if (mainObj.getString("status").equals("success")) {
                        JSONArray categoriesArray = mainObj.getJSONArray("categories");
                        for (int i = 0; i < categoriesArray.length(); i++) {
                            JSONObject object = categoriesArray.getJSONObject(i);
                            Category category = new Category(
                                    object.getString("name"),
                                    Constants.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                    object.getString("color"),
                                    object.getString("brief"),
                                    object.getInt("id")
                            );
                            if (!object.getString("name").equals("Flash Sale") && !(object.getString("name").equals("Best Seller"))) {
                                categories.add(category);
                            }
                        }
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        // DO nothing
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

    public void getRecentProducts() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = Constants.GET_PRODUCTS_URL + "?count=200";
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