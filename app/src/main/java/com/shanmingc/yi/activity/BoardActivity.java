package com.shanmingc.yi.activity;

import android.content.*;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.shanmingc.yi.R;
import com.shanmingc.yi.network.RequestProxy;
import com.shanmingc.yi.view.BoardView;
import okhttp3.FormBody;
import okhttp3.Request;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.shanmingc.yi.activity.LoginActivity.USER_ID;
import static com.shanmingc.yi.activity.LoginActivity.USER_NAME;
import static com.shanmingc.yi.activity.RegisterActivity.HOST;
import static com.shanmingc.yi.activity.RoomActivity.*;
import static com.shanmingc.yi.view.BoardView.*;

public class BoardActivity extends AppCompatActivity {

    private SharedPreferences roomPreference;
    private SharedPreferences userPreference;

    private ExecutorService exec;

    public static final String CHESS_BROADCAST = "com.shanmingc.yi.chessbroadcast";
    public static final String PLAYER_BROADCAST = "com.shanmingc.yi.playerbroadcast";
    public static final String FINISH_BROADCAST = "com.shanmingc.yi.finishbroadcast";
    public static final String UI_BROADCAST = "com.shanmingc.yi.uibroadcast";
    public static final String GAME_PREFERENCE = "game_preference";

    private static final String TAG = "BoardActivity";

    //private long opponentId;
    //private String opponentName;

    private String room_id;

    private TextView selfText;
    private TextView opponentText;
    private ImageView selfChess;
    private ImageView opponentChess;
    private TextView opponentReady;
    private Button selfReady;
    private BoardView mBoardView;

    private SharedPreferences gamePreference;

    private BroadcastReceiver mBoardReceiver;

    private boolean mYourTurn;
    private boolean mFinish = false;
    private boolean ready = false;
    private boolean owner;
    private boolean mGameStart = false;
    private boolean mBlack;
    private boolean threadEnd = false;


    private int currStep = 0;

    enum Player {
        NONE, MATCHED, READY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_actvity);

