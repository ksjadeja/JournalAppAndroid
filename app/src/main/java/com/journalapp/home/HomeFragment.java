package com.journalapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.journalapp.R;
import com.journalapp.utils.TabAdapter;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    private TabAdapter tabAdapter;
    private TabLayout timelineTabs;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        View root = inflater.inflate(R.layout.fragment_home, container, true);
//
//        ViewPager pager = root.findViewById(R.id.viewPager);
//        tabAdapter = new TabAdapter(getChildFragmentManager());
//        pager.setAdapter(tabAdapter);
//
//        timelineTabs = root.findViewById(R.id.timelineTabs);
//        timelineTabs.setupWithViewPager(pager,true);
        return null;
    }
}