package com.shanmingc.yi.activity;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.shanmingc.yi.R;
import com.shanmingc.yi.view.BoardView;

import java.util.ArrayList;
import java.util.List;

import static com.shanmingc.yi.activity.BoardActivity.GAME_PREFERENCE;

public class WatchActivity extends AppCompatActivity {

    private List<int[]> steps = new ArrayList<>();

    private BoardView board;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        SharedPreferences gamePreference = getSharedPreferences(GAME_PREFERENCE, MODE_PRIVATE);

        parseProcess(gamePreference.getString("steps", ""));

        board = findViewById(R.id.board);

        CardView button = findViewById(R.id.next_step);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index >= steps.size()) {
                    Toast.makeText(WatchActivity.this, "游戏结束", Toast.LENGTH_SHORT).show();
                    return;
                }
                int[] step = steps.get(index);
                index++;
                board.chess(step[0], step[1]);
            }
        });
    }

    private void parseProcess(String process) {
        String[] ss = process.split("\n");
        for(String s : ss) {
            String[] a = s.split(" ");
            steps.add(new int[] { Integer.valueOf(a[1]),Integer.valueOf(a[2]) });
        }
    }
}
