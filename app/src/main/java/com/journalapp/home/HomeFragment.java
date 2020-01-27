package com.journalapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journalapp.R;
import com.journalapp.utils.MyPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment implements  TabLayout.OnTabSelectedListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout= root.findViewById(R.id.timelineTabs);

        viewPager = root.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Journal Entries"));
        tabLayout.addTab(tabLayout.newTab().setText("Account Entries"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(this);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(myPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));




        return root;
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