package com.shanmingc.yi.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shanmingc.yi.R;

import java.util.List;
import java.util.Map;

import static com.shanmingc.yi.activity.LoginActivity.USER_ID;
import static com.shanmingc.yi.activity.RoomActivity.USER_PREFERENCE;

public class RecordFragment_ListViewAdapter extends BaseAdapter {
    private List<Map<String,Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public RecordFragment_ListViewAdapter(Context context,List<Map<String,Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return data.size();
    }
    @Override
    public Object getItem(int position){
        return data.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.recordlistview,null);
        }

        SharedPreferences userPreference = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        long id = userPreference.getLong(USER_ID, 0);

        Map<String, Object> m = data.get(position);
        long black_id = ((Double) m.get("black_id")).longValue();
        int step_count = ((Double) m.get("step_count")).intValue();
        String start_time = (String) (m.get("start_time"));
        boolean blackWin = (step_count % 2 == 1);


        TextView record = convertView.findViewById(R.id.TVrecord);
        Log.d("record", "blackWin: " + blackWin + " id == black_id: " + (id == black_id));
        if(blackWin == (id == black_id)) {
            record.setText("胜利");
            record.setTextColor(Color.parseColor("#00FC3F"));
        }
        else {
            record.setText("失败");
            record.setTextColor(Color.parseColor("#b22222"));
        }

        TextView time = convertView.findViewById(R.id.time);
        time.setText(start_time);

        TextView round = convertView.findViewById(R.id.TVround);
        round.setText("总步数：" + step_count);
        return convertView;
    }
}
