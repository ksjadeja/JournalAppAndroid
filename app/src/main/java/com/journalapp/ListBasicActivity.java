package com.journalapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ListBasicActivity extends AppCompatActivity {

    ListView listView;
    String str[] = {"Apple","Google","Tesla","Java","C","Python","PHP","Spring"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_basic);

        listView = findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.raw_list_view,R.id.tv_list);
        arrayAdapter.addAll(str);
        listView.setAdapter(arrayAdapter);
    }
}
