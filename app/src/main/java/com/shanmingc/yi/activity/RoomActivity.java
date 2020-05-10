package com.shanmingc.yi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.shanmingc.yi.R;

public class RoomActivity extends AppCompatActivity {

    public static final String USER_PREFERENCES = "Yi-user";
    public static final String IS_LOGIN = "isLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        final Button startButton = findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
                preferences.edit().putBoolean(IS_LOGIN, false).apply();
                boolean isLogin = preferences.getBoolean(IS_LOGIN, false);
                if(isLogin)
                    startActivity(new Intent(RoomActivity.this, BoardActivity.class));
                else startActivity(new Intent(RoomActivity.this, LoginActivity.class));
            }
        });
    }
}
