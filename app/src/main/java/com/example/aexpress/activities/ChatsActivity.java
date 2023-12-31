package com.example.aexpress.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.aexpress.R;
import com.example.aexpress.databinding.ActivityChatsBinding;
import com.example.aexpress.db.DatabaseRepository;
import com.example.aexpress.model.LoggedInUserModel;
import com.example.aexpress.model.UserModel;


public class ChatsActivity extends AppCompatActivity {

    DatabaseRepository databaseRepository;
    Context context;
    LoggedInUserModel loggedInUserModel = null;
    String name;
    ActivityChatsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        databaseRepository = new DatabaseRepository(context);
        bind_views();
        get_logged_in_user();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Messages");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.logout) {
                    logout_user();
                    return true;
                }
                return false;
            }
        });
        binding.name.setText(name);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuchat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void get_logged_in_user() {

        databaseRepository.get_logged_in_user().observe((LifecycleOwner) context, new Observer<LoggedInUserModel>() {
            @Override
            public void onChanged(LoggedInUserModel u) {
                if (u == null) {
                    Toast.makeText(context, "You are not logged in.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                loggedInUserModel = u;
                binding.name.setText("Hello, "+ u.name);
            }
        });

    }

    private void bind_views() {
    findViewById(R.id.btn_pick_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_user();
            }
        });
    }

    int my_counter = 0;
    Handler handler = new Handler();

    private void pick_user() {
        Intent i = new Intent(context, UsersActivity.class);
        i.putExtra("task", "pick_user");
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data == null) {
                Toast.makeText(context, "Data response is null", Toast.LENGTH_SHORT).show();
                return;
            }
            if (data.hasExtra("user_id")) {
                receiver_id = data.getStringExtra("user_id");
                if (receiver_id != null) {
                    if (!receiver_id.isEmpty()) {
                        if ((loggedInUserModel.user_id + "").equals(receiver_id)) {
                            Toast.makeText(context, "You cannot send message to yourself.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onActivityResult: You cannot send message to yourself. ");
                            return;
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("sender_id", (loggedInUserModel.user_id + ""));
                        intent.putExtra("receiver_id", receiver_id+"");
                        context.startActivity(intent);
                    }
                }
            }
        }
    }

    private static final String TAG = "ChatsActivity";
    String receiver_id = "";


    private void logout_user() {
        try {
            databaseRepository.logout_user();
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            finish();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to logout because " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}