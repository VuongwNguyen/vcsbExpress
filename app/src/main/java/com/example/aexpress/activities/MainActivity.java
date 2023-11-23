package com.example.aexpress.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aexpress.R;
import com.example.aexpress.databinding.ActivityMainBinding;
import com.example.aexpress.fragments.FragmentCart;
import com.example.aexpress.fragments.FragmentCategory;
import com.example.aexpress.fragments.FragmentHome;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Cart cart;
    int itemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /* cái này nó bị thiểu năng nè :))))))) */
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                countItem(binding.quantity);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 2000);
        /* đừng thắc sao tui làm z tại nó chạy dc là dc :))
         *
         *
         * máy yếu gặp cái này là hết nước chấm =))) */

        binding.ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new FragmentCart());
            }
        });
        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_SPEECH) {
                    startTextFromSpeech();
                }
            }
        });

        replaceFragment(new FragmentHome());
        binding.bottomnavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home){
                replaceFragment(new FragmentHome());
                return true;
            } else if (item.getItemId()==R.id.categories) {
                replaceFragment(new FragmentCategory());
                return true;
            } else if (item.getItemId() == R.id.history) {
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                return true;
            }
            return false;
        });
        replaceFragment(new FragmentHome());
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    private void startTextFromSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói gì đó...");

        try {
            myLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            // Xử lý nếu không tìm thấy ứng dụng nhận dạng giọng nói trên thiết bị
            Toast.makeText(this, "Device is not supported", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> myLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            ArrayList<String> resultText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            if (resultText != null && resultText.size() > 0) {
                                String spokenText = resultText.get(0);
                                // Xử lý văn bản được nhận dạng (làm chức năng tìm kiếm)
                                binding.searchBar.openSearch();
                                binding.searchBar.setText(spokenText);
                            }
                        }
                    }
                }
            });

    public void countItem(TextView textView) {
        itemCount = 0;
        cart = TinyCartHelper.getCart();
        Map<Item, Integer> itemWithQty = cart.getAllItemsWithQty();
        for (int quantity : itemWithQty.values()) {
            itemCount += quantity;
        }
        textView.setText(String.valueOf(itemCount));
    }

}