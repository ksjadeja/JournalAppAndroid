package com.journalapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EntriesEditPad extends AppCompatActivity {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
    TextView dateText,timeText,dataText;

    Feedbox feedbox;

    private boolean update=false;

    String USER = "Kiran1901";

    FloatingActionButton saveFab;

    CollectionReference journalEntriesRef = FirebaseFirestore.getInstance().collection("journal_entries");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_edit_pad);

        saveFab = findViewById(R.id.saveEntryFab);
        dateText = findViewById(R.id.timeline_edit_pad_date);
        timeText = findViewById(R.id.timeline_edit_pad_time);
        dataText = findViewById(R.id.timeline_edit_pad_data);

        Intent intent = getIntent();
        if(intent.hasExtra("feedbox")){
            feedbox = ((Feedbox) intent.getSerializableExtra("feedbox"));
            dataText.setText(feedbox.getData());
            dateText.setText(feedbox.getDate());
            timeText.setText(feedbox.getTime());
            update = true;
        }else{

            Calendar c = Calendar.getInstance();
            feedbox = new Feedbox();
            feedbox.setDate(dateFormat.format(c.getTime()));
            feedbox.setTime(timeFormat.format(c.getTime()));
            feedbox.setTimestamp(new Date());
            dateText.setText(feedbox.getDate());
            timeText.setText(feedbox.getTime());
            dataText.setText("Write here..");
        }

        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(dataText.getText())){
                    Toast.makeText(EntriesEditPad.this,"Enter something",Toast.LENGTH_LONG).show();
                }else{
                    feedbox.setData(dataText.getText().toString());
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

        FeedboxDao feedboxDao = new FeedboxDao(feedbox);

        journalEntriesRef.document(USER).collection("entries").add(feedboxDao).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    Log.i("Status:","Entry added successfully");
                    Toast.makeText(EntriesEditPad.this,"Entry Saved",Toast.LENGTH_SHORT).show();
                }else {
                    Log.i("Status:","db entry is not successful");
                }
            }
        });
        finish();
    }

    private void updateEntry() {

        FeedboxDao feedboxDao = new FeedboxDao(feedbox);

        journalEntriesRef.document(USER).collection("entries").document(feedbox.getId()).set(feedboxDao);

        Toast.makeText(EntriesEditPad.this, "Entry Updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
