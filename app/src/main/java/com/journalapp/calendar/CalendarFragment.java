package com.journalapp.calendar;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.journalapp.R;
import com.journalapp.utils.CalendarTabPagerAdapter;

import java.util.Calendar;

public class CalendarFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private DatePicker datePicker;
    private TabLayout calendarTabs;
    private ViewPager calendarViewPager;
    private CalendarTabPagerAdapter calendarPagerAdapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_calendar,container,false);

        calendarTabs = rootView.findViewById(R.id.calendarTabs);
        calendarViewPager = rootView.findViewById(R.id.calendarViewPager);

        calendarTabs.addTab(calendarTabs.newTab().setText("Journal Entries"));
        calendarTabs.addTab(calendarTabs.newTab().setText("Account Entries"));
        calendarTabs.addTab(calendarTabs.newTab().setText("Expense Entries"));
        calendarTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        calendarTabs.setOnTabSelectedListener(this);

        calendarPagerAdapter = new CalendarTabPagerAdapter(getActivity().getSupportFragmentManager(),calendarTabs.getTabCount());
        calendarViewPager.setAdapter(calendarPagerAdapter);

        calendarViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(calendarTabs));


        datePicker = rootView.findViewById(R.id.datePicker);
        Calendar c = Calendar.getInstance();
        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener(){
                @Override
                public void onDateChanged(DatePicker view, int newYear, int newMonth, int newDay) {
                String date= (newDay<10?"0"+newDay:newDay) + "-" + (newMonth<9?"0"+(newMonth+1):(newMonth+1)) + "-" + newYear;
                Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
                }
        });


        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Gallery");
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        calendarViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
