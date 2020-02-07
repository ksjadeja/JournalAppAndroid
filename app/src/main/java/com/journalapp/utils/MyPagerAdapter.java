package com.journalapp.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.journalapp.home.AccEntriesTab;
import com.journalapp.home.EntriesTab;
import com.journalapp.home.ExpenseEntriesTab;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;
    EntriesTab entriesTab;
    AccEntriesTab accEntriesTab;
    ExpenseEntriesTab expenseEntriesTab;
    public MyPagerAdapter(FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);
        this.tabCount=tabCount;
        entriesTab= new EntriesTab();
        expenseEntriesTab = new ExpenseEntriesTab();
        accEntriesTab= new AccEntriesTab();
    }

    @Override

    public Fragment getItem(int position) {
        switch (position){

            case 0:

                return entriesTab;
            case 1:

                return accEntriesTab;
            case 2:

                return expenseEntriesTab;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
