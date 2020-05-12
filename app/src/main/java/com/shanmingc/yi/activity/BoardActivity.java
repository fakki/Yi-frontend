package com.shanmingc.yi.activity;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.shanmingc.yi.R;

import static com.shanmingc.yi.activity.RoomActivity.ROOM_PREFERENCE;

public class BoardActivity extends AppCompatActivity {

    private SharedPreferences roomPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_actvity);
        roomPreference = getSharedPreferences(ROOM_PREFERENCE, MODE_PRIVATE);
    }
}
