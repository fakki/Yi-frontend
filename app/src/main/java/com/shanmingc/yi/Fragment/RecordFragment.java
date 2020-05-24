package com.shanmingc.yi.Fragment;

import android.graphics.Color;
import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.shanmingc.yi.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFragment extends Fragment {
    private ListView listView;
    private TextView TVrecord;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.record_fragment, null);
        TVrecord = view.findViewById(R.id.TVrecord);
        listView = view.findViewById(R.id.Recordlist);
        List<Map<String,Object>> list = getData();
        listView.setAdapter(new RecordFragment_ListViewAdapter(getActivity(),list));

        return  view;
    }

    public List<Map<String,Object>> getData(){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

        for (int i = 0;i < 10 ;i++)
        {
            Map<String ,Object> map = new HashMap<String,Object>();
            String strrecord = "";
            String strround = "";
            String strtime = "";
            map.put("record","胜负"/* 直接放strrecord */);
          /*  if (strrecord == "负") TVrecord.setTextColor(Color.parseColor("FF0000"));
                else  TVrecord.setTextColor(Color.parseColor("7cfc00")); */
            map.put("round","步数:"+strround);
            map.put("time","对战日期:"+strtime);
            list.add(map);
        }

        return list;
    }

}
