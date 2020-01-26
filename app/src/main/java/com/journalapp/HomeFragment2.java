package com.journalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.journalapp.utils.MyPagerAdapter;
import com.journalapp.utils.TabAdapter;

public class HomeFragment2 extends Fragment implements TabLayout.OnTabSelectedListener{

    private TabAdapter tabAdapter;
    private TabLayout timelineTabs;
//    private Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_home,container,false);

//        ViewPager pager = rootView.findViewById(R.id.viewPager);
//        tabAdapter = new TabAdapter(getChildFragmentManager());
//        pager.setAdapter(tabAdapter);
//
//        timelineTabs = rootView.findViewById(R.id.timelineTabs);
//        timelineTabs.setupWithViewPager(pager,true);
//        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        tabLayout = rootView.findViewById(R.id.timelineTabs);
        viewPager = rootView.findViewById(R.id.viewPager);

//        setSupportActionBar(toolbar);

        tabLayout.addTab(tabLayout.newTab().setText("Journal Entries"));
        tabLayout.addTab(tabLayout.newTab().setText("Account Entries"));
//        tabLayout.addTab(tabLayout.newTab().setText("Calls"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(this);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(myPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Home");
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
