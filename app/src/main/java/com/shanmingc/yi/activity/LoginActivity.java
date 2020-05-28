package com.shanmingc.yi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.shanmingc.yi.R;
import com.shanmingc.yi.model.UserMessage;
import com.shanmingc.yi.network.RequestProxy;
import com.shanmingc.yi.view.ProgressDialog;
import okhttp3.*;


import java.util.Map;

import static com.shanmingc.yi.activity.RegisterActivity.HOST;
import static com.shanmingc.yi.activity.RoomActivity.IS_LOGIN;
import static com.shanmingc.yi.activity.RoomActivity.USER_PREFERENCE;

public class LoginActivity extends AppCompatActivity {

    private String username;
    private String password;

    private AlertDialog mDialog;

    private static final String TAG = "LoginActivity";

    public static final String USER_ID = "user_id";

    public static final String USER_NAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEdit = findViewById(R.id.username);
        final EditText passwordEdit = findViewById(R.id.password);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.progress_dialog, null, false);

        mDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        mDialog.setCanceledOnTouchOutside(false);

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

        TextView registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        TextView forgotPasswordButton = findViewById(R.id.forgetfound);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this,ForgetActivity.class));
            }
        });

        CardView loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击登录后，显示加载动画
                mDialog.show();

                LoginTask loginTask = new LoginTask();
                loginTask.execute();
            }
        });
    }

    class LoginTask extends AsyncTask<Void, Void, UserMessage> {
        @Override
        protected UserMessage doInBackground(Void... voids) {
            //构建Post请求体
            FormBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .build();
            //网络请求
            Request request = new Request.Builder().url(HOST + "/api/user/login").
                    post(formBody).build();

            Map<String, Object> user = RequestProxy.waitForResponse(request);

            UserMessage message = new UserMessage(
                    (String) user.get("username"),
                    (String) user.get("message"),
                    ((Double) user.get("uid")).longValue());

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return message;
        }

        @Override
        protected void onPostExecute(UserMessage message) {
            super.onPostExecute(message);
            mDialog.cancel();
            if(message.getUsername().length() > 0)
                onSuccess(message);
            else onFailed(message);
        }
    }

    private void onFailed(UserMessage msg) {
        String message = msg.getMessage();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message)
                .setNeutralButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        Log.d(TAG, "login failed: " + message);
    }

    private void onSuccess(UserMessage msg) {
        String message = msg.getMessage();
        Log.d(TAG, "login success: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE);
        preferences.edit()
                .putBoolean(IS_LOGIN, true)
                .putLong(USER_ID, msg.getUid())
                .putString(USER_NAME, msg.getUsername())
                .apply();
        startActivity(new Intent(this, GameMenuActivity.class));
        finish();
    }
}
