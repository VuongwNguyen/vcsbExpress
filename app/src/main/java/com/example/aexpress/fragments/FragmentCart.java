package com.example.aexpress.fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aexpress.activities.CheckoutActivity;
import com.example.aexpress.adapters.CartAdapter;
import com.example.aexpress.databinding.FragmentCartBinding;
import com.example.aexpress.model.Product;
import com.google.android.material.snackbar.Snackbar;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class FragmentCart extends Fragment {

    private FragmentCartBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;

    private Product recentlyDeletedProduct;
    private int recentlyDeletedProductPosition;

    private DecimalFormat decimalFormat = new DecimalFormat("#,### VNĐ");

    public FragmentCart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Locale locale = new Locale("vi","VN");

        products = new ArrayList<>();

        Cart cart = TinyCartHelper.getCart();

        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
        }
        adapter = new CartAdapter(getContext(), products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {

                binding.subtotal.setText(NumberFormat.getCurrencyInstance(locale).format(cart.getTotalPrice()));
                binding.subtotal.setText(decimalFormat.format(cart.getTotalPrice()));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);

        binding.subtotal.setText(NumberFormat.getCurrencyInstance(locale).format(cart.getTotalPrice()));
        binding.subtotal.setText(decimalFormat.format(cart.getTotalPrice()));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Lưu trữ thông tin về item bị xóa
                recentlyDeletedProductPosition = viewHolder.getAdapterPosition();
                recentlyDeletedProduct = products.get(recentlyDeletedProductPosition);

                // Xóa item khỏi danh sách
                adapter.removeItem(recentlyDeletedProductPosition);

                // Hiển thị Snackbar để cho phép khôi phục
                Snackbar snackbar = Snackbar.make(requireView(), recentlyDeletedProduct.getName()+" removed", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Khôi phục item
                        adapter.restoreItem(recentlyDeletedProduct, recentlyDeletedProductPosition);

                        // Cập nhật lại subtotal khi item được khôi phục
                        binding.subtotal.setText(NumberFormat.getCurrencyInstance(locale).format(cart.getTotalPrice()));
                        binding.subtotal.setText(decimalFormat.format(cart.getTotalPrice()));
                    }
                });
                snackbar.show();

                // Cập nhật lại subtotal
                binding.subtotal.setText(NumberFormat.getCurrencyInstance(locale).format(cart.getTotalPrice()));
                binding.subtotal.setText(decimalFormat.format(cart.getTotalPrice()));
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.cartList);
        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CheckoutActivity.class));
            }
        });
    }
}