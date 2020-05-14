package com.shanmingc.yi.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.shanmingc.yi.R;
import okhttp3.FormBody;
import okhttp3.Request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.shanmingc.yi.activity.RegisterActivity.HOST;
import static com.shanmingc.yi.activity.RoomActivity.IS_LOGIN;
import static com.shanmingc.yi.activity.RoomActivity.USER_PREFERENCES;
public class ResetPasswordActivity  extends AppCompatActivity {

    private String password;
    private ProgressBar loading;

    private ExecutorService exec = Executors.newCachedThreadPool();

    @Override
    protected   void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        final EditText  passwordEdit = findViewById(R.id.password);

        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loading = findViewById(R.id.loading);

        Button  resetButton = findViewById(R.id.resetpasswordbutton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                FormBody formBody = new FormBody.Builder()
                        .add("password", password)
                        .build();
                Request request = new Request.Builder().url(HOST + "/api/user/login/forgetpassword/resetpassword").
                        post(formBody).build();
                Future<String> response = exec.submit(new com.shanmingc.yi.network.Request(request));
                //while (!response.isDone())
            }
        });
    }
}
