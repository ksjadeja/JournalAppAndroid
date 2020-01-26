package com.journalapp.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.journalapp.home.AccEntriesTab;
import com.journalapp.home.EntriesTab;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;
    public MyPagerAdapter(FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.tabCount=tabCount;
    }

    @Override

    public Fragment getItem(int position) {
        switch (position){

            case 0:
                EntriesTab entriesTab= new EntriesTab();
                return entriesTab;
            case 1:
                AccEntriesTab statusFragment = new AccEntriesTab();
                return statusFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
