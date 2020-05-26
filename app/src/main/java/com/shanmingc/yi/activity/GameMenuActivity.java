package com.shanmingc.yi.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TableLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.shanmingc.yi.Fragment.Gamemenu_FragmentPagerAdapter;
import com.shanmingc.yi.R;

import static com.shanmingc.yi.activity.RoomActivity.IS_LOGIN;
import static com.shanmingc.yi.activity.RoomActivity.USER_PREFERENCE;


public class GameMenuActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewpager;
    private Gamemenu_FragmentPagerAdapter menuFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamemenu);
        initView();
    }

    private void initView(){

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

        viewpager = findViewById(R.id.vp);
        tabLayout = findViewById(R.id.tabLayout);
        menuFragmentPagerAdapter = new Gamemenu_FragmentPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(menuFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewpager);


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences userPreference = getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        boolean isLogin = userPreference.getBoolean(IS_LOGIN, false);
        if(!isLogin) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
