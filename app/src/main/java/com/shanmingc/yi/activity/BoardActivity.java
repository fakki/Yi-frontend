package com.shanmingc.yi.activity;

import android.content.*;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.shanmingc.yi.R;
import com.shanmingc.yi.network.RequestProxy;
import com.shanmingc.yi.view.BoardView;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;

import static com.shanmingc.yi.activity.LoginActivity.USER_ID;
import static com.shanmingc.yi.activity.LoginActivity.USER_NAME;
import static com.shanmingc.yi.activity.RegisterActivity.HOST;
import static com.shanmingc.yi.activity.RoomActivity.*;
import static com.shanmingc.yi.view.BoardView.CHESS_X;
import static com.shanmingc.yi.view.BoardView.CHESS_Y;

public class BoardActivity extends AppCompatActivity {

    private SharedPreferences roomPreference;
    private SharedPreferences userPreference;

    private BoardView boardView;

    public static final String CHESS_BROADCAST = "com.shanmingc.yi.chess_broadcast";

    public static final String TAG = "BoardActivity";

    private TextView selfName;
    private TextView opponentName;

    private boolean ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_actvity);
        roomPreference = getSharedPreferences(ROOM_PREFERENCE, MODE_PRIVATE);

        boardView = findViewById(R.id.board);
        selfName = findViewById(R.id.self_name);
        selfName.setText(userPreference.getString(USER_NAME, "null"));
        opponentName = findViewById(R.id.opponent_name);

        IntentFilter filter = new IntentFilter();
        filter.addAction(CHESS_BROADCAST);
        registerReceiver(new ChessReceiver(), filter);

        //获取对手信息
        requestInfo();

        ready();
    }

    private void requestInfo() {
        long playerId = roomPreference.getLong(ROOM_PLAYER, 0);
        if(playerId == 0)
            return;
        Request request = new Request.Builder()
                .url(HOST + "/api/user/" + playerId)
                .get()
                .build();

        Map<String, Object> user = RequestProxy.waitForResponse(request);

        if(user == null || user.size() == 0)
            return;

        opponentName.setText((String) user.get("username"));
    }

    private void ready() {
        boolean owner_ready = false, player_ready = false;

        while(!owner_ready || !player_ready) {
            FormBody form = new FormBody.Builder()
                    .add("room_id", roomPreference.getString(ROOM_ID, ""))
                    .add("uid", Long.toString(userPreference.getLong(USER_ID, 0)))
                    .add("isReady", Boolean.toString(ready))
                    .build();

            Request request = new Request.Builder()
                    .url(HOST + "/api/room/ready")
                    .post(form)
                    .build();

            Map<String, Object> room = RequestProxy.waitForResponse(request);

            owner_ready = (Boolean) room.get("ownerReady");
            player_ready = (Boolean) room.get("playerReady");
        }
    }

    private void setReady(boolean ready) {
        this.ready = ready;
    }

    class ChessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int x = intent.getIntExtra(CHESS_X, -1);
            int y = intent.getIntExtra(CHESS_Y, -1);
            Log.d(TAG, x + " " + y);
        }
    }
}
