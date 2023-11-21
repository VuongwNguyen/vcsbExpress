package com.example.aexpress.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aexpress.R;
import com.example.aexpress.adapters.ProductAdapter;
import com.example.aexpress.adapters.ProductCategoryAdapter;
import com.example.aexpress.databinding.FragmentHomeBinding;
import com.example.aexpress.model.Product;
import com.example.aexpress.model.ProductCategory;
import com.example.aexpress.utils.Constants;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    private FragmentHomeBinding binding;
    RequestQueue queue;
    ArrayList<ProductCategory> ProductCate;
    ProductCategoryAdapter adapter;

    public FragmentHome() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initProductCategory();
        initSlider();
    }

    private void initProductCategory() {
        ProductCate = new ArrayList<>();
        getData();
        adapter = new ProductCategoryAdapter(requireContext(), ProductCate);
        binding.rvProductCate.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvProductCate.setAdapter(adapter);
    }

    private void getData() {
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_CATEGORIES_URL, response -> {
            try {
                Log.e("Data", "getData: "+response);
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray cateArr = object.getJSONArray("categories");
                    for (int i = 0; i < cateArr.length(); i++) {
                        JSONObject category = cateArr.getJSONObject(i);
                        int id = category.getInt("id");
                        String cate = category.getString("name");

                        getProductsByCategory(cate, id, new getProductCategory() {
                            @Override
                            public void getProductArray(ArrayList<Product> list, String category) {
                               if (!list.isEmpty()) {
                                   ProductCategory productCategory = new ProductCategory(category, list);
                                   ProductCate.add(productCategory);
                                   adapter.notifyDataSetChanged();
                               } else {
                                   Log.e("Category: "+category, "getProductArray: Empty");
                               }
                            }
                        });
                    }
                } else {
                    Toast.makeText(requireContext(), "No connected from server", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});

        queue.add(request);
    }

    private void getProductsByCategory(String category, int id, getProductCategory productCategory) {
        ArrayList<Product> products = new ArrayList<>();
        String url = Constants.GET_PRODUCTS_URL + "?category_id=" + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray array = object.getJSONArray("products");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject childObj = array.getJSONObject(i);
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
                    if(category.equals("Flash Sale") || category.equals("Best Seller")){
                        productCategory.getProductArray(products, category);
                    }
                } else {
                    Toast.makeText(requireContext(), "No connected from server", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e){
                Log.e("JSONObject Exception", "getProductsByCategory: "+"Không tìm thấy JSONObject getProductByCategory : "+e);
            }

        }, error -> {});
        queue.add(request);
    }

    public interface getProductCategory {
        void getProductArray(ArrayList<Product> list, String category);
    }



    private void initSlider() {
        getRecentOffers();
    }

//    public void getRecentProducts() {
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//
//        String url = Constants.GET_PRODUCTS_URL + "?count=8";
//        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getString("status").equals("success")) {
//                    JSONArray productsArray = object.getJSONArray("products");
//                    for (int i = 0; i < productsArray.length(); i++) {
//                        JSONObject childObj = productsArray.getJSONObject(i);
//                        Product product = new Product(
//                                childObj.getString("name"),
//                                Constants.PRODUCTS_IMAGE_URL + childObj.getString("image"),
//                                childObj.getString("status"),
//                                childObj.getDouble("price"),
//                                childObj.getDouble("price_discount"),
//                                childObj.getInt("stock"),
//                                childObj.getInt("id"),
//                                childObj.getString("description")
//                        );
//                        products.add(product);
//                    }
//                    productAdapter.notifyDataSetChanged();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
//        });
//
//        queue.add(request);
//    }

    void getRecentOffers() {
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray offerArray = object.getJSONArray("news_infos");
                    for (int i = 0; i < offerArray.length(); i++) {
                        JSONObject childObj = offerArray.getJSONObject(i);
                        binding.carousel.addData(
                                new CarouselItem(
                                        Constants.NEWS_IMAGE_URL + childObj.getString("image"),
                                        childObj.getString("title")
                                )
                        );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        queue.add(request);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        queue = Volley.newRequestQueue(context);
    }
}