package com.journalapp.home;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.EntriesMap;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.RecyclerViewAdapter;

import java.util.ArrayList;

import static com.journalapp.EntriesMap.EntriesIndex;


public class EntriesTab extends Fragment {

    String USER =  FirebaseAuth.getInstance().getCurrentUser().getUid();

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;

    CollectionReference journalEntriesRef = FirebaseFirestore.getInstance().collection("journal_entries");
    RecyclerViewAdapter adapter;
    ListenerRegistration liveJournalEntries;

    public EntriesTab() {}

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);

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
                            Log.i("DEBUG    :","ent: "+feedboxDao.getTimestamp().toDate());
                            feedboxesList.add(0,new Feedbox(feedboxDao,key));
                            EntriesMap.addFirst(key);
                            adapter.notifyDataSetChanged();
                            break;

                        case MODIFIED:
                            key = dc.getDocument().getId();
                            feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
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
        recyclerView.setAdapter(adapter);
        return entriesView;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        liveJournalEntries.remove();
    }
}
