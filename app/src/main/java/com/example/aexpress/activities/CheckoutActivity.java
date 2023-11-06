package com.example.aexpress.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aexpress.R;
import com.example.aexpress.adapters.CartAdapter;
import com.example.aexpress.databinding.ActivityCheckoutBinding;
import com.example.aexpress.model.Product;
import com.example.aexpress.utils.Constants;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;
    double totalPrice = 0;
    final int tax = 11;
    ProgressDialog progressDialog;
    Cart cart;

    DecimalFormat decimalFormat = new DecimalFormat("#,### VNĐ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        products = new ArrayList<>();

        cart = TinyCartHelper.getCart();

        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        adapter = new CartAdapter(this, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText(String.format(decimalFormat.format(cart.getTotalPrice())));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);

        binding.subtotal.setText(String.format(decimalFormat.format(cart.getTotalPrice())));

        totalPrice = (cart.getTotalPrice().doubleValue() * tax / 100) + cart.getTotalPrice().doubleValue();
        binding.total.setText(decimalFormat.format(totalPrice));

        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidate()) {
                    processOrder();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Please fill out the form completely", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.dateBox.setFocusable(false);
        binding.dateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CheckoutActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                // Xử lý ngày đã chọn
                                calendar.set(year, month, dayOfMonth);
                                Locale vietnameseLocale = new Locale("vi", "VN");
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", vietnameseLocale);
                                String formattedDate = sdf.format(calendar.getTime());
                                binding.dateBox.setText(formattedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private boolean checkValidate() {
        boolean checkName, checkEmail, checkPhone, checkAddress, checkDate;
        Pattern numberRegex = Pattern.compile("^0+[0-9]{9}$");
        Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        String name = binding.nameBox.getText().toString();
        String email = binding.emailBox.getText().toString();
        String phone = binding.phoneBox.getText().toString();
        String address = binding.addressBox.getText().toString();
        String date = binding.dateBox.getText().toString();
        String comment = binding.commentBox.getText().toString();

        if (name.equals("")) {
            binding.nameBox.setError("Name is required");
            checkName = false;
        } else {
            checkName = true;
            binding.nameBox.setError(null);
        }

        if (email.equals("")) {
            binding.emailBox.setError("Email is required");
            checkEmail = false;
        } else if (!emailRegex.matcher(email).matches()) {
            binding.emailBox.setError("Invalid email address format");
            checkEmail = false;
        } else {
            binding.emailBox.setError(null);
            checkEmail = true;
        }

        if (phone.equals("")) {
            binding.phoneBox.setError("Phone Number is required");
            checkPhone = false;
        } else if (!numberRegex.matcher(phone).matches()) {
            binding.phoneBox.setError("Invalid phone number format");
            checkPhone = false;
        } else {
            binding.phoneBox.setError(null);
            checkPhone = true;
        }

        if (address.equals("")) {
            binding.addressBox.setError("Address is required");
            checkAddress = false;
        } else {
            binding.addressBox.setError(null);
            checkAddress = true;
        }

        if (date.equals("")) {
            binding.dateBox.setError("Date is required");
            checkDate = false;
        } else {
            binding.dateBox.setError(null);
            checkDate = true;
        }

        return checkName && checkPhone && checkDate && checkEmail && checkAddress;
    }

    void processOrder() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject productOrder = new JSONObject();
        JSONObject dataObject = new JSONObject();
        try {
            productOrder.put("address",binding.addressBox.getText().toString());
            productOrder.put("buyer",binding.nameBox.getText().toString());
            productOrder.put("comment", binding.commentBox.getText().toString());
            productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update", Calendar.getInstance().getTimeInMillis());
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
            productOrder.put("email", binding.emailBox.getText().toString());
            productOrder.put("phone", binding.phoneBox.getText().toString());
            productOrder.put("serial", "cab8c1a4e4421a3b");
            productOrder.put("shipping", "");
            productOrder.put("shipping_location", "");
            productOrder.put("shipping_rate", "0.0");
            productOrder.put("status", "WAITING");
            productOrder.put("tax", tax);
            productOrder.put("total_fees", totalPrice);

            JSONArray product_order_detail = new JSONArray();
            for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
                Product product = (Product) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);

                JSONObject productObj = new JSONObject();
                productObj.put("amount", quantity);
                productObj.put("price_item", product.getPrice());
                productObj.put("product_id", product.getId());
                productObj.put("product_name", product.getName());
                product_order_detail.put(productObj);
            }

            dataObject.put("product_order",productOrder);
            dataObject.put("product_order_detail",product_order_detail);

            Log.e("err", dataObject.toString());

        } catch (JSONException e) {}

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.POST_ORDER_URL, dataObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("success")) {
                        Toast.makeText(CheckoutActivity.this, "Success order.", Toast.LENGTH_SHORT).show();
                        String orderNumber = response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Successful")
                                .setCancelable(false)
                                .setMessage("Your order number is: " + orderNumber)
                        .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                                intent.putExtra("orderCode", orderNumber);
                                startActivity(intent);
                            }
                        }).show();
                    } else {
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Failed")
                                .setMessage("Something went wrong, please try again.")
                                .setCancelable(false)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                        Toast.makeText(CheckoutActivity.this, "Failed order.", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                    Log.e("res", response.toString());
                } catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Security","secure_code");
                return headers;
            }
        } ;

        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}