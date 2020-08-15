package com.myjournal.journalapp.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.myjournal.journalapp.home.AccEntriesTab;
import com.myjournal.journalapp.home.EntriesTab;
import com.myjournal.journalapp.home.ExpEntriesTab;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;
    EntriesTab entriesTab;
    AccEntriesTab accEntriesTab;
    ExpEntriesTab expEntriesTab;
    public MyPagerAdapter(FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.tabCount=tabCount;
    }
    @Override

    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(entriesTab==null) {
                    entriesTab = new EntriesTab();
                }
                return entriesTab;
            case 1:
                if(accEntriesTab==null) {
                    accEntriesTab = new AccEntriesTab();
                }
                return accEntriesTab;
            case 2:
                if(expEntriesTab==null) {
                    expEntriesTab = new ExpEntriesTab();
                }
                return expEntriesTab;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
