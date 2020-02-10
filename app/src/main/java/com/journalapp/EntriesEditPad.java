package com.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journalapp.models.FeedboxDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EntriesEditPad extends AppCompatActivity {

    public static SimpleDateFormat dateFormat, timeFormat;
    TextView dateText,timeText,dataText;
    String date,time,data,id;

    private boolean update=false;

    FloatingActionButton saveFab;

    String user;
    DatabaseReference entriesDb,byDateDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = "Kiran1901";
        entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child(user);
        byDateDb = FirebaseDatabase.getInstance().getReference().child("by_date").child(user);


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
            id = intent.getStringExtra("id");
            dataText.setText(data);
            update = true;

        }else{
            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
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
                if (TextUtils.isEmpty(dataText.getText())){
                    Toast.makeText(EntriesEditPad.this,"Enter something",Toast.LENGTH_LONG).show();
                }else{
                    data = dataText.getText().toString();
                    if(update){
                        updateEntry();
                    }else{
                        saveEntry();
                    }
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(dataText.getText())){
            data = dataText.getText().toString();
            AlertDialog.Builder saveAlert = new AlertDialog.Builder(EntriesEditPad.this);
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
                    Toast.makeText(EntriesEditPad.this,"Closing Activity",Toast.LENGTH_SHORT).show();
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
        entry.setData(data);

        final String key = entriesDb.push().getKey();
        entriesDb.child(key).setValue(entry);
        byDateDb.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String newKey = byDateDb.child(date).child("journal_entries").push().getKey();
                byDateDb.child(date).child("journal_entries").child(newKey).setValue(key);
                Log.i("msg2","added in by_entry");
                Toast.makeText(EntriesEditPad.this,"added in by_entry",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(EntriesEditPad.this,"Entry Saved",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateEntry() {
        FeedboxDao entry = new FeedboxDao();
        entry.setDate(date);
        entry.setTime(time);
        entry.setData(data);

        entriesDb.child(id).setValue(entry);

        Toast.makeText(EntriesEditPad.this, "Entry Updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
