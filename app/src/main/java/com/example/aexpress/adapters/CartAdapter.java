package com.example.aexpress.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aexpress.R;
import com.example.aexpress.activities.CartActivity;
import com.example.aexpress.databinding.ItemCartBinding;
import com.example.aexpress.databinding.ItemCategoriesBinding;
import com.example.aexpress.databinding.ItemProductBinding;
import com.example.aexpress.databinding.QuantityDialogBinding;
import com.example.aexpress.model.Product;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.Collections;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> implements  ItemTouchHelperAdapter{

    Context context;
    ArrayList<Product> products;
    CartListener cartListener;
    Cart cart;



    public interface CartListener {
        public void onQuantityChanged();
    }


    public CartAdapter(Context context, ArrayList<Product> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);

        holder.binding.name.setText(product.getName());
        holder.binding.price.setText("PKR " + product.getPrice());
        holder.binding.quantity.setText(product.getQuantity() + " item(s)");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                QuantityDialogBinding quantityDialogBinding = QuantityDialogBinding.inflate(LayoutInflater.from(context));

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(quantityDialogBinding.getRoot())
                        .create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.transparent));

                quantityDialogBinding.productName.setText(product.getName());
                quantityDialogBinding.productStock.setText("Stock: " + product.getStock());
                quantityDialogBinding.quantity.setText(String.valueOf(product.getQuantity()));
                int stock = product.getStock();


                quantityDialogBinding.plusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = product.getQuantity();
                        quantity++;

                        if(quantity>product.getStock()) {
                            Toast.makeText(context, "Max stock available: "+ product.getStock(), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            product.setQuantity(quantity);
                            quantityDialogBinding.quantity.setText(String.valueOf(quantity));
                        }

                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });

                quantityDialogBinding.minusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = product.getQuantity();
                        if(quantity > 1)
                            quantity--;
                        product.setQuantity(quantity);
                        quantityDialogBinding.quantity.setText(String.valueOf(quantity));

                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });

                quantityDialogBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
//                        notifyDataSetChanged();
//                        cart.updateItem(product, product.getQuantity());
//                        cartListener.onQuantityChanged();
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

    public class CartViewHolder extends RecyclerView.ViewHolder {

        ItemCartBinding binding;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
    @Override
    public void onItemSwiped(int position, int direction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cảnh Bảo");
        builder.setMessage("Xác Nhận Xoá Mặt Hàng "+products.get(position).getName()+"!");
        notifyDataSetChanged();
        builder.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                products.remove(position);
                notifyDataSetChanged();
            }
        }).setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
// Di chuyển item trong danh sách dựa vào vị trí nguồn (fromPosition) và đích (toPosition).
        // Đầu tiên, lưu trữ item tại vị trí nguồn.
        String movedItem = String.valueOf(products.get(fromPosition));


        // Sử dụng Collections.swap để hoán đổi vị trí của hai item trong danh sách.
//        Collections.swap(products, fromPosition, toPosition);

        // Thông báo cho RecyclerView rằng item đã di chuyển để cập nhật giao diện.
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
        // Áp dụng hiệu ứng khi item được chọn (đang kéo và thả).
        viewHolder.itemView.setBackgroundColor(Color.CYAN);
        applyScaleAnimation(viewHolder.itemView, 1.8f); // Áp dụng animation co giãn
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
// Thực hiện các xử lý khi item không còn được chọn (kéo và thả kết thúc).
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
        applyScaleAnimation(viewHolder.itemView, 1.0f); // Đặt về kích thước ban đầu
    }
    private void applyScaleAnimation(View view, float scale) {
        Animation anim = new ScaleAnimation(
                1.0f, scale, // Bắt đầu và kết thúc scale theo trục X
                1.0f, scale, // Bắt đầu và kết thúc scale theo trục Y
                Animation.RELATIVE_TO_SELF, 0.5f, // Trung tâm scale theo trục X
                Animation.RELATIVE_TO_SELF, 0.5f // Trung tâm scale theo trục Y
        );
        anim.setFillAfter(true); // Giữ lại trạng thái mới sau khi animation kết thúc
        anim.setDuration(200); // Thời gian animation
        view.startAnimation(anim);
    }
}
