package com.journalapp.calendar;

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

public class DatewiseEntries extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;
    DatabaseReference entriesDb;
    RecyclerViewAdapter recyclerViewAdapter;

    String startDate = "08/02/2020";


    public DatewiseEntries() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);
        entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901")
                                    .orderByChild("date").equalTo(startDate).getRef();
        feedboxesList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), feedboxesList);

// db listener
        entriesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key;
                FeedboxDao feedboxDao;
                key = dataSnapshot.getKey();
                feedboxDao = dataSnapshot.getValue(FeedboxDao.class);

                Log.i("data:cal",feedboxDao.getDate());
                Log.i("data:cal",feedboxDao.getTime());
                Log.i("data:cal",feedboxDao.getData());

                feedboxesList.add(0,new Feedbox(feedboxDao,key));
//                EntriesMap.addFirst(key);
                recyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key;
                FeedboxDao feedboxDao;
                key = dataSnapshot.getKey();
                feedboxDao = dataSnapshot.getValue(FeedboxDao.class);

                int index = EntriesIndex.get(key);
                feedboxesList.set(index,new Feedbox(feedboxDao,key));
                recyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for(Feedbox fb:feedboxesList){
                    if(fb.getId().equals(dataSnapshot.getKey())){
                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
                        feedboxesList.remove(fb);
                        recyclerViewAdapter.notifyDataSetChanged();
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

        recyclerView.setAdapter(recyclerViewAdapter);


        return entriesView;
    }
}
