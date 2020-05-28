package com.shanmingc.yi.Fragment;

import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Gamemenu_FragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList = new ArrayList<>();
    private String[] Titles = new String[]{"对战模式","个人战绩","我的"};

    public Gamemenu_FragmentPagerAdapter(FragmentManager fm){
        super(fm);
        fragmentList.add(new BattleListFragment());
        fragmentList.add(new RecordFragment());
        fragmentList.add(new PersonalFragment());
    }

    /*public void refreshRecord() {
        RecordFragment recordFragment = (RecordFragment) fragmentList.get(1);
        recordFragment.getData();
    }*/

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return Titles.length;
    }
    @Override
    public CharSequence getPageTitle(int position){
        return Titles[position];
    }
}
