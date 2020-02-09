package com.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EntriesViewPad extends AppCompatActivity {

    String date,time,data,id;
    FloatingActionButton editFab;

    TextView dateField,timeField,dataField;

    ImageButton deleteEntryButton;

    DatabaseReference entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901");
    DatabaseReference byDateDb = FirebaseDatabase.getInstance().getReference("by_date").child("Kiran1901");


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
        Intent intent = new Intent(getApplicationContext(), EntriesEditPad.class);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        intent.putExtra("data",data);
        intent.putExtra("id",id);
        startActivity(intent);
        finish();
    }

    private void deleteEntry(){
        entriesDb.child(id).removeValue();
        byDateDb.child(date).child("journal_entries").orderByValue().equalTo(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                byDateDb.child(date).child("journal_entries").child(dataSnapshot.getKey()).removeValue();
                Toast.makeText(EntriesViewPad.this,"Deleted from by_date",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        EntriesMap.delete(id,EntriesMap.EntriesIndex.get(id));
        finish();
    }
}
