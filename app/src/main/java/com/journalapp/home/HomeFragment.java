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
    private Boolean fabExpanded = false;
    private LinearLayout layoutAccEntryFab, layoutEntryFab;
    private FloatingActionButton add_fab;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout= root.findViewById(R.id.timelineTabs);
        add_fab = root.findViewById(R.id.add_fab);
        layoutEntryFab = root.findViewById(R.id.layoutEntry);
        layoutAccEntryFab = root.findViewById(R.id.layoutAccEntry);
        viewPager = root.findViewById(R.id.viewPager);

        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fabExpanded){
                    closeSubMenusFab();
                }else {
                    openSubMenusFab();
                }
            }
        });

        layoutEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You've tapped new Entry",Toast.LENGTH_SHORT).show();
                //TODO
            }
        });

        layoutAccEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You've tapped new Account Entry",Toast.LENGTH_SHORT).show();
                //TODO
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText("Journal Entries"));
        tabLayout.addTab(tabLayout.newTab().setText("Account Entries"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(this);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(myPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));




        closeSubMenusFab();
        return root;
    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutEntryFab.setVisibility(View.INVISIBLE);
        layoutAccEntryFab.setVisibility(View.INVISIBLE);
        add_fab.setImageResource(R.drawable.ic_plus_btn);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutEntryFab.setVisibility(View.VISIBLE);
        layoutAccEntryFab.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        add_fab.setImageResource(R.drawable.ic_close_btn);
        fabExpanded = true;
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