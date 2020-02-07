package com.journalapp.home;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.utils.RecyclerViewAdapter;

import java.util.ArrayList;



public class EntriesTab extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;
    Button button;
    DatabaseReference entiesDb;
    RecyclerViewAdapter adapter;

    ChildEventListener childEventListener;
    public EntriesTab() {
        // Required empty public constructor

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);
        entiesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901");

        button = entriesView.findViewById(R.id.btn_add_item);

        feedboxesList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter(getContext(), feedboxesList);
        recyclerView.setAdapter(adapter);


        return entriesView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        entiesDb.removeEventListener(childEventListener);
    }
}
