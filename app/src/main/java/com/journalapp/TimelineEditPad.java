package com.journalapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimelineEditPad extends AppCompatActivity {

    public static SimpleDateFormat dateFormat, timeFormat;
    TextView dateText,timeText,dataText;
    String date,time,data;

    private boolean update=false;

    FloatingActionButton saveFab;

    String user;
    DatabaseReference entriesDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = "Kiran1901";
        entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries/Kiran1901");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_edit_pad);

        saveFab = findViewById(R.id.saveEntryFab);
        dateText = findViewById(R.id.timeline_edit_pad_date);
        timeText = findViewById(R.id.timeline_edit_pad_time);
        dataText = findViewById(R.id.timeline_edit_pad_data);

        Intent intent = getIntent();
        if(!TextUtils.isEmpty(intent.getStringExtra("date"))){
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");
            data = intent.getStringExtra("data");
            dataText.setText(data);
            update = true;

        }else{
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            timeFormat = new SimpleDateFormat("hh:mm:ss a");

            Calendar c = Calendar.getInstance();
            date = dateFormat.format(c.getTime());
            time = timeFormat.format(c.getTime());
        }


        dateText.setText(date);
        timeText.setText(time);

        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(update){
                    updateEntry();
                }else{
                    saveEntry();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(dateText.getText())){
            AlertDialog.Builder saveAlert = new AlertDialog.Builder(TimelineEditPad.this);
            saveAlert.setTitle("Do you want to save?");
            saveAlert.setCancelable(false);
            saveAlert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveEntry();
                }
            });
            saveAlert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(TimelineEditPad.this,"Closing Activity",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            saveAlert.show();
        }
    }

    private void saveEntry(){

        FeedboxDao entry = new FeedboxDao();
        entry.setDate(date);
        entry.setTime(time);
        entry.setData(dataText.getText().toString());

        String key = entriesDb.push().getKey();
        entriesDb.child(key).setValue(entry);

        Toast.makeText(TimelineEditPad.this,"Entry Saved",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateEntry(){
        //TODO update entry code
    }
}
