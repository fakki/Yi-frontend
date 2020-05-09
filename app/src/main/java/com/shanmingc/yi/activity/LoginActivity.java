package com.shanmingc.yi.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.gson.Gson;
import com.shanmingc.yi.R;
import com.shanmingc.yi.model.UserMessage;
import okhttp3.*;

import java.util.concurrent.*;

import static com.shanmingc.yi.activity.RegisterActivity.HOST;
import static com.shanmingc.yi.activity.RoomActivity.IS_LOGIN;
import static com.shanmingc.yi.activity.RoomActivity.USER_PREFERENCES;

public class LoginActivity extends AppCompatActivity {

    private String username;
    private String password;

    private ProgressBar loading;

    private ExecutorService exec = Executors.newCachedThreadPool();

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEdit = findViewById(R.id.username);
        final EditText passwordEdit = findViewById(R.id.password);

        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                username = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loading = findViewById(R.id.loading);

        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        Button  forgotPasswordButton = findViewById(R.id.forgotPassword);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this,ForgetActivity.class));
            }
        });

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                FormBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder().url(HOST + "/api/user/login").
                        post(formBody).build();
                Future<String> response = exec.submit(new com.shanmingc.yi.network.Request(request));
                while (!response.isDone()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.toString());
                    }
                }
                Gson gson = new Gson();
                UserMessage message;
                try {
                    message = gson.fromJson(response.get(), UserMessage.class);
                } catch (Exception e) {
                    Log.d(TAG, "json parse error: " + e);
                    finish();
                    return;
                }
                loading.setVisibility(View.GONE);
                if(message.getUsername().length() > 0)
                    onSuccess(message.getMessage());
                else onFailed(message.getMessage());
            }
        });
    }

    private void onFailed(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message)
                .setNeutralButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        Log.d(TAG, "login failed: " + message);
    }

    private void onSuccess(String message) {
        Log.d(TAG, "login success: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putBoolean(IS_LOGIN, true).apply();
        finish();
    }
}
