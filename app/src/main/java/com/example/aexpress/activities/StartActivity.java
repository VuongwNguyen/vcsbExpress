package com.example.aexpress.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.aexpress.R;
import com.example.aexpress.db.DatabaseRepository;
import com.example.aexpress.model.LoggedInUserModel;


public class StartActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "whatsapp_clone_db";
    public static final String LOGGED_IN_USER_TABLE = "LOGGED_IN_USER_TABLE";
    public static final String USERS_TABLE = "USERS_TABLE";
    public static final String MESSAGES_TABLE = "MESSAGES_TABLE";

    public static final String BASE_URL = "https://vcsbexpress.id.vn/whatsapp/";
    DatabaseRepository databaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        databaseRepository = new DatabaseRepository(this);

        check_login();

    }

    private void check_login() {
        databaseRepository.get_logged_in_user().observe(this, new Observer<LoggedInUserModel>() {
            @Override
            public void onChanged(LoggedInUserModel loggedInUserModel) {
                if (loggedInUserModel == null) {
                    Toast.makeText(StartActivity.this, "Not Logged in", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(StartActivity.this, RegisterActivity.class);
                    StartActivity.this.startActivity(i);
                    finish();
                } else {
                    databaseRepository.login_user(loggedInUserModel);
                    Intent i = new Intent(StartActivity.this, ChatsActivity.class);
                    StartActivity.this.startActivity(i);
                    finish();
                }
            }
        });
    }
}