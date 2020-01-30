package com.journalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AccountEntryActivity extends AppCompatActivity {

    TextView accountEntryDate;
    TextView accountEntryTime;
    FrameLayout frameLayout;
    public static SimpleDateFormat dateFormat, timeFormat;
    String date,time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_entry);
        accountEntryDate = findViewById(R.id.account_entry_date);
        accountEntryTime = findViewById(R.id.account_entry_time);
        frameLayout = findViewById(R.id.frame_account);

        LayoutInflater layoutInflater = getLayoutInflater();
        View myview = layoutInflater.inflate(R.layout.layout_account_entries,null,false);
        frameLayout.addView(myview);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeFormat = new SimpleDateFormat("hh:mm:ss a");

        Calendar c = Calendar.getInstance();
        date = dateFormat.format(c.getTime());
        time = timeFormat.format(c.getTime());
        accountEntryDate.setText(date);
        accountEntryTime.setText(time);

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_category_give:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_category_take:
                if (checked)
                    // Ninjas rule
                    break;
        }

    }
}
