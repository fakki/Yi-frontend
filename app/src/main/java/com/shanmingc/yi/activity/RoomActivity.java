package com.shanmingc.yi.activity;

import android.content.*;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.jyn.vcview.VerificationCodeView;
import com.shanmingc.yi.R;
import com.shanmingc.yi.network.RequestProxy;
import com.shanmingc.yi.view.ProgressDialog;
import okhttp3.FormBody;
import okhttp3.Request;

import java.util.Map;
import java.util.concurrent.*;

import static com.shanmingc.yi.activity.LoginActivity.USER_ID;
import static com.shanmingc.yi.activity.RegisterActivity.HOST;

public class RoomActivity extends AppCompatActivity {

    public static final String USER_PREFERENCE = "Yi-user";
    public static final String IS_LOGIN = "isLogin";

    private SharedPreferences userPreference;

    private static final String TAG = "RoomActivity";

    private ExecutorService exec = Executors.newCachedThreadPool();

    public static final String ROOM_PREFERENCE = "room_preference";
    public static final String ROOM_CODE = "room_code";
    public static final String ROOM_ID = "room_id";
    public static final String ROOM_PLAYER = "room_player";
    public static final String ROOM_OWNER = "room_owner";
    private static final String CODE = "com.shanmingc.yi.roomactivity.code";


    private AlertDialog mDialog;

    private RoomReceiver mRoomReceiver = new RoomReceiver();

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        userPreference = getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE);

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
                SharedPreferences preferences = getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE);
                preferences.edit().putBoolean(IS_LOGIN, true).apply();
                boolean isLogin = preferences.getBoolean(IS_LOGIN, false);
                if(isLogin)
                    startActivity(new Intent(RoomActivity.this, BoardActivity.class));
                else startActivity(new Intent(RoomActivity.this, LoginActivity.class));
            }
        });

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.progress_dialog, null, false);

        mDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        mDialog.setCanceledOnTouchOutside(false);

        registerReceiver(mRoomReceiver, new IntentFilter(CODE));
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

                SharedPreferences userPreference = getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE);
                long id = userPreference.getLong(USER_ID, 0);

                FormBody form = new FormBody.Builder()
                        .add("code", content)
                        .add("player", Long.toString(id))
                        .build();

                final Request request = new Request.Builder()
                        .url(HOST + "/api/room/join")
                        .post(form)
                        .build();

                mDialog.show();

                Map<String, Object> room = null;

                try {

                    room = exec.submit(new Callable<Map<String, Object>>() {
                        @Override
                        public Map<String, Object> call() throws Exception {
                            return RequestProxy.waitForResponse(request);
                        }
                    }).get(4, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mDialog.cancel();


                if(room == null || room.size() == 0 || room.containsKey("error")) {
                    Toast.makeText(RoomActivity.this, "邀请码错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences roomPreference = getSharedPreferences(ROOM_PREFERENCE, MODE_PRIVATE);
                roomPreference
                        .edit()
                        .putString(ROOM_CODE, (String) room.get("code"))
                        .putString(ROOM_ID, (String) room.get("room_id"))
                        .putLong(ROOM_OWNER, ((Double) room.get("room_owner")).longValue())
                        .putLong(ROOM_PLAYER, ((Double) room.get("player")).longValue()).apply();

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

        mDialog.show();

        SharedPreferences userPreference = this.getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE);
        long uid = userPreference.getLong(USER_ID, 0);
        FormBody form = new FormBody.Builder()
                .add("owner_id", Long.toString(uid))
                .build();
        final Request request = new Request.Builder().url(HOST + "/api/room/battle/friend").post(form).build();

        Map<String, Object> room = null;

        try {
            room = exec.submit(new Callable<Map<String, Object>>() {
                @Override
                public Map<String, Object> call() throws Exception {
                    return RequestProxy.waitForResponse(request);
                }
            }).get(4, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDialog.cancel();

        SharedPreferences roomPreference = getSharedPreferences(ROOM_PREFERENCE, MODE_PRIVATE);
        code = (String) room.get("code");

        Log.d(TAG, "" + room.get("room_owner"));

        if(code != null) {
            roomPreference
                    .edit()
                    .putString(ROOM_CODE, (String) room.get("code"))
                    .putString(ROOM_ID, (String) room.get("room_id"))
                    .putLong(ROOM_OWNER, ((Double) room.get("room_owner")).longValue())
                    .putLong(ROOM_PLAYER, ((Double) room.get("player")).longValue()).apply();

            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.code_tint) + code)
                    .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(RoomActivity.this, BoardActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else
            Toast.makeText(RoomActivity.this, "获取失败，请重试", Toast.LENGTH_SHORT).show();
    }

    class RoomReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isLogin = userPreference.getBoolean(IS_LOGIN, false);
        if(!isLogin)
            startActivity(new Intent(RoomActivity.this, LoginActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRoomReceiver);
    }
}
