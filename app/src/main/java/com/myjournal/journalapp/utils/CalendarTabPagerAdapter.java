package com.myjournal.journalapp.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.myjournal.journalapp.calendar.DatewiseAccEntries;
import com.myjournal.journalapp.calendar.DatewiseEntries;
import com.myjournal.journalapp.calendar.DatewiseExpEntries;

public class CalendarTabPagerAdapter extends FragmentStatePagerAdapter {
    int tabCount;
    DatewiseEntries datewiseEntries;
    DatewiseAccEntries datewiseAccEntries;
    DatewiseExpEntries datewiseExpEntries;

    public CalendarTabPagerAdapter(FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.tabCount=tabCount;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(datewiseEntries!=null) {
                    return datewiseEntries;
                }else {
                    datewiseEntries = new DatewiseEntries();
                    return datewiseEntries;
                }
            case 1:
                if(datewiseAccEntries!=null) {
                    return datewiseAccEntries;
                }else {
                    datewiseAccEntries= new DatewiseAccEntries();
                    return datewiseAccEntries;
                }
            case 2:
                if(datewiseExpEntries!=null) {
                    return datewiseExpEntries;
                }else {
                    datewiseExpEntries= new DatewiseExpEntries();
                    return datewiseExpEntries;
                }
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
