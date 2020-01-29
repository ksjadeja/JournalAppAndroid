package com.journalapp.home;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journalapp.EntriesMap;
import com.journalapp.MainActivity;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

import static com.journalapp.EntriesMap.EntriesIndex;


public class EntriesTab extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;

    DatabaseReference entiesDb;

    public EntriesTab() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);
        entiesDb = FirebaseDatabase.getInstance().getReference("journal_entries/").child("Kiran1901");

        feedboxesList = new ArrayList<>();

        getEntriesFromFirebase();

        return entriesView;
    }
    LayoutInflater layoutInflater = getLayoutInflater();
    View view = layoutInflater.inflate(R.layout.feedbox_layout,null);



    private void getEntriesFromFirebase(){
        entiesDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key;
                FeedboxDao feedboxDao;
                Feedbox feedbox;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    key = ds.getKey();
                    feedboxDao = ds.getValue(FeedboxDao.class);
                    feedbox = new Feedbox(feedboxDao,key);
                    feedboxesList.add(feedbox);
                    Toast.makeText(getContext(),"Data Fetched"+feedbox.hashCode(),Toast.LENGTH_SHORT).show();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                Collections.reverse(feedboxesList);
                Log.i("key",  ""+feedboxesList.size() );
                RecyclerViewAdapter adapter= new RecyclerViewAdapter(getContext(), feedboxesList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateEntriesFromFirebaseRealtime(){
        entiesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key;Feedbox feedbox;
                TextView date,time,data;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    key = ds.getKey();
                    feedbox = ds.getValue(Feedbox.class);
                    View entryCard = LayoutInflater.from(getContext()).inflate(R.layout.feedbox_layout,null);
                    date=entryCard.findViewById(R.id.dateField);
                    time=entryCard.findViewById(R.id.timeField);
                    data=entryCard.findViewById(R.id.dataField);

                    date.setText(feedbox.getDate());
                    time.setText(feedbox.getTime());
                    data.setText(feedbox.getTime());

                    recyclerView.addView(entryCard,0);
                    EntriesIndex.put(key,0);
                }
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
    }

}
