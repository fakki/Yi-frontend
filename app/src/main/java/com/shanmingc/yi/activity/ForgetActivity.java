package com.shanmingc.yi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import okhttp3.FormBody;
import okhttp3.Request;
import org.w3c.dom.Text;

import java.util.HashMap;
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

public class ForgetActivity extends AppCompatActivity{

    private String email;

    private ProgressBar loading;

    private ExecutorService exec = Executors.newCachedThreadPool();

    private static final String TAG = "ForgetActivity";
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_forget);

        EditText emailEdit = findViewById(R.id.email);

        Button forgetButton = findViewById(R.id.forgotPassword);

        emailEdit.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s,int start, int count,int after){

            }
            @Override
            public void onTextChanged(CharSequence s,int start, int before, int count){
                email = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s){

            }
        });

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                FormBody formBody = new FormBody.Builder()
                        .add("email", email)
                        .build();
                Request request = new Request.Builder().url(HOST + "/api/user/login/forgetpassword")
                        .post(formBody).build();
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
                    message = gson.fromJson(response.get(),UserMessage.class);
                }   catch (Exception e){
                    Log.d(TAG,"json parse error:" + e);
                    finish();
                    return;
                }
                loading.setVisibility(View.GONE);
                if(message.getUsername().length() > 0)
                {
                    onSuccess(message.getMessage());
                    startActivity(new Intent(ForgetActivity.this,ResetPasswordActivity.class));
                }
                else {
                    onFailed(message.getMessage());
                    startActivity(new Intent(ForgetActivity.this,ForgetActivity.class));
                }
            }
        });
    }

    private void onFailed(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message)
                .setNeutralButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
        Log.d(TAG,"Not find ：" + message);
    }

    private void onSuccess(String message){
       Log.d(TAG,"found it:"+message);

       finish();
    }
}
