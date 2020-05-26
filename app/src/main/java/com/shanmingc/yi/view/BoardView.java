package com.shanmingc.yi.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import com.shanmingc.yi.R;
import com.shanmingc.yi.network.RequestProxy;
import okhttp3.FormBody;
import okhttp3.Request;

import static android.view.MotionEvent.*;
import static com.shanmingc.yi.activity.BoardActivity.*;
import static com.shanmingc.yi.activity.LoginActivity.USER_ID;
import static com.shanmingc.yi.activity.RegisterActivity.HOST;
import static com.shanmingc.yi.activity.RoomActivity.*;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String CHESS_X = "chess_x";
    public static final String CHESS_Y = "chess_y";
    public static final String STEP = "step";

    private static int GRID_W_SIZE=15;
    private static int GRID_H_SIZE=15;
    private static int start=20;
    private float chessRadius, blockSize, edgeWidth;
    private int boardSize, screenW,screenH;
    private Canvas canvas;
    private SurfaceHolder sfh;

    private SharedPreferences gamePreference;
    private SharedPreferences roomPreference;
    private SharedPreferences userPreference;

    private static final String TAG = "BoardView";

    private int[][] board;
    private int[][] chessSeq = new int[GRID_H_SIZE*GRID_W_SIZE+1][2];
    private int curChess = 0;

    private boolean mBlack;

    private Context context;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        sfh=this.getHolder();
        sfh.addCallback(this);

        gamePreference = context.getSharedPreferences(GAME_PREFERENCE, MODE_PRIVATE);
        roomPreference = context.getSharedPreferences(ROOM_PREFERENCE, MODE_PRIVATE);
        userPreference = context.getSharedPreferences(USER_PREFERENCE, MODE_PRIVATE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW=this.getWidth();
        screenH=this.getHeight();
        int smaller = Math.min(screenH, screenW);
        while((smaller-start)%15 != 0) {
            start++;
        }
        start /= 2;
        boardSize = smaller-2*start;
        blockSize = boardSize/15;
        edgeWidth = blockSize/2;
        chessRadius = blockSize/2-7;
        sfh.setFixedSize(boardSize, boardSize);
        board = new int[GRID_W_SIZE+1][GRID_H_SIZE+1];
        paintBoard();
        paintBoard();
    }

    public void paintBoard() {
        canvas=sfh.lockCanvas();

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_board);
        drawable.setBounds(0, 0, boardSize, boardSize);
        drawable.draw(canvas);

        for(int i = 1; i <= curChess; i++) {
            if(i%2 == 1)
                drawChess(chessSeq[i][0],chessSeq[i][1], false, canvas);
            else drawChess(chessSeq[i][0],chessSeq[i][1], true, canvas);
        }

        Log.d(TAG, "painted board");

        if (canvas!=null) {
            sfh.unlockCanvasAndPost(canvas);
        }
    }

    private void drawChess(int x, int y, boolean white, Canvas canvas) {
        float cx = x*blockSize + blockSize/2;
        float cy = y*blockSize + blockSize/2;
        Paint paint = new Paint();
        if(white)
            paint.setColor(ContextCompat.getColor(context, R.color.white));
        else paint.setColor(ContextCompat.getColor(context, R.color.black));
        paint.setAntiAlias(true);
        //添加阴影
        paint.setShadowLayer(20f, 0, 0, R.color.colorPrimary);
        canvas.drawCircle(cx, cy, chessRadius, paint);
        paint.setStrokeWidth(4f);
        float bias, start, sweep;
        if(white) {
            paint.setColor(ContextCompat.getColor(context, R.color.white));
            bias = 2f;
            start = 20f;
            sweep = 70f;
        }
        else {
            paint.setColor(ContextCompat.getColor(context, R.color.shadow_grey));
            start = 20f;
            sweep = 70f;
            bias = 3f;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(cx-chessRadius, cy-chessRadius, cx+chessRadius-bias, cy+chessRadius-bias, start, sweep, false, paint);
        }
    }

    public int[] processPos(float x, float y) {
        x -= edgeWidth;
        y-= edgeWidth;
        int blockNumX = (int) (x/blockSize);
        if(x - blockNumX*blockSize >= blockSize/2)
            blockNumX++;
        int blockNumY = (int) (y/blockSize);
        if(y - blockNumY*blockSize >= blockSize/2)
            blockNumY++;
        return new int[] { blockNumX, blockNumY };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == ACTION_UP) {
            mBlack = gamePreference.getBoolean("isBlack", false);
            int[] pos = processPos(event.getX(), event.getY());
            if(board[pos[0]][pos[1]] != 0)
                return false;
            if(mBlack == ((curChess+1)%2 == 0)) {
                Toast.makeText(context, "不是你的回合", Toast.LENGTH_SHORT).show();
                return false;
            }
            boolean win = chess(pos[0], pos[1]);
            Intent intent = new Intent(CHESS_BROADCAST);
            intent.putExtra(CHESS_X, pos[0]);
            intent.putExtra(CHESS_Y, pos[1]);
            intent.putExtra(STEP, curChess);
            intent.putExtra("local", true);
            Log.d("chess", ": has win " + win);
            intent.putExtra("win", win);
            context.sendBroadcast(intent);
        }
        return true;
    }

    private void onSuccess() {
        /*if(curChess % 2 == 0)
            Toast.makeText(getContext(), R.string.white_win, Toast.LENGTH_SHORT).show();
        else Toast.makeText(getContext(), R.string.black_win, Toast.LENGTH_SHORT).show();
        curChess = 0;*/
        Intent intent = new Intent(FINISH_BROADCAST);
        intent.putExtra("blackWin", curChess % 2 != 0);
        context.sendBroadcast(intent);
        //requestWin();
        board = new int[GRID_W_SIZE+1][GRID_H_SIZE+1];
    }

    private void requestWin() {

        String room_id = roomPreference.getString(ROOM_ID, "");
        long id = userPreference.getLong(USER_ID, 0);

        FormBody form = new FormBody.Builder()
                .add("game_id", room_id)
                .add("winner", Long.toString(id))
                .build();

        Request request = new Request.Builder()
                .url(HOST+"/api/game/win")
                .post(form)
                .build();

        RequestProxy.waitForResponse(request);
    }

    private boolean isSuccess(int x, int y) {
        return isSuccess(x, y, 0, 1) || isSuccess(x, y, 1, 0)
                || isSuccess(x, y, 1, 1) || isSuccess(x, y, 1, -1);
    }

    private boolean isSuccess(int x, int y, int dirX, int dirY) {
        int num = 0, curX = x, curY = y, label = board[x][y];
        while (curX >= 0 && curX < GRID_W_SIZE && curY >= 0 && curY < GRID_H_SIZE && board[curX][curY] == label) {
            num++;
            curX += dirX;
            curY += dirY;
        }
        curX = x - dirX;
        curY = y - dirY;
        while (curX >= 0 && curX < GRID_W_SIZE && curY >= 0 && curY < GRID_H_SIZE && board[curX][curY] == label) {
            num++;
            curX -= dirX;
            curY -= dirY;
        }
        return num == 5;
    }

    public boolean chess(int x, int y) {
        boolean win = addStep(x, y);
        paintBoard();
        return win;
    }

    private boolean addStep(int x, int y) {
        chessSeq[++curChess][0] = x;
        chessSeq[curChess][1] = y;
        board[x][y] = (curChess % 2) + 1;
        if(isSuccess(x, y)) {
            onSuccess();
            return true;
        }
        return false;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
}
