package com.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
    GoogleSignInClient mGoogleSignInClient;

    Fragment last_fragment;


    ImageView user_profile_image;
    TextView user_display_name, user_email;

    public static CalendarFragment calendarFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);
        View header = getLayoutInflater().inflate(R.layout.nav_header_main, null);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
        homeFragment.setRetainInstance(true);
        last_fragment = homeFragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment, homeFragment, "home");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        if (menuItem.getItemId() == R.id.nav_calendar) {
            if (getSupportFragmentManager().findFragmentByTag("calendar") == null) {
                fragment = new CalendarFragment();
                fragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().hide(last_fragment).add(R.id.nav_host_fragment, fragment, "calendar").commit();
                calendarFragment = ((CalendarFragment) fragment);
                last_fragment = fragment;
            } else {
                getSupportFragmentManager().beginTransaction().hide(last_fragment).show(getSupportFragmentManager().findFragmentByTag("calendar")).commit();
                last_fragment = getSupportFragmentManager().findFragmentByTag("calendar");
            }
            this.setTitle("Calendar");
        } else if (menuItem.getItemId() == R.id.nav_home) {
            if (getSupportFragmentManager().findFragmentByTag("home") == null) {
                fragment = new HomeFragment();
                fragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment, fragment, "home").commit();
                last_fragment = fragment;
            } else {
                getSupportFragmentManager().beginTransaction().hide(last_fragment).show(getSupportFragmentManager().findFragmentByTag("home")).commit();
                last_fragment = getSupportFragmentManager().findFragmentByTag("home");
            }
            this.setTitle("Timeline");
        } else if (menuItem.getItemId() == R.id.nav_charts) {
            if (getSupportFragmentManager().findFragmentByTag("chart") == null) {
                fragment = new ChartsFragment();
                fragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().hide(last_fragment).add(R.id.nav_host_fragment, fragment, "chart").commit();
                last_fragment = fragment;
            } else {
                getSupportFragmentManager().beginTransaction().hide(last_fragment).show(getSupportFragmentManager().findFragmentByTag("chart")).commit();
                last_fragment = getSupportFragmentManager().findFragmentByTag("chart");
            }
            this.setTitle("Charts");
        } else if (menuItem.getItemId() == R.id.nav_calculate) {
            if (getSupportFragmentManager().findFragmentByTag("calculate") == null) {
                fragment = new CalculateFragment();
                fragment.setRetainInstance(false);
                getSupportFragmentManager().beginTransaction().hide(last_fragment).add(R.id.nav_host_fragment, fragment, "calculate").commit();
                last_fragment = fragment;
            } else {
                getSupportFragmentManager().beginTransaction().hide(last_fragment).show(getSupportFragmentManager().findFragmentByTag("calculate")).commit();
                last_fragment = getSupportFragmentManager().findFragmentByTag("calculate");
            }
            this.setTitle("Calculate");
        } else if (menuItem.getItemId() == R.id.nav_email) {
            if (getSupportFragmentManager().findFragmentByTag("mail") == null) {
                fragment = new MailFragment();
                fragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().hide(last_fragment).add(R.id.nav_host_fragment, fragment, "mail").commit();
                last_fragment = fragment;
            } else {
                getSupportFragmentManager().beginTransaction().hide(last_fragment).show(getSupportFragmentManager().findFragmentByTag("mail")).commit();
                last_fragment = getSupportFragmentManager().findFragmentByTag("mail");
            }
            this.setTitle("Mail");
        } else if (menuItem.getItemId() == R.id.nav_contact_us) {
            if (getSupportFragmentManager().findFragmentByTag("contact") == null) {
                fragment = new ContactUsFragment();
                fragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().hide(last_fragment).add(R.id.nav_host_fragment, fragment, "contact").commit();
                last_fragment = fragment;
            } else {
                getSupportFragmentManager().beginTransaction().hide(last_fragment).show(getSupportFragmentManager().findFragmentByTag("contact")).commit();
                last_fragment = getSupportFragmentManager().findFragmentByTag("contact");
            }
            this.setTitle("Contact Us");
        } else if (menuItem.getItemId() == R.id.nav_sign_out) {
            AlertDialog.Builder saveAlert = new AlertDialog.Builder(this);
            saveAlert.setTitle("Do you really want to Sign Out?");
            saveAlert.setCancelable(false);
            saveAlert.setPositiveButton("Yes", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });
            saveAlert.setNegativeButton("No", (dialog, which) -> {
                return;
            });
            saveAlert.show();
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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("home") != null) {
            Fragment homeFragment = getSupportFragmentManager().findFragmentByTag("home");
            if(homeFragment.isHidden()){
               getSupportFragmentManager().beginTransaction().hide(last_fragment).show(homeFragment).commit();
               last_fragment = homeFragment;
               return;
            }
        }
        finish();
    }

}
