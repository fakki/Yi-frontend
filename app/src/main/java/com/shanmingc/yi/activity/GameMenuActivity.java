package com.shanmingc.yi.activity;


import android.os.Bundle;
import android.widget.TableLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.shanmingc.yi.Fragment.Gamemenu_FragmentPagerAdapter;
import com.shanmingc.yi.R;


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
        viewpager = findViewById(R.id.vp);
        tabLayout = findViewById(R.id.tabLayout);
        menuFragmentPagerAdapter = new Gamemenu_FragmentPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(menuFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewpager);


    }


}
