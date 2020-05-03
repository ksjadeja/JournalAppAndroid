package com.journalapp.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.journalapp.AccountEntryEditActivity;
import com.journalapp.EntriesEditPad;
import com.journalapp.ExpenseEntryNewActivity;
import com.journalapp.R;
import com.journalapp.utils.CalendarTabPagerAdapter;

import java.util.Calendar;

public class CalendarFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private DatePicker datePicker;
    private TabLayout calendarTabs;
    private ViewPager calendarViewPager;
    private CalendarTabPagerAdapter calendarPagerAdapter;
    private String date;
    private LinearLayout layoutAccEntryFab, layoutEntryFab, layoutExpEntryFab;
    private FloatingActionButton add_fab;
    private Boolean fabExpanded = false;

    public interface JDatePickerSelectionListener {
        void onDatePickerSelection(String date);
    }

    public interface ADatePickerSelectionListener {
        void onDatePickerSelection(String date);
    }

    public interface EDatePickerSelectionListener {
        void onDatePickerSelection(String date);
    }

    JDatePickerSelectionListener jdatePickerSelectionListener;
    ADatePickerSelectionListener adatePickerSelectionListener;
    EDatePickerSelectionListener edatePickerSelectionListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarTabs = root.findViewById(R.id.calendarTabs);
        calendarViewPager = root.findViewById(R.id.calendarViewPager);

        calendarTabs.addTab(calendarTabs.newTab().setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED).setIcon(R.drawable.ic_entries_white));
        calendarTabs.addTab(calendarTabs.newTab().setIcon(R.drawable.ic_account_entries_white).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED));
        calendarTabs.addTab(calendarTabs.newTab().setIcon(R.drawable.ic_expense_entries_white).setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_LABELED));
        calendarTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        calendarTabs.setOnTabSelectedListener(this);

        add_fab = root.findViewById(R.id.add_fab);
        layoutEntryFab = root.findViewById(R.id.layoutEntry);
        layoutAccEntryFab = root.findViewById(R.id.layoutAccEntry);
        layoutExpEntryFab = root.findViewById(R.id.layoutExpEntry);
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabExpanded) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        layoutEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newEntryIntent = new Intent(getContext(), EntriesEditPad.class);
                startActivity(newEntryIntent);
                closeSubMenusFab();

            }
        });

        layoutAccEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountEntryIntent = new Intent(getContext(), AccountEntryEditActivity.class);
                startActivity(accountEntryIntent);
                closeSubMenusFab();
            }
        });

        layoutExpEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent expenseEntryIntent = new Intent(getContext(), ExpenseEntryNewActivity.class);
                startActivity(expenseEntryIntent);
                closeSubMenusFab();
            }
        });

        calendarPagerAdapter = new CalendarTabPagerAdapter(getActivity().getSupportFragmentManager(), calendarTabs.getTabCount());
        calendarViewPager.setOffscreenPageLimit(3);
        calendarViewPager.setAdapter(calendarPagerAdapter);

        calendarViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(calendarTabs));

        datePicker = root.findViewById(R.id.datePicker);
        Calendar c = Calendar.getInstance();
        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int newYear, int newMonth, int newDay) {
                date = (newDay < 10 ? "0" + newDay : newDay) + "-" + (newMonth < 9 ? "0" + (newMonth + 1) : (newMonth + 1)) + "-" + newYear;
                Log.i("Event    :::", "date changed to " + date);
                if (jdatePickerSelectionListener != null) {
                    jdatePickerSelectionListener.onDatePickerSelection(date);
                    Log.i("Event    :::", date + "  jour");
                } else {
                    Log.i("Event    :::", "jour datelistener is null");
                }
                if (adatePickerSelectionListener != null) {
                    adatePickerSelectionListener.onDatePickerSelection(date);
                    Log.i("Event    :::", date + "  acc");
                } else {
                    Log.i("Event    :::", "acc datelistener is null");
                }
                if (edatePickerSelectionListener != null) {
                    edatePickerSelectionListener.onDatePickerSelection(date);
                    Log.i("Event    :::", date + "  exp");
                } else {
                    Log.i("Event    :::", "exp datelistener is null");
                }
            }
        });

        closeSubMenusFab();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Calendar");
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
}
