package com.example.aexpress.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aexpress.R;
import com.example.aexpress.adapters.CartOrderAdapter;
import com.example.aexpress.databinding.FragmentCategoryBinding;
import com.example.aexpress.databinding.FragmentFragmentsCartOrderBinding;
import com.example.aexpress.model.Category;
import com.example.aexpress.model.ProductOrder;
import com.example.aexpress.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentsCartOrder extends Fragment {
    FragmentFragmentsCartOrderBinding binding;
    ArrayList<ProductOrder> productOrders;
    CartOrderAdapter cartorderAdapter;

    public FragmentsCartOrder() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFragmentsCartOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productOrders = new ArrayList<>();
        getCartOrder();
        RecyclerView recyclerView;
        recyclerView = binding.cartorderList;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        cartorderAdapter = new CartOrderAdapter(getContext(), productOrders);
        recyclerView.setAdapter(cartorderAdapter);
    }

    public void getCartOrder() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_PRODUCTS_ORDER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("err", response);
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")) {
                        JSONArray productOrderArray = object.getJSONArray("product_order");
                        for (int i = 0; i < productOrderArray.length(); i++) {
                            JSONObject childObj = productOrderArray.getJSONObject(i);
                            ProductOrder productOrder = new ProductOrder(
                                    childObj.getInt("id"),
                                    childObj.getInt("created_at"),
                                    childObj.getString("code"),
                                    childObj.getString("buyer"),
                                    childObj.getString("status"),
                                    childObj.getDouble("total_fees")
                            );
                            productOrders.add(productOrder);
                        }
                        cartorderAdapter.notifyDataSetChanged();
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
}
