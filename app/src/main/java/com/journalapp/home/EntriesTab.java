package com.journalapp.home;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.journalapp.EntriesMap;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.RecyclerViewAdapter;

import java.util.ArrayList;

import static com.journalapp.EntriesMap.EntriesIndex;


public class EntriesTab extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;
    DatabaseReference entriesDb;
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
        entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901");


        feedboxesList = new ArrayList<>();


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter(getContext(), feedboxesList);

        entriesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key;
                FeedboxDao feedboxDao;
                key = dataSnapshot.getKey();
                feedboxDao = dataSnapshot.getValue(FeedboxDao.class);

                Log.i("data",feedboxDao.getDate());
                Log.i("data",feedboxDao.getTime());
                Log.i("data",feedboxDao.getData());

                feedboxesList.add(0,new Feedbox(feedboxDao,key));
                EntriesMap.addFirst(key);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key;
                FeedboxDao feedboxDao;
                key = dataSnapshot.getKey();
                feedboxDao = dataSnapshot.getValue(FeedboxDao.class);

                int index = EntriesIndex.get(key);
                feedboxesList.set(index,new Feedbox(feedboxDao,key));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

//                int index = EntriesIndex.get(dataSnapshot.getKey());
//                entries.remove(index);
//                EntriesMap.delete(dataSnapshot.getKey(),index);
//                notifyDataSetChanged();

                for(Feedbox fb:feedboxesList){
                    if(fb.getId().equals(dataSnapshot.getKey())){
                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
                        feedboxesList.remove(fb);
                        adapter.notifyDataSetChanged();
                        return;
                    }
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

        recyclerView.setAdapter(adapter);


        return entriesView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        entriesDb.removeEventListener(childEventListener);
    }
}
