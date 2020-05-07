package com.journalapp.home;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
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

    String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;

    CollectionReference journalEntriesRef = FirebaseFirestore.getInstance().collection("journal_entries");
    RecyclerViewAdapter adapter;
    ListenerRegistration liveJournalEntries;

    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private int limit = 5;
    private DocumentSnapshot lastVisible;


    public EntriesTab() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View entriesView = inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView = entriesView.findViewById(R.id.recycler_view);

        feedboxesList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter(getContext(), feedboxesList);

        liveJournalEntries = journalEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.DESCENDING).limit(limit).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    String key = null;
                    FeedboxDao feedboxDao = null;

                    switch (dc.getType()) {
                        case ADDED:
                            key = dc.getDocument().getId();
                            feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
                            Log.i("DEBUG    :", "ent: " + feedboxDao.getTimestamp().toDate());
                            if(feedboxesList.size()>0 &&  feedboxesList.get(0).getTimestamp().compareTo(feedboxDao.getTimestamp().toDate()) < 0 ){
                                feedboxesList.add(0, new Feedbox(feedboxDao, key));
                            }else{
                                feedboxesList.add(new Feedbox(feedboxDao, key));
                            }
                            EntriesMap.addFirst(key);
                            break;

                        case MODIFIED:
                            key = dc.getDocument().getId();
                            feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
                            int index = EntriesIndex.get(key);
                            feedboxesList.set(index, new Feedbox(feedboxDao, key));
                            break;

                        case REMOVED:
                            for (Feedbox fb : feedboxesList) {            //TODO optimize it futher
                                if (fb.getId().equals(dc.getDocument().getId())) {
                                    EntriesMap.delete(fb.getId(), feedboxesList.indexOf(fb));
                                    feedboxesList.remove(fb);
                                    break;
                                }
                            }
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
                if(snapshots.size()!=0)
                    lastVisible = snapshots.getDocuments().get(snapshots.size()-1);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();

                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                    isScrolling = false;

                    journalEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                String key = null;
                                FeedboxDao feedboxDao = null;

                                switch (dc.getType()) {
                                    case ADDED:
                                        key = dc.getDocument().getId();
                                        feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
                                        Log.i("DEBUG    :", "ent: " + feedboxDao.getTimestamp().toDate());
                                        feedboxesList.add(new Feedbox(feedboxDao, key));
                                        EntriesMap.addFirst(key);
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
                                        int index = EntriesIndex.get(key);
                                        feedboxesList.set(index, new Feedbox(feedboxDao, key));
                                        break;

                                    case REMOVED:
                                        for (Feedbox fb : feedboxesList) {
                                            if (fb.getId().equals(dc.getDocument().getId())) {
                                                EntriesMap.delete(fb.getId(), feedboxesList.indexOf(fb));
                                                feedboxesList.remove(fb);
                                                break;
                                            }
                                        }
                                        break;
                                }
                            }
                            adapter.notifyDataSetChanged();

                            if(snapshots.size() != 0){
                                lastVisible = snapshots.getDocuments().get(snapshots.size()-1);
                            }

                            if (snapshots.size() < limit) {
                                isLastItemReached = true;
                            }
                        }
                    });
                }
            }
        });

        return entriesView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        liveJournalEntries.remove();
    }
}
