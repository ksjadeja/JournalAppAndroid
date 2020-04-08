package com.journalapp.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.journalapp.home.AccEntriesTab;

public class TabAdapter extends FragmentPagerAdapter {

    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
//                EntriesTab entriesTab = new EntriesTab();
//                return entriesTab;
            case 1:
                AccEntriesTab accEntriesTab = new AccEntriesTab();
                return accEntriesTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Journal Entries";
            case 1:
                return "Account Entries";
            default:
                return null;
        }
    }


}
