package com.journalapp.home;

import android.content.Intent;
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
import com.google.android.material.tabs.TabLayout;
import com.journalapp.AccountEntryActivity;
import com.journalapp.DrawerLayoutActivity2;
import com.journalapp.EntriesEditPad;
import com.journalapp.ExpenseEntryActivity;
import com.journalapp.R;
import com.journalapp.utils.MyPagerAdapter;

public class HomeFragment extends Fragment implements  TabLayout.OnTabSelectedListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    private LinearLayout layoutAccEntryFab, layoutEntryFab , layoutExpEntryFab;
    private FloatingActionButton add_fab;
    private Boolean fabExpanded = false;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout= root.findViewById(R.id.timelineTabs);

        viewPager = root.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Journal Entries"));
        tabLayout.addTab(tabLayout.newTab().setText("Account Entries"));
        tabLayout.addTab(tabLayout.newTab().setText("Expense Entries"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        add_fab = root.findViewById(R.id.add_fab);
        layoutEntryFab = root.findViewById(R.id.layoutEntry);
        layoutAccEntryFab =root.findViewById(R.id.layoutAccEntry);
        layoutExpEntryFab = root.findViewById(R.id.layoutExpEntry);
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
                Intent newEntryIntent = new Intent(getContext(), EntriesEditPad.class);
                startActivity(newEntryIntent);
                closeSubMenusFab();

            }
        });

        layoutAccEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You've tapped new Account Entry",Toast.LENGTH_SHORT).show();
                Intent accountEntryIntent = new Intent(getContext(), AccountEntryActivity.class);
                startActivity(accountEntryIntent);
                closeSubMenusFab();
            }
        });

        layoutExpEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You've tapped new Expense Entry",Toast.LENGTH_SHORT).show();
                Intent expenseEntryIntent = new Intent(getContext(), ExpenseEntryActivity.class);
                startActivity(expenseEntryIntent);
                closeSubMenusFab();
            }
        });

        tabLayout.setOnTabSelectedListener(this);
         myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        closeSubMenusFab();

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

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutEntryFab.setVisibility(View.INVISIBLE);
        layoutAccEntryFab.setVisibility(View.INVISIBLE);
        layoutExpEntryFab.setVisibility(View.INVISIBLE);
        add_fab.setImageResource(R.drawable.ic_plus_btn);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutEntryFab.setVisibility(View.VISIBLE);
        layoutAccEntryFab.setVisibility(View.VISIBLE);
        layoutExpEntryFab.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        add_fab.setImageResource(R.drawable.ic_close_btn);
        fabExpanded = true;
    }


}
