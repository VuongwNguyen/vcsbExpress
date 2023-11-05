package com.example.aexpress.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.aexpress.R;
import com.example.aexpress.databinding.ActivityProductDetailBinding;
import com.example.aexpress.model.Product;
import com.example.aexpress.utils.Constants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {


    ActivityProductDetailBinding binding;
    Product currentProduct;

    Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Product product = (Product) bundle.getSerializable("product");

        String name = product.getName();
        String image = product.getImage();
        String description = product.getDescription();
        double price = product.getPrice();
        double discount = product.getDiscount();
        Locale locale = new Locale("vi","VN");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        binding.tvNameProduct.setText(Html.fromHtml(name));
        binding.tvPriceProduct.setText(Html.fromHtml(String.valueOf(numberFormat.format(price - discount)+" VNĐ")));
        binding.tvDiscountProduct.setText(Html.fromHtml("<s>"+String.valueOf(numberFormat.format(price)+" VNĐ</s>")));

        Glide.with(this)
                .load(image)
                .into(binding.productImage);

        binding.productDescription.setText(Html.fromHtml(description));
//        getProductDetails(id);
//        initImagesProduct(product.getId());
        getSupportActionBar().setTitle(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cart cart = TinyCartHelper.getCart();


        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.addItem(product, 1);
                binding.addToCartBtn.setEnabled(false);
                binding.addToCartBtn.setText("Added in cart");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart) {
            startActivity(new Intent(this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    void getProductDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.GET_PRODUCT_DETAILS_URL + id;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")) {
                        JSONObject product = object.getJSONObject("product");
                        String description = product.getString("description");
                        binding.productDescription.setText(
                                Html.fromHtml(description)
                        );
                        currentProduct = new Product(
                                product.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL + product.getString("image"),
                                product.getString("status"),
                                product.getDouble("price"),
                                product.getDouble("price_discount"),
                                product.getInt("stock"),
                                product.getInt("id")
                        );
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
//    void initImagesProduct(int id){
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_IMAGES_PRODUCT_URL+id, response -> {
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getString("status").equals("success")) {
//                    JSONArray offerArray = object.getJSONArray("images");
//                    for (int i = 0; i < offerArray.length(); i++) {
//                        JSONObject childObj = offerArray.getJSONObject(i);
//                        binding.carousel.addData(
//                                new CarouselItem(
//                                        Constants.PRODUCTS_IMAGE_URL + childObj.getString("name")
//                                )
//                        );
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
//        });
//        queue.add(request);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}