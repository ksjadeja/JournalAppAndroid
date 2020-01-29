package com.journalapp.home;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.utils.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

import static com.journalapp.EntriesMap.EntriesIndex;


public class EntriesTab extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;
Button button;
    DatabaseReference entiesDb;
    RecyclerViewAdapter adapter;
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

        button = entriesView.findViewById(R.id.btn_add_item);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addNewData("This is new data which I added");
            }
        });
        feedboxesList = new ArrayList<>();
        Feedbox feedbox;
        for (int i=0;i<15;i++)
        {
            feedbox = new Feedbox();
            feedbox.setDate("date"+i);
            feedbox.setTime("time"+i);
            feedbox.setData("Descr"+i);
            feedboxesList.add(feedbox);

        }

        // FeedboxListAdapter feedboxListAdapter = new FeedboxListAdapter(feedboxListView.getContext(),feedboxesList);
        // feedboxListView.setAdapter(feedboxListAdapter);
        // feedboxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //     @Override
        //     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //
        //     }
        // });

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        Collections.reverse(feedboxesList);
        adapter= new RecyclerViewAdapter(getContext(), feedboxesList);
        recyclerView.setAdapter(adapter);


        return entriesView;
    }
//    LayoutInflater layoutInflater = getLayoutInflater();
//    View view = layoutInflater.inflate(R.layout.feedbox_layout,null);


    private void getEntriesFromFirebase(){
        entiesDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key;Feedbox feedbox;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    key = ds.getKey();
                    feedbox = ds.getValue(Feedbox.class);
                    EntriesMap.EntriesMap.put(feedbox,key);
                    View entryCard = LayoutInflater.from(getContext()).inflate(R.layout.feedbox_layout,null);
                    recyclerView.addView(entryCard,0);
                    EntriesIndex.put(key,0);
                }
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
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Feedbox feedbox = ds.getValue(Feedbox.class);
                    EntriesMap.EntriesMap.put(feedbox,key);
                    View entryCard = LayoutInflater.from(getContext()).inflate(R.layout.feedbox_layout,null);
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
