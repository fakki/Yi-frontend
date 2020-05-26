package com.shanmingc.yi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.shanmingc.yi.R;
import com.shanmingc.yi.activity.LoginActivity;
import com.shanmingc.yi.widget.ItemGroup;

import static com.shanmingc.yi.activity.RoomActivity.IS_LOGIN;
import static com.shanmingc.yi.activity.RoomActivity.USER_PREFERENCE;

public class PersonalFragment extends Fragment {

    private ItemGroup quit,mine,setting;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.personal_fragment,null);
        quit = v.findViewById(R.id.quit);
        mine = v.findViewById(R.id.mine);
        setting = v.findViewById(R.id.setting);
        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userPreference = getActivity().getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
                userPreference.edit().putBoolean(IS_LOGIN, false).apply();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

}
