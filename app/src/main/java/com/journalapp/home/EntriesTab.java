package com.journalapp.home;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

//        getEntriesFromFirebase();

        adapter = new RecyclerViewAdapter(getContext(), feedboxesList);
        recyclerView.setAdapter(adapter);

//        childEventListener = updateEntriesFromFirebaseRealtime();

        return entriesView;
    }

//    private void getEntriesFromFirebase() {
//        entiesDb.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String key;
//                FeedboxDao feedboxDao;
//                Feedbox feedbox;
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    key = ds.getKey();
//                    feedboxDao = ds.getValue(FeedboxDao.class);
//                    feedbox = new Feedbox(feedboxDao, key);
//                    feedboxesList.add(feedbox);
//                }
//                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                Collections.reverse(feedboxesList);
//
//                for (int i=0;i<feedboxesList.size();i++){
//                    EntriesIndex.put(feedboxesList.get(i).getId(),i);
//                }
//
//                final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), feedboxesList);
//                recyclerView.setAdapter(adapter);
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        adapter.addNewData("This is new data which I added");
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(),"Firebase Error: "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private ChildEventListener updateEntriesFromFirebaseRealtime() {
        return entiesDb.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String key;
                        FeedboxDao feedboxDao;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            key = ds.getKey();
                            feedboxDao = dataSnapshot.getValue(FeedboxDao.class);

                            Log.i("data",feedboxDao.getDate());
                            Log.i("data",feedboxDao.getTime());
                            Log.i("data",feedboxDao.getData());

                            feedboxesList.add(0,new Feedbox(feedboxDao,key));
                            EntriesMap.addFirst(key);
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String key;
                        FeedboxDao feedboxDao;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            key = ds.getKey();
                            feedboxDao = ds.getValue(FeedboxDao.class);

                            int index = EntriesIndex.get(key);
                            feedboxesList.set(index,new Feedbox(feedboxDao,key));
                            adapter.notifyItemChanged(index);
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        String key;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            key = ds.getKey();

                            int index = EntriesIndex.get(key);
                            feedboxesList.remove(index);
                            adapter.notifyItemRemoved(index);
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(),"Firebase Error: "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        entiesDb.removeEventListener(childEventListener);
    }
}
