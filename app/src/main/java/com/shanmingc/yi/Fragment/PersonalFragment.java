package com.shanmingc.yi.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.shanmingc.yi.R;
import com.shanmingc.yi.activity.LoginActivity;
import com.shanmingc.yi.widget.ItemGroup;

public class PersonalFragment extends Fragment {

    private ItemGroup mine,setting;
    private CardView quit;
    private View view;
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_fragment,null);
        quit = view.findViewById(R.id.quit);
        mine = view.findViewById(R.id.mine);
        setting = view.findViewById(R.id.setting);
        return  view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


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
                Intent intent2 = new Intent(getActivity(),null);
                startActivity(null);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(getActivity(),null);
                startActivity(null);
            }
        });
    }

}
