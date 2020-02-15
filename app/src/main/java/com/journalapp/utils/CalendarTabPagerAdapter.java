package com.journalapp.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.journalapp.DrawerLayoutActivity2;
import com.journalapp.calendar.DatewiseAccEntries;
import com.journalapp.calendar.DatewiseEntries;
import com.journalapp.calendar.DatewiseExpEntries;

public class CalendarTabPagerAdapter extends FragmentStatePagerAdapter {    int tabCount;
    public CalendarTabPagerAdapter(FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.tabCount=tabCount;
    }

    @Override

    public Fragment getItem(int position) {
        switch (position){

            case 0:
                DatewiseEntries entriesTab= new DatewiseEntries();
                DrawerLayoutActivity2.datewiseEntries = entriesTab;
                return entriesTab;
            case 1:
                DatewiseAccEntries accEntriesTab = new DatewiseAccEntries();
                return accEntriesTab;
            case 2:
                DatewiseExpEntries expEntriesTab = new DatewiseExpEntries();
                return expEntriesTab;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
