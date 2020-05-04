package com.journalapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.journalapp.calculate.CalculateFragment;
import com.journalapp.calendar.CalendarFragment;
import com.journalapp.charts.ChartsFragment;
import com.journalapp.contact_us.ContactUsFragment;
import com.journalapp.home.HomeFragment;
import com.journalapp.mail.MailFragment;

public class DrawerLayoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    FirebaseAuth mAuth;

    ImageView user_profile_image;
    TextView user_display_name, user_email;

    public static CalendarFragment calendarFragment;
    //    public static DatewiseEntries datewiseEntries;
    private Boolean fabExpanded = false;
    private LinearLayout layoutAccEntryFab, layoutEntryFab, layoutExpEntryFab;
    private FloatingActionButton add_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        View header = getLayoutInflater().inflate(R.layout.nav_header_main, null);

        mAuth = FirebaseAuth.getInstance();

        user_profile_image = header.findViewById(R.id.user_profile_image);
        user_display_name = header.findViewById(R.id.user_display_name);
        user_email = header.findViewById(R.id.user_email);

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            user_display_name.setText(user.getDisplayName());
            user_email.setText(user.getEmail());
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .error(R.drawable.ic_user)
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(user_profile_image);
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.addHeaderView(header);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment, homeFragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        if (menuItem.getItemId() == R.id.nav_calendar) {
            fragment = new CalendarFragment();
            calendarFragment = ((CalendarFragment) fragment);
        } else if (menuItem.getItemId() == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (menuItem.getItemId() == R.id.nav_charts) {
            fragment = new ChartsFragment();
        } else if (menuItem.getItemId() == R.id.nav_calculate) {
            fragment = new CalculateFragment();
        } else if (menuItem.getItemId() == R.id.nav_email) {
            fragment = new MailFragment();
        } else if (menuItem.getItemId() == R.id.nav_contact_us) {
            fragment = new ContactUsFragment();
        } else if (menuItem.getItemId() == R.id.nav_sign_out) {
            AlertDialog.Builder saveAlert = new AlertDialog.Builder(this);
            saveAlert.setTitle("Do you really want to Sign Out?");
            saveAlert.setCancelable(false);
            saveAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
            saveAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            saveAlert.show();
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.commit();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

    }
}
