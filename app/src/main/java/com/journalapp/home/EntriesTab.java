package com.journalapp.home;


import android.app.Activity;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.EntriesMap;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import static com.journalapp.EntriesMap.EntriesIndex;


public class EntriesTab extends Fragment {

    String USER = "Kiran1901";

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;
    DatabaseReference entriesDb;

    CollectionReference journalEntriesRef = FirebaseFirestore.getInstance().collection("journal_entries");
    CollectionReference byDateEntriesRef = FirebaseFirestore.getInstance().collection("by_date");

    RecyclerViewAdapter adapter;
    ListenerRegistration liveJournalEntries;

    public EntriesTab() {}


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);
//        entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901");


        feedboxesList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter(getContext(), feedboxesList);

        liveJournalEntries = journalEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    String key=null;
                    FeedboxDao feedboxDao = null;

                    switch (dc.getType()) {
                        case ADDED:
                            key = dc.getDocument().getId();
                            feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
                            Log.i("DEBUG    :","Timestamp:::"+feedboxDao.getTimestamp().toDate());
                            feedboxesList.add(0,new Feedbox(feedboxDao,key));
                            EntriesMap.addFirst(key);
                            adapter.notifyDataSetChanged();
                            break;

                        case MODIFIED:
                            key = dc.getDocument().getId();
                            feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
                            Log.i("DEBUG    :","Timestamp:::"+feedboxDao.getTimestamp().toDate());
                            int index = EntriesIndex.get(key);
                            feedboxesList.set(index,new Feedbox(feedboxDao,key));
                            adapter.notifyDataSetChanged();
                            break;

                        case REMOVED:
                            for(Feedbox fb:feedboxesList){            //TODO optimize it futher
                                if(fb.getId().equals(dc.getDocument().getId())){
                                    EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
                                    feedboxesList.remove(fb);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            break;
                    }
                }

            }
        });

//        entriesDb.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String key;
//                FeedboxDao feedboxDao;
//                key = dataSnapshot.getKey();
//                feedboxDao = dataSnapshot.getValue(FeedboxDao.class);
//
//
//                feedboxesList.add(0,new Feedbox(feedboxDao,key));
//                EntriesMap.addFirst(key);
//                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String key;
//                FeedboxDao feedboxDao;
//                key = dataSnapshot.getKey();
//                feedboxDao = dataSnapshot.getValue(FeedboxDao.class);
//                int index = EntriesIndex.get(key);
//                feedboxesList.set(index,new Feedbox(feedboxDao,key));
//                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
////                int index = EntriesIndex.get(dataSnapshot.getKey());
////                entries.remove(index);
////                EntriesMap.delete(dataSnapshot.getKey(),index);
////                notifyDataSetChanged();
//
//                for(Feedbox fb:feedboxesList){
//                    if(fb.getId().equals(dataSnapshot.getKey())){
//                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
//                        feedboxesList.remove(fb);
//                        adapter.notifyDataSetChanged();
//                        return;
//                    }
//                }
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(),"Firebase Error: "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });

        recyclerView.setAdapter(adapter);


        return entriesView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        liveJournalEntries.remove();
    }
}
