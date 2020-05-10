package com.shanmingc.yi.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.shanmingc.yi.R;

import static android.view.MotionEvent.*;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {

    private static int GRID_W_SIZE=14;
    private static int GRID_H_SIZE=20;
    private static int startW=40,startH=40;
    private float titleW;
    private float titleH;
    private float radius;
    private int screenW,screenH;
    private Canvas canvas;
    private SurfaceHolder sfh;

    private int[][] board;
    private int[][] chessSeq = new int[GRID_H_SIZE*GRID_W_SIZE+1][2];
    private int curChess = 0;

    private Context context;

    private float prePaintedX = -1;
    private float getPrePaintedY = -1;

    private Bitmap black, white;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        black = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.black_shadow), 150 ,150, false);
        white = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.white_shadow), 150 ,150, false);
        sfh=this.getHolder();
        sfh.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW=this.getWidth();
        screenH=this.getHeight();
        titleW=(screenW-2*startW)/GRID_W_SIZE;
        titleH=(screenH-2*startH)/GRID_H_SIZE;
        radius = Math.min(titleH, titleW)/2-5;
        board = new int[GRID_W_SIZE+1][GRID_H_SIZE+1];
        paintBoard();
    }

    public void paintBoard() {
        canvas=sfh.lockCanvas();
        canvas.drawColor(Color.WHITE);
        Paint paint=new Paint();
        canvas.drawColor(ContextCompat.getColor(context, R.color.board_yellow));
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        float startX=0,startY=0;
        for (int i=0;i<=GRID_W_SIZE;i++){
            startX=startW+i*titleW;
            startY=startH;
            canvas.drawLine(startX,startY,startX,screenH-startH,paint);
        }
        for (int j=0;j<=GRID_H_SIZE;j++){
            startX=startW;
            startY=startH+j*titleH;
            canvas.drawLine(startX,startY,screenW-startW-5,startY,paint);
        }
        paint.setStrokeWidth(radius);
        for(int i = 1; i <= curChess; i++) {
            if(i%2 == 1)
                canvas.drawBitmap(black, chessSeq[i][0] * titleW, chessSeq[i][1] * titleH , paint);
            else canvas.drawBitmap(white,  chessSeq[i][0] * titleW, chessSeq[i][1] * titleH , paint);
        }
        if (canvas!=null) {
            sfh.unlockCanvasAndPost(canvas);
        }
    }

    public int[] processPos(float x, float y) {
        x -= startW;
        y-= startH;
        int blockNumX = (int) (x/titleW);
        if(x - blockNumX*titleW >= titleW/2)
            blockNumX++;
        int blockNumY = (int) (y/titleH);
        if(y - blockNumY*titleH >= titleH/2)
            blockNumY++;
        return new int[] { blockNumX, blockNumY };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == ACTION_UP) {
            int[] pos = processPos(event.getX(), event.getY());
            if(board[pos[0]][pos[1]] != 0)
                return false;

            chessSeq[++curChess][0] = pos[0];
            chessSeq[curChess][1] = pos[1];
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

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
}
