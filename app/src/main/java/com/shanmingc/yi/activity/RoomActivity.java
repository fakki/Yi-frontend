package com.shanmingc.yi.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.jyn.vcview.VerificationCodeView;
import com.shanmingc.yi.R;
import com.shanmingc.yi.network.RequestProxy;
import okhttp3.FormBody;
import okhttp3.Request;

import java.util.Map;

import static com.shanmingc.yi.activity.LoginActivity.USER_ID;
import static com.shanmingc.yi.activity.RegisterActivity.HOST;

public class RoomActivity extends AppCompatActivity {

    public static final String USER_PREFERENCES = "Yi-user";
    public static final String IS_LOGIN = "isLogin";

    private static final String TAG = "RoomActivity";

    public static final String ROOM_PREFERENCE = "room_preference";
    public static final String ROOM_CODE = "room_code";
    public static final String ROOM_ID = "room_id";
    public static final String ROOM_PLAYER = "room_player";
    public static final String ROOM_OWNER = "room_owner";

    private ProgressBar loading;

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        loading = findViewById(R.id.loading);

        Button rebutton =findViewById(R.id.re_button);
        rebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RoomActivity.this,GameMenuActivity.class));
            }
        });

        CardView friend_battle = findViewById(R.id.friend_battle);
        friend_battle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                battleWithFriend();
            }
        });

        CardView join_battle = findViewById(R.id.join_battle);
        join_battle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinBattle();
            }
        });

        CardView online_battle = findViewById(R.id.online_battle);
        online_battle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
                preferences.edit().putBoolean(IS_LOGIN, true).apply();
                boolean isLogin = preferences.getBoolean(IS_LOGIN, false);
                if(isLogin)
                    startActivity(new Intent(RoomActivity.this, BoardActivity.class));
                else startActivity(new Intent(RoomActivity.this, LoginActivity.class));
            }
        });
    }

    private void joinBattle() {
        View v = getLayoutInflater().inflate(R.layout.code_input, null);
        VerificationCodeView code = v.findViewById(R.id.code);
        code.setOnCodeFinishListener(new VerificationCodeView.OnCodeFinishListener() {
            @Override
            public void onTextChange(View view, String content) {

            }

            @Override
            public void onComplete(View view, String content) {
                RoomActivity.this.code = content;

                SharedPreferences userPreference = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
                long id = userPreference.getLong(USER_ID, 0);

                FormBody form = new FormBody.Builder()
                        .add("code", content)
                        .add("player", Long.toString(id))
                        .build();

                Request request = new Request.Builder()
                        .url(HOST + "/api/room/join")
                        .post(form)
                        .build();


                Map<String, Object> room = RequestProxy.waitForResponse(request);

                SharedPreferences roomPreference = getSharedPreferences(ROOM_PREFERENCE, MODE_PRIVATE);
                roomPreference.edit().putString(ROOM_CODE, (String) room.get("code")).apply();
                roomPreference.edit().putString(ROOM_ID, (String) room.get("room_id")).apply();
                roomPreference.edit().putLong(ROOM_OWNER, (Long) room.get("room_owner")).apply();
                roomPreference.edit().putLong(ROOM_PLAYER, (Long) room.get("player")).apply();

                startActivity(new Intent(RoomActivity.this, BoardActivity.class));
            }
        });
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.black)
                .setTitle(R.string.invite_code_tint)
                .setView(v)
                .show();

    }

    private void battleWithFriend() {
        loading.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loading.setElevation(100);
        }

        SharedPreferences userPreference = this.getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        long uid = userPreference.getLong(USER_ID, 0);
        FormBody form = new FormBody.Builder()
                .add("owner_id", Long.toString(uid))
                .build();
        Request request = new Request.Builder().url(HOST + "/api/room/battle/friend").post(form).build();

        Map<String, Object> room = RequestProxy.waitForResponse(request);

        SharedPreferences roomPreference = getSharedPreferences(ROOM_PREFERENCE, MODE_PRIVATE);
        code = (String) room.get("code");
        roomPreference.edit()
                .putString(ROOM_CODE, code)
                .putString(ROOM_ID, (String) room.get("room_id"))
                .putLong(ROOM_OWNER, (Long) room.get("room_owner"))
                .apply();


        loading.setVisibility(View.GONE);

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.code_tint) + code)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RoomActivity.this, BoardActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        boolean isLogin = preferences.getBoolean(IS_LOGIN, false);
        if(!isLogin)
            startActivity(new Intent(RoomActivity.this, LoginActivity.class));
    }
}
