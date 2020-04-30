package com.journalapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.journalapp.calculate.CalculateFragment;
import com.journalapp.calendar.CalendarFragment;
import com.journalapp.charts.ChartsFragment;
import com.journalapp.home.HomeFragment;
import com.journalapp.mail.MailFragment;

public class DrawerLayoutActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    public static CalendarFragment calendarFragment;
//    public static DatewiseEntries datewiseEntries;
    private Boolean fabExpanded = false;
    private LinearLayout layoutAccEntryFab, layoutEntryFab , layoutExpEntryFab;
    private FloatingActionButton add_fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment,homeFragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        if(menuItem.getItemId() == R.id.nav_calendar)
        {
            fragment = new CalendarFragment();
            calendarFragment = ((CalendarFragment) fragment);
        }
        else if(menuItem.getItemId() == R.id.nav_home)
        {
            fragment = new HomeFragment();
        }
        else if(menuItem.getItemId() == R.id.nav_charts)
        {
            fragment = new ChartsFragment();
        }
        else if(menuItem.getItemId() == R.id.nav_calculate)
        {
            fragment = new CalculateFragment();
        }
        else if(menuItem.getItemId() == R.id.nav_email)
        {
            fragment = new MailFragment();
        }

        if (fragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment,fragment);
            fragmentTransaction.commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
