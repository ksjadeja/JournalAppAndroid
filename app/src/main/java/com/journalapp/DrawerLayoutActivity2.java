package com.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.journalapp.calendar.CalendarFragment;
import com.journalapp.calendar.DatewiseEntries;
import com.journalapp.home.HomeFragment;

public class DrawerLayoutActivity2 extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    public static CalendarFragment calendarFragment;
    public static DatewiseEntries datewiseEntries;

    private Boolean fabExpanded = false;
    private LinearLayout layoutAccEntryFab, layoutEntryFab;
    private FloatingActionButton add_fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        add_fab = findViewById(R.id.add_fab);
        layoutEntryFab = findViewById(R.id.layoutEntry);
        layoutAccEntryFab = findViewById(R.id.layoutAccEntry);
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
                Toast.makeText(DrawerLayoutActivity2.this,"You've tapped new Entry",Toast.LENGTH_SHORT).show();

                Intent newEntryIntent = new Intent(DrawerLayoutActivity2.this, EntriesEditPad.class);
                startActivity(newEntryIntent);

            }
        });

        layoutAccEntryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DrawerLayoutActivity2.this,"You've tapped new Account Entry",Toast.LENGTH_SHORT).show();
                Intent accountEntryIntent = new Intent(DrawerLayoutActivity2.this,AccountEntryActivity.class);
                startActivity(accountEntryIntent);
                //TODO
            }
        });

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
        closeSubMenusFab();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        if(menuItem.getItemId() == R.id.nav_gallery)
        {
            fragment = new CalendarFragment();
            calendarFragment = ((CalendarFragment) fragment);
        }
        else if(menuItem.getItemId() == R.id.nav_home)
        {
            fragment = new HomeFragment();
        }
        else if(menuItem.getItemId() == R.id.nav_slideshow)
        {
            fragment = new SlideShowFragment2();
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
