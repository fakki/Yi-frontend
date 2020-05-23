package com.shanmingc.yi.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.shanmingc.yi.R;
import com.shanmingc.yi.network.RequestProxy;
import okhttp3.FormBody;
import okhttp3.Request;

import java.util.Map;

import static com.shanmingc.yi.activity.BoardActivity.logResponse;
import static com.shanmingc.yi.activity.RegisterActivity.HOST;
import static com.shanmingc.yi.activity.RoomActivity.ROOM_ID;

public class TestActivity extends AppCompatActivity {

    private static final String TAG  = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Map<String, Object> game;
        do {

            FormBody form = new FormBody.Builder()
                    .add("game_id", "1d2f8158-c477-4fcb-82da-fd0ffa38fa6e")
                    .add("x", Integer.toString(2))
                    .add("y", Integer.toString(2))
                    .add("step", Integer.toString(1))
                    .build();

            Request request = new Request.Builder()
                    .url(HOST + "/api/game/step")
                    .post(form)
                    .build();

            game = RequestProxy.waitForResponse(request);
            logResponse(TAG, game, "chess on");
        } while(game == null);
    }
}
