package com.shanmingc.yi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.shanmingc.yi.R;
import okhttp3.FormBody;
import okhttp3.Request;

import java.util.concurrent.Future;

import static com.shanmingc.yi.activity.RegisterActivity.HOST;

public class ForgetActivity extends AppCompatActivity {

    private String email;
    private String newpassword;

    private ProgressBar loading;

    private ExecutorService exec = Executors.newCachedThreadPool();

    private static final String TAG = "ForgetActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_forget);
        final TextView newpasswordValid = findViewById(R.id.valid_newpassword);
        EditText emailEdit = findViewById(R.id.email);
        final EditText newpasswordEdit = findViewById(R.id.newpassword);

        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newpasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newpassword = charSequence.toString();
                if(isValidPassword(newpassword))
                    newpasswordValid.setVisibility(View.INVISIBLE);
                else newpasswordValid.setVisibility(View.VISIBLE);
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        CardView forgetfind = findViewById(R.id.forgetfound);
        forgetfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSuccess(new UserMessage("default", "success"));
            }
        });
    }


    private void forgetfinding() {
        loading.setVisibility(View.VISIBLE);
        FormBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", newpassword)
                .build();
        Request request = new Request.Builder().url(HOST + "/api/user/login/forgetpassword")
                .post(formBody).build();

        Map<String, Object> user = RequestProxy.waitForResponse(request);

        loading.setVisibility(View.GONE);

        UserMessage message = new UserMessage(
                (String) user.get("username"),
                (String) user.get("message"));
        if (message.getUsername().length() > 0)
            onSuccess(message);
        else onFailed(message);
    }

    private void onFailed(UserMessage message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage((CharSequence) message)
                .setNeutralButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
        Log.d(TAG, "Not find your account：" + message);
    }

    private void onSuccess(UserMessage message) {
        Log.d(TAG, "found it and completed:" + message);
        startActivity(new Intent(ForgetActivity.this, LoginActivity.class));
        finish();
    }
    public static boolean isValidPassword(String password) {
        if(password.length() > 20 || password.length() < 8)
            return false;
        return password.matches("[0-9a-zA-Z_]*?");
    }
}


