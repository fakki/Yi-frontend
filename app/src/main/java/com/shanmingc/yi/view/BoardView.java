package com.shanmingc.yi.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import com.shanmingc.yi.R;

import static android.view.MotionEvent.*;
import static com.shanmingc.yi.activity.BoardActivity.CHESS_BROADCAST;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String CHESS_X = "chess_x";
    public static final String CHESS_Y = "chess_y";

    private static int GRID_W_SIZE=15;
    private static int GRID_H_SIZE=15;
    private static int start=20;
    private float chessRadius, blockSize, edgeWidth;
    private int boardSize, screenW,screenH;
    private Canvas canvas;
    private SurfaceHolder sfh;

    private int[][] board;
    private int[][] chessSeq = new int[GRID_H_SIZE*GRID_W_SIZE+1][2];
    private int curChess = 0;

    private Context context;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        sfh=this.getHolder();
        sfh.addCallback(this);
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
    }

    public void paintBoard() {
        canvas=sfh.lockCanvas();
        Paint paint=new Paint();

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_board);
        drawable.setBounds(0, 0, boardSize, boardSize);
        drawable.draw(canvas);

        for(int i = 1; i <= curChess; i++) {
            if(i%2 == 1)
                drawChess(chessSeq[i][0],chessSeq[i][1], false, canvas);
            else drawChess(chessSeq[i][0],chessSeq[i][1], true, canvas);
        }

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
            int[] pos = processPos(event.getX(), event.getY());
            if(board[pos[0]][pos[1]] != 0)
                return false;
            Intent intent = new Intent(CHESS_BROADCAST);
            intent.putExtra(CHESS_X, pos[0]);
            intent.putExtra(CHESS_Y, pos[1]);
            context.sendBroadcast(intent);
            addStep(pos[0], pos[1]);
            board[pos[0]][pos[1]] = (curChess % 2) + 1;
            if(isSuccess(pos[0], pos[1]))
                onSuccess();
            paintBoard();
        }
        return true;
    }

    private void onSuccess() {
        if(curChess % 2 == 0)
            Toast.makeText(getContext(), R.string.white_win, Toast.LENGTH_SHORT).show();
        else Toast.makeText(getContext(), R.string.black_win, Toast.LENGTH_SHORT).show();
        curChess = 0;
        board = new int[GRID_W_SIZE+1][GRID_H_SIZE+1];
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

    public void chess(int x, int y) {
        addStep(x, y);
    }

    private void addStep(int x, int y) {
        chessSeq[++curChess][0] = x;
        chessSeq[curChess][1] = y;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
}
