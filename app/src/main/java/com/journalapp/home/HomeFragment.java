package com.journalapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.journalapp.AccountEntryEditActivity;
import com.journalapp.EntriesEditPad;
import com.journalapp.ExpenseEntryNewActivity;
import com.journalapp.R;
import com.journalapp.utils.MyPagerAdapter;

public class HomeFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    private FloatingActionButton add_fab, layoutAccEntryFab, layoutEntryFab, layoutExpEntryFab;
    private Boolean fabExpanded = false;

    private static String TAG = "DEB    :";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setRetainInstance(true);
        tabLayout = root.findViewById(R.id.timelineTabs);

        viewPager = root.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED).setIcon(R.drawable.ic_entries_white).setText("Journal"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_account_entries_white).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED).setText("Accounts"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_expense_entries_white).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED).setText("Expenses"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        add_fab = root.findViewById(R.id.add_fab);
        layoutEntryFab = root.findViewById(R.id.entries_fab);
        layoutAccEntryFab = root.findViewById(R.id.acc_entry_fab);
        layoutExpEntryFab = root.findViewById(R.id.exp_entry_fab);
        add_fab.setOnClickListener(v -> {
            if (fabExpanded) {
                closeSubMenusFab();
            } else {
                openSubMenusFab();
            }
        });

        layoutEntryFab.setOnClickListener(v -> {
            Intent newEntryIntent = new Intent(getContext(), EntriesEditPad.class);
            startActivity(newEntryIntent);
            closeSubMenusFab();

        });

        layoutAccEntryFab.setOnClickListener(v -> {
            Intent accountEntryIntent = new Intent(getContext(), AccountEntryEditActivity.class);
            startActivity(accountEntryIntent);
            closeSubMenusFab();
        });

        layoutExpEntryFab.setOnClickListener(v -> {
            Intent expenseEntryIntent = new Intent(getContext(), ExpenseEntryNewActivity.class);
            startActivity(expenseEntryIntent);
            closeSubMenusFab();
        });

        tabLayout.setOnTabSelectedListener(this);
        myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        closeSubMenusFab();
        Log.w(TAG, "fragment onCreateView() ");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Timeline");
        Toast.makeText(getActivity(), "On  View Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        Toast.makeText(getActivity(), "Tab selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    //closes FAB submenus
    private void closeSubMenusFab() {
        layoutEntryFab.setVisibility(View.INVISIBLE);
        layoutAccEntryFab.setVisibility(View.INVISIBLE);
        layoutExpEntryFab.setVisibility(View.INVISIBLE);
        add_fab.setImageResource(R.drawable.ic_plus_btn);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        layoutEntryFab.setVisibility(View.VISIBLE);
        layoutAccEntryFab.setVisibility(View.VISIBLE);
        layoutExpEntryFab.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        add_fab.setImageResource(R.drawable.ic_close_btn);
        fabExpanded = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Timeline");
        Log.w(TAG, "fragment onStart() ");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle("Timeline");
        }
    }

}
