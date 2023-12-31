package com.example.aexpress.activities;


import static com.example.aexpress.activities.StartActivity.BASE_URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

;

import com.example.aexpress.R;
import com.example.aexpress.adapters.AdapterUsers;
import com.example.aexpress.db.DatabaseRepository;
import com.example.aexpress.model.UserModel;
import com.example.aexpress.web.WebInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersActivity extends AppCompatActivity {

    Context context;
    DatabaseRepository databaseRepository;
    private static final String TAG = "UsersActivity";
    String task = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        context = this;
        if (getIntent().hasExtra("task")) {
            task = getIntent().getStringExtra("task");
            if (task == null) {
                task = "";
            }
        }
        databaseRepository = new DatabaseRepository(context);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        get_users_from_server();
        get_data_from_local_db();
        //init_recycler_view();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void get_data_from_local_db() {
        databaseRepository.get_users().observe((LifecycleOwner) context, new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                if (userModels != null) {
                    users = userModels;
                }
                Toast.makeText(context, "Found " + userModels.size(), Toast.LENGTH_SHORT).show();
                init_recycler_view();
            }
        });
    }

    private RecyclerView recyclerView = null;
    private AdapterUsers mAdapter = null;
    Toolbar toolbar;
    ProgressBar progress_bar;
    TextView no_record_found;

    private void init_recycler_view() {
        if (recyclerView != null) {
            update_recycler_view();
            return;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setSubtitle(users.size() + "");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress_bar = findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = findViewById(R.id.no_record_found);
        no_record_found.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        if (users.size() < 1) {
            no_record_found.setVisibility(View.VISIBLE);
        }

        mAdapter = new AdapterUsers(this, users);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterUsers.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UserModel obj, int position) {
                if (task.equals("pick_user")) {
                    Intent intent = new Intent();
                    intent.putExtra("user_id", obj.user_id+"");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return;
                }

                Log.d(TAG, "YOU CLICKED ON ==> " + obj.name);

               /* Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user_id", obj.user_id);
                context.startActivity(intent);
                return;*/
            }
        });

        progress_bar.setVisibility(View.GONE);


    }

    private void update_recycler_view() {

        if (users.size() < 1) {
            no_record_found.setVisibility(View.VISIBLE);
        }

        Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    List<UserModel> users = new ArrayList<>();


    private void get_users_from_server() {

        //step 1
        Retrofit ret = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Step 2
        WebInterface webInterface = ret.create(WebInterface.class);

        //Step 3
        Call<List<UserModel>> my_call = webInterface.get_users();

        my_call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {

                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: SUCCESS BUT not success " + response.errorBody());
                    return;
                }

                if (response.body().isEmpty()) {
                    Log.d(TAG, "onResponse: SUCCESS BUT Empty");
                    return;
                }

                try {
                    Log.d(TAG, "onResponse: SUCCESS FOUND === > " + response.body().size());
                    databaseRepository.save_users(response.body());
                    Log.d(TAG, "onResponse: SUCCESS ");
                } catch (Exception e) {
                    Log.d(TAG, "onResponse: FAiLED to save .... " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                Log.d(TAG, "onResponse: FAILED BECAUSE  " + t.getMessage());

                Toast.makeText(context, "Failed to get online users because " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}