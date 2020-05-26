package com.shanmingc.yi.Fragment;

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

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentList.get(position);
        if(position == 1)
            ((RecordFragment) fragment).getData();
        return fragment;
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
