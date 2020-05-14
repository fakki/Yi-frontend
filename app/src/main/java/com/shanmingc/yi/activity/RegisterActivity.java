package com.shanmingc.yi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.gson.Gson;
import com.shanmingc.yi.R;
import com.shanmingc.yi.model.UserMessage;
import com.shanmingc.yi.network.RequestProxy;
import okhttp3.FormBody;
import okhttp3.Request;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";


    public static final String HOST = "http://192.168.1.5:8081";


    private String username;
    private String password;
    private String email;

    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loading = findViewById(R.id.loading);
        initEditText();

        initRegisterButton();
    }

    private void initRegisterButton() {
        Button button = findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                if (!isValidEmail(email) || !isValidUsername(username) || !isValidPassword(password)) {
                    showSingleNeutralAlertDialog(RegisterActivity.this,
                            getString(R.string.error_register_message));
                }
                FormBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .add("email", email)
                        .build();
                Request request = new Request.Builder().url(HOST + "/api/user/register").post(formBody).build();

                Map<String, Object> user = RequestProxy.waitForResponse(request);

                UserMessage message = new UserMessage(
                        (String) user.get("username"),
                        (String) user.get("message"),
                        (long) user.get("uid"));

                loading.setVisibility(View.GONE);
                if (message.getUsername().length() > 0)
                    onSuccess(message.getMessage());
                else onFailed(message.getMessage());

            }
        });
    }

    private void initEditText() {
        final TextView usernameValid = findViewById(R.id.valid_username);
        final TextView passwordValid = findViewById(R.id.valid_password);
        final TextView emailInvalid = findViewById(R.id.invalid_email);
        EditText usernameEdit = findViewById(R.id.username);
        EditText passwordEdit = findViewById(R.id.password);
        EditText emailEdit = findViewById(R.id.email);
        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                username = s.toString();
                if(isValidUsername(username))
                    usernameValid.setVisibility(View.INVISIBLE);
                else usernameValid.setVisibility(View.VISIBLE);
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
                if(isValidPassword(password))
                    passwordValid.setVisibility(View.INVISIBLE);
                else passwordValid.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();
                if(isValidEmail(email))
                    emailInvalid.setVisibility(View.INVISIBLE);
                else emailInvalid.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.d(TAG, "register success: " + message);
        finish();
    }

    private void onFailed(String message) {
        showSingleNeutralAlertDialog(this, message);
        Log.d(TAG, "register failed: " + message);
    }

    public static void showSingleNeutralAlertDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setNeutralButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public static boolean isValidUsername(String username) {
        return username.length() <= 7;
    }
    public static boolean isValidPassword(String password) {
        if(password.length() > 20 || password.length() < 8)
            return false;
        return password.matches("[0-9a-zA-Z_]*?");
    }
    public static boolean isValidEmail(String email) {
        return email.matches(".+?@.+?[.].+?");
    }
}
