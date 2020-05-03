package com.journalapp.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.AccEntriesMap;
import com.journalapp.EntriesMap;
import com.journalapp.ExpEntriesMap;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.AccountRecyclerViewAdapter;
import com.journalapp.utils.ExpenseRecyclerViewAdapter;
import com.journalapp.utils.ExpenseRecyclerViewAdapterView;

import java.util.ArrayList;

import static com.journalapp.AccEntriesMap.AccEntriesIndex;
import static com.journalapp.EntriesMap.EntriesIndex;
import static com.journalapp.ExpEntriesMap.ExpEntriesIndex;

public class ExpEntriesTab extends Fragment {
    RecyclerView recyclerView;
    ArrayList<ExpenseBox> expenseEntryList;
    String USER= FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";
    CollectionReference expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");
    ExpenseRecyclerViewAdapterView adapter;
    ListenerRegistration liveExpenseEntries;


    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private int limit = 5;
    private DocumentSnapshot lastVisible;


    public ExpEntriesTab (){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home_expense_entries, container, false);
        recyclerView=rootView.findViewById(R.id.exp_recycler_view);

        expenseEntryList= new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseRecyclerViewAdapterView(getContext(), expenseEntryList);
        liveExpenseEntries = expenseEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                int i=0;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    String key=null;
                    ExpenseBoxDao expenseBoxDao= null;
                    switch (dc.getType()) {
                        case ADDED:
                            key = dc.getDocument().getId();
                            expenseBoxDao= dc.getDocument().toObject(ExpenseBoxDao.class);
                            Log.i("DEBUG    :","exp: "+expenseBoxDao.getTimestamp().toDate());
                            expenseEntryList.add(0,new ExpenseBox(expenseBoxDao,key));
                            ExpEntriesMap.addFirst(key);
                            break;

                        case MODIFIED:
                            key = dc.getDocument().getId();
                            expenseBoxDao= dc.getDocument().toObject(ExpenseBoxDao.class);
                            int index = ExpEntriesIndex.get(key);
                            expenseEntryList.set(index,new ExpenseBox(expenseBoxDao,key));
                            break;

                        case REMOVED:
                            for(ExpenseBox ex:expenseEntryList){            //TODO optimize it futher
                                if(ex.getId().equals(dc.getDocument().getId())){
                                    ExpEntriesMap.delete(ex.getId(),expenseEntryList.indexOf(ex));
                                    expenseEntryList.remove(ex);
                                    break;
                                }
                            }
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
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

                    expenseEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                String key=null;
                                ExpenseBoxDao expenseBoxDao= null;
                                switch (dc.getType()) {
                                    case ADDED:
                                        key = dc.getDocument().getId();
                                        expenseBoxDao= dc.getDocument().toObject(ExpenseBoxDao.class);
                                        Log.i("DEBUG    :","exp: "+expenseBoxDao.getTimestamp().toDate());
                                        expenseEntryList.add(new ExpenseBox(expenseBoxDao,key));
                                        ExpEntriesMap.addFirst(key);
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        expenseBoxDao= dc.getDocument().toObject(ExpenseBoxDao.class);
                                        int index = ExpEntriesIndex.get(key);
                                        expenseEntryList.set(index,new ExpenseBox(expenseBoxDao,key));
                                        break;

                                    case REMOVED:
                                        for(ExpenseBox ex:expenseEntryList){            //TODO optimize it futher
                                            if(ex.getId().equals(dc.getDocument().getId())){
                                                ExpEntriesMap.delete(ex.getId(),expenseEntryList.indexOf(ex));
                                                expenseEntryList.remove(ex);
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

        return rootView;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        liveExpenseEntries.remove();
    }
}
