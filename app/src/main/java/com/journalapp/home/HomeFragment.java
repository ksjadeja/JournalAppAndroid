package com.journalapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.journalapp.R;
import com.journalapp.utils.MyPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment implements  TabLayout.OnTabSelectedListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Button button;
    MyPagerAdapter myPagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout= root.findViewById(R.id.timelineTabs);

        viewPager = root.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Journal Entries"));
        tabLayout.addTab(tabLayout.newTab().setText("Account Entries"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(this);
         myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(myPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));




        return root;
//        View root = inflater.inflate(R.layout.fragment_home, container, true);
//
//        ViewPager pager = root.findViewById(R.id.viewPager);
//        tabAdapter = new TabAdapter(getChildFragmentManager());
//        pager.setAdapter(tabAdapter);
//
//        timelineTabs = root.findViewById(R.id.timelineTabs);
//        timelineTabs.setupWithViewPager(pager,true);
//        return null;
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