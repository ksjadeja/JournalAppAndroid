package com.journalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TimelineViewPad extends AppCompatActivity {

    String date,time,data,id;
    FloatingActionButton editFab;

    TextView dateField,timeField,dataField;

    ImageButton deleteEntryButton;

    DatabaseReference entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_view_pad);

        editFab = findViewById(R.id.editEntryFab);
        dateField = findViewById(R.id.timeline_view_pad_date);
        timeField = findViewById(R.id.timeline_view_pad_time);
        dataField = findViewById(R.id.timeline_view_pad_data);

        deleteEntryButton = findViewById(R.id.deleteEntryButton);

        deleteEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });

        Intent intent = getIntent();
        date = intent.getStringExtra("dateField");
        time = intent.getStringExtra("time");
        data = intent.getStringExtra("data");
        id = intent.getStringExtra("id");

        dateField.setText(date);
        timeField.setText(time);
        dataField.setText(data);

        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEntry();
            }
        });

    }

    private void editEntry(){
        Intent intent = new Intent(getApplicationContext(), TimelineEditPad.class);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        intent.putExtra("data",data);
        intent.putExtra("id",id);
        startActivity(intent);
        finish();
    }

    private void deleteEntry(){
        entriesDb.child(id).removeValue();
        EntriesMap.delete(id,EntriesMap.EntriesIndex.get(id));
        finish();
    }
}
