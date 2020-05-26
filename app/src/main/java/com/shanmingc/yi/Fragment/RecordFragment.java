package com.shanmingc.yi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.shanmingc.yi.R;
import com.shanmingc.yi.activity.WatchActivity;
import com.shanmingc.yi.network.RequestProxy;
import com.shanmingc.yi.view.ProgressDialog;
import okhttp3.FormBody;
import okhttp3.Request;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shanmingc.yi.activity.BoardActivity.GAME_PREFERENCE;
import static com.shanmingc.yi.activity.LoginActivity.USER_ID;
import static com.shanmingc.yi.activity.RegisterActivity.HOST;
import static com.shanmingc.yi.activity.RoomActivity.USER_PREFERENCE;

public class RecordFragment extends Fragment {
    private ListView listView;

    private ProgressDialog dialog;

    List<Map<String, Object>> games;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.record_fragment, null);

        dialog = new ProgressDialog.Builder(getActivity()).build();
        dialog.setCancelable(false);

        listView = view.findViewById(R.id.Recordlist);

        getData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences gamePreferences =
                        RecordFragment.this.getActivity().getSharedPreferences(GAME_PREFERENCE, Context.MODE_PRIVATE);
                gamePreferences
                        .edit()
                        .putString("steps", (String) games.get(position).get("process"))
                        .apply();
                Context context = RecordFragment.this.getActivity();
                context.startActivity(new Intent(context, WatchActivity.class));
            }
        });

        return  view;
    }

    public void getData(){

        dialog.show();

        RecordTask task = new RecordTask();
        task.execute();
    }

    private class RecordTask extends AsyncTask<Void, Void, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(Void... voids) {

            SharedPreferences userPreference = getActivity().getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);

            Request request = new Request.Builder()
                    .url(HOST + "/api/game/records?id=" + userPreference.getLong(USER_ID, 0))
                    .get()
                    .build();

            return RequestProxy.waitForResponseList(request);
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            super.onPostExecute(maps);
            games = maps;
            listView.setAdapter(new RecordFragment_ListViewAdapter(getActivity(), maps));
            dialog.cancel();
        }
    }

}
