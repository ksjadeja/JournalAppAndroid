package com.journalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TimelineViewPad extends AppCompatActivity {

    String date,time,data,id;
    FloatingActionButton editFab;

    TextView dateField,timeField,dataField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_view_pad);

        editFab = findViewById(R.id.editEntryFab);
        dateField = findViewById(R.id.timeline_view_pad_date);
        timeField = findViewById(R.id.timeline_view_pad_time);
        dataField = findViewById(R.id.timeline_view_pad_data);

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
        //TODO edit entry transition code
    }
}
