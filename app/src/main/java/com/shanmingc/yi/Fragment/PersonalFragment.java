package com.shanmingc.yi.Fragment;

import android.content.Intent;
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

public class PersonalFragment extends Fragment {

    private ItemGroup quit,mine,setting;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.personal_fragment,null);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        quit = getActivity().findViewById(R.id.quit);
        mine = getActivity().findViewById(R.id.mine);
        setting = getActivity().findViewById(R.id.setting);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),null);
                startActivity(null);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),null);
                startActivity(null);
            }
        });
    }

}
