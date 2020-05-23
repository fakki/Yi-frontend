package com.shanmingc.yi.Fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Gamemenu_FragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList = new ArrayList<>();
    private String[] Titles = new String[]{"Tab1","Tab2","Tab3"};

    public Gamemenu_FragmentPagerAdapter(FragmentManager fm){
        super(fm);
        fragmentList.add(new BattleListFragment());
        fragmentList.add(new RecordFragment());
        fragmentList.add(new PersonalFragment());
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1)
            return new RecordFragment();
        else if (position == 2)
            return new PersonalFragment();

        return new BattleListFragment();
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