        selfReady = findViewById(R.id.self_ready);
        selfReady.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ready = !ready;
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        requestReady(ready);
                    }
                });
                if(ready)
                    selfReady.setText(R.string.cancel_ready);
                else selfReady.setText(R.string.ready);
            }
        });

        init();

        //exec.execute(new ChessTask());

    }

    class BoardReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PLAYER_BROADCAST)) {
                Player state = (Player) intent.getSerializableExtra("state");
                String name = intent.getStringExtra("username");
                switch (state) {
                    case NONE:
                        opponentText.setText("");
                        opponentReady.setText(R.string.not_ready);
                        break;
                    case READY:
                        opponentReady.setText(R.string.ready);
                        if (ready) {
                            mGameStart = true;
                            //requestGameStart();
                            //gameStart();
                            exec.execute(new ChessTask());
                        }
                        break;
                    case MATCHED:
                        opponentText.setText(name);
                        opponentReady.setText(R.string.not_ready);
                        break;
                }
            } else if(action.equals(CHESS_BROADCAST)) {
                if(!mGameStart)
                    return;
                int x = intent.getIntExtra(CHESS_X, -1);
                int y = intent.getIntExtra(CHESS_Y, -1);
                int step = intent.getIntExtra(STEP, 0);
                boolean local = intent.getBooleanExtra("local", false);
                currStep = step;
                if(local) {
                    if(mYourTurn) {
                        mYourTurn = false;
                        requestChessOn(x, y, step);
                        //mBoardView.chess(x, y);
                    }
                }
                else {
                    if(!mYourTurn) {
                        mYourTurn = true;
                        mBoardView.chess(x, y);
                    }
                }
                Log.d(TAG, x + " " + y + " " + step);
            } else if(action.equals(FINISH_BROADCAST)) {
                boolean blackWin = intent.getBooleanExtra("blackWin", false);
                new AlertDialog
                        .Builder(BoardActivity.this)
                        .setMessage((blackWin)? R.string.black_win : R.string.white_win)
                        .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestGameQuit();
                                requestQuitRoom();
                                BoardActivity.this.finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                mGameStart = false;
                //exec.execute(new PlayerTask());
            } else if(action.equals(UI_BROADCAST)) {
                if(mGameStart)
                    gameStart();
            }
        }
    }

    class PlayerTask implements Runnable {

        @Override
        public void run() {

            Log.d(TAG, "enter player task");

            while(!mGameStart) {

                if(threadEnd)
                    break;

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String room_id = roomPreference.getString(ROOM_ID, "");

                Map<String, Object> room = requestRoomInfo(room_id);

                if(room == null) {
                    BoardActivity.this.finish();
                }

                long id = userPreference.getLong(USER_ID, 0);

                Log.d(TAG, "owner_id : " + room.get("room_owner"));

                long owner_id = ((Double) room.get("room_owner")).longValue();
                long player_id = ((Double) room.get("player")).longValue();

                owner = (owner_id == id);

                boolean owner_ready = (Boolean) room.get("ownerReady");
                boolean player_ready = (Boolean) room.get("playerReady");

                if (owner) {
                    Map<String, Object> player = requestPlayerInfo(player_id);
                    if(player == null) {
                        Log.d(TAG, "request player " + player_id + " failed");
                        continue;
                    }
                    String player_name = (String) player.get("username");
                    if (player_id == 0) {
                        sendBroadcast(createBroadcast(Player.NONE, ""));
                    }
                    else if (player_ready)
                        sendBroadcast(createBroadcast(Player.READY, player_name));
                    else sendBroadcast(createBroadcast(Player.MATCHED, player_name));
                } else {
                    Map<String, Object> owner = requestPlayerInfo(owner_id);
                    if(owner == null) {
                        Log.d(TAG, "request owner " +  owner_id + " failed");
                        continue;
                    }
                    String owner_name = (String) owner.get("username");
                    if (owner_ready)
                        sendBroadcast(createBroadcast(Player.READY, owner_name));
                    else sendBroadcast(createBroadcast(Player.MATCHED, owner_name));
                }
            }
        }

        private Intent createBroadcast(Player state, String username) {
            Intent intent = new Intent(PLAYER_BROADCAST);
            intent.putExtra("state", state);
            intent.putExtra("username", username);
            return intent;
        }
    }

    class ChessTask implements Runnable {
        @Override
        public void run() {
            requestGameStart();

            mYourTurn = mBlack;

            sendBroadcast(new Intent(UI_BROADCAST));

            while(true) {
                //退出activity时的结束条件
                if(threadEnd)
                    break;
                if(mYourTurn) {
                    while(mYourTurn) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    int step = 0;
                    Map<String, Object> game;
                    do {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        game = requestGameInfo(room_id);
                        logResponse(TAG, game, "game step");
                        if(game.get("step_count") == null)
                            continue;
                        boolean blackWin = (Boolean) game.get("blackWin");
                        boolean finish = (Boolean) game.get("finish");
                        //结束条件
                        if(finish) {
                            Intent intent = new Intent(FINISH_BROADCAST);
                            intent.putExtra("blackWin", blackWin);
                            sendBroadcast(intent);
                            return;
                        }
                        step = ((Double) game.get("step_count")).intValue();
                    } while(step != currStep+1);
                    int x = ((Double) game.get("lastX")).intValue();
                    int y = ((Double) game.get("lastY")).intValue();
                    sendBroadcast(createBroadcast(x, y, step, false));
                }
            }

        }

        private Intent createBroadcast(int x, int y, int step, boolean local) {
            Intent intent = new Intent(CHESS_BROADCAST);
            intent.putExtra(CHESS_X, x);
            intent.putExtra(CHESS_Y, y);
            intent.putExtra(STEP, step);
            intent.putExtra("local", local);
            return intent;
        }
    }

    public void init() {
        exec = Executors.newCachedThreadPool();

        userPreference = getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE);
        roomPreference = getSharedPreferences(ROOM_PREFERENCE, MODE_PRIVATE);
        gamePreference = getSharedPreferences(GAME_PREFERENCE, MODE_PRIVATE);

        selfText = findViewById(R.id.self_name);
        selfChess = findViewById(R.id.self_chess);
        selfText.setText(userPreference.getString(USER_NAME, "null"));
        opponentText = findViewById(R.id.opponent_name);
        opponentChess = findViewById(R.id.opponent_chess);
        opponentReady = findViewById(R.id.opponent_ready);
        mBoardView = findViewById(R.id.board);
        //mBoardView.paintBoard();

        room_id = roomPreference.getString(ROOM_ID, "");



        //broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAYER_BROADCAST);
        filter.addAction(CHESS_BROADCAST);
        filter.addAction(FINISH_BROADCAST);
        filter.addAction(UI_BROADCAST);
        mBoardReceiver = new BoardReceiver();
        registerReceiver(mBoardReceiver, filter);

        Log.d(TAG, "start player task");
        exec.execute(new PlayerTask());
    }

    private Map<String, Object> requestRoomInfo(String room_id) {

        Map<String, Object> room;

        int times = 3;

        do {
            Request request = new Request.Builder()
                    .url(HOST + "/api/room/info?room_id=" + room_id)
                    .get()
                    .build();

            room = RequestProxy.waitForResponse(request);
            logResponse(TAG, room, "room info");
            times--;
        } while (room == null && times > 0);

        return room;
    }

    private Map<String, Object> requestPlayerInfo(long id) {
        if(id == 0)
            return null;
        Map<String, Object> user;
        do {
            Request request = new Request.Builder()
                    .url(HOST + "/api/user/" + id)
                    .get()
                    .build();

            user = RequestProxy.waitForResponse(request);
            logResponse(TAG, user, "user info");
        } while(user == null);

        return user;
    }

    private void requestReady(boolean ready) {

        Map<String, Object> response;
        FormBody form = new FormBody.Builder()
                .add("room_id", room_id)
                .add("uid", Long.toString(userPreference.getLong(USER_ID, 0)))
                .add("isReady", Boolean.toString(ready))
                .build();

        Request request = new Request.Builder()
                .url(HOST + "/api/room/ready")
                .post(form)
                .build();
        do {
            response = RequestProxy.waitForResponse(request);
            logResponse(TAG, response, "request ready");
        } while(response == null);
    }

    private Map<String, Object> requestGameInfo(String game_id) {
        //获取对局信息

        int times = 3;

        Map<String, Object> game;

        Request request = new Request.Builder()
                .url(HOST + "/api/game/info?game_id="+game_id)
                .get()
                .build();
        do {
            game = RequestProxy.waitForResponse(request);
            logResponse(TAG, game, "game info");
            times--;
        } while(game == null && times > 0);

        return game;
    }

    private void requestGameStart() {
        Map<String, Object> game;

        FormBody form = new FormBody.Builder()
                .add("game_id", room_id)
                .build();

        Request request = new Request.Builder()
                .url(HOST + "/api/game/start")
                .post(form)
                .build();
        do {
            game = RequestProxy.waitForResponse(request);

            logResponse(TAG, game, "game start");
        } while (game == null);

        long black_id = ((Double) game.get("black_id")).longValue();
        long id = userPreference.getLong(USER_ID, 0);
        mBlack = (black_id == id);

        gamePreference.edit().putBoolean("isBlack", mBlack).apply();

        Log.d("chess", "mblack:" + mBlack + " black_id: " + black_id + " id: " + id);
    }

    private void requestGameQuit() {
        FormBody form  = new FormBody.Builder()
                .add("game_id", room_id)
                .add("id", Long.toString(userPreference.getLong(USER_ID, 0)))
                .build();

        Request request = new Request.Builder()
                .url(HOST + "/api/game/quit")
                .post(form)
                .build();

        RequestProxy.waitForResponse(request);
    }

    private void requestChessOn(int x, int y, int step) {

        Map<String, Object> game;
        do {

            FormBody form = new FormBody.Builder()
                    .add("game_id", roomPreference.getString(ROOM_ID, ""))
                    .add("x", Integer.toString(x))
                    .add("y", Integer.toString(y))
                    .add("step", Integer.toString(step))
                    .build();

            Request request = new Request.Builder()
                    .url(HOST + "/api/game/step")
                    .post(form)
                    .build();

            game = RequestProxy.waitForResponse(request);
            logResponse(TAG, game, "chess on");
        } while(game == null);
    }

    private void gameStart() {
        opponentReady.setVisibility(INVISIBLE);
        selfReady.setVisibility(INVISIBLE);
        String text = "游戏开始，你执";


        if(mBlack) {
            text += "黑棋";
            opponentChess.setImageResource(R.drawable.white_shadow);
            selfChess.setImageResource(R.drawable.black_shadow);
        }
        else {
            text += "白棋";
            opponentChess.setImageResource(R.drawable.black_shadow);
            selfChess.setImageResource(R.drawable.white_shadow);
        }
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void requestQuitRoom() {
        FormBody form = new FormBody.Builder()
                .add("room_id", room_id)
                .add("id", Long.toString(roomPreference.getLong(USER_ID, 0)))
                .build();

        Request request = new Request.Builder()
                .url(HOST + "/api/room/quit")
                .post(form)
                .build();

        RequestProxy.waitForResponse(request);
    }

    public static void logResponse(String tag, Map<String, Object> response, String msg) {
        if(response == null) {
            Log.d(tag, msg + ": null 获取失败");
            return;
        }
        Log.d(TAG, msg + ":\n");
        for(Map.Entry<String, Object> entry : response.entrySet()) {
            Log.d(tag, entry.getKey() + " : " +entry.getValue());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadEnd = true;
        exec.shutdown();
        unregisterReceiver(mBoardReceiver);
    }
}
