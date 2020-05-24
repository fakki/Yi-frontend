package com.shanmingc.yi.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shanmingc.yi.R;

import java.util.List;
import java.util.Map;

public class RecordFragment_ListViewAdapter extends BaseAdapter {
    private List<Map<String,Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public RecordFragment_ListViewAdapter(Context context,List<Map<String,Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class Contrllist{
        public TextView TVrecord,TVtime,TVround;
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
        Contrllist contrl = null;
        if(convertView == null){
            contrl = new Contrllist();
            convertView = layoutInflater.inflate(R.layout.recordlistview,null);
            contrl.TVrecord = convertView.findViewById(R.id.TVrecord);
            contrl.TVround = convertView.findViewById(R.id.TVround);
            contrl.TVtime = convertView.findViewById(R.id.TVtime);
            convertView.setTag(contrl);
        }
        else contrl =(Contrllist) convertView.getTag();

        contrl.TVrecord.setText((String)data.get(position).get("record"));
        contrl.TVround.setText((String)data.get(position).get("round"));
        contrl.TVtime.setText((String)data.get(position).get("time"));
        return convertView;
    }
}
