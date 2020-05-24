package com.shanmingc.yi.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.shanmingc.yi.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFragment extends Fragment {
    private ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.record_fragment, null);

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
            map.put("record","胜负");
            map.put("round","步数");
            map.put("time","对战日期");
            list.add(map);
        }

        return list;
    }

}
