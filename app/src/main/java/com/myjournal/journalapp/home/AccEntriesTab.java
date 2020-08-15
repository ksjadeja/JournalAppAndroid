package com.myjournal.journalapp.home;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.myjournal.journalapp.AccEntriesMap;
import com.journalapp.R;
import com.myjournal.journalapp.models.AccountBox;
import com.myjournal.journalapp.models.AccountBoxDao;
import com.myjournal.journalapp.models.MailBean;
import com.myjournal.journalapp.utils.AccountRecyclerViewAdapter;

import java.util.ArrayList;

import static com.myjournal.journalapp.AccEntriesMap.AccEntriesIndex;


public class AccEntriesTab extends Fragment {

    RecyclerView recyclerView;
    ArrayList<AccountBox> accountEntryList;
    String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();       // "Kiran1901";
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
    CollectionReference mailEntriesRef = FirebaseFirestore.getInstance().collection("mailing_list");
    AccountRecyclerViewAdapter adapter;
    ListenerRegistration liveAccountEntries;


    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private int limit = 5;
    private DocumentSnapshot lastVisible;


    public AccEntriesTab() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_home_acc_entries, container, false);
        recyclerView = rootView.findViewById(R.id.acc_recycler_view);
        accountEntryList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AccountRecyclerViewAdapter(getContext(), accountEntryList);
        liveAccountEntries = accountEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.i("ERROR:", "listen:error", e);
                return;
            }
            int i = 0;
            if (snapshots == null) throw new AssertionError();
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                String key = null;
                AccountBoxDao accountBoxDao = null;
                switch (dc.getType()) {
                    case ADDED:
                        key = dc.getDocument().getId();
                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);

                        if (accountEntryList.size() > 0 && accountEntryList.get(0).getTimestamp().compareTo(accountBoxDao.getTimestamp().toDate()) < 0) {
                            accountEntryList.add(0, new AccountBox(accountBoxDao, key));
                            AccEntriesMap.addFirst(key);
                        } else {
                            accountEntryList.add(new AccountBox(accountBoxDao, key));
                            AccEntriesIndex.put(key, accountEntryList.size() - 1);
                        }
                        break;

                    case MODIFIED:
                        key = dc.getDocument().getId();
                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                        int index = AccEntriesIndex.get(key);
                        AccountBox oldAccBox = accountEntryList.get(index);
                        accountEntryList.set(index, new AccountBox(accountBoxDao, key));

                        MailBean mailBean = new MailBean();
                        String name = accountBoxDao.getName();
                        mailBean.setPersonName(name);
                        mailBean.setEmail("");
                        mailBean.setEmailEntered(false);

                            mailEntriesRef.document(USER).collection("entries").document(name).get().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task12.getResult();
                                    if(documentSnapshot.exists()) {
                                        Log.i("MAIL::STATUS", "User already exists(modify)");
                                        mailEntriesRef.document(USER).collection("entries").document(name).update("count", FieldValue.increment(1));
                                        mailEntriesRef.document(USER).collection("entries").document(oldAccBox.getName()).update("count", FieldValue.increment(-1));
                                    }else{
                                        Log.i("MAIL::STATUS", "User does not exist(modify)");
                                        mailBean.setCount(1);
                                        mailEntriesRef.document(USER).collection("entries").document(oldAccBox.getName()).update("count",FieldValue.increment(-1)).addOnCompleteListener(task -> {
                                            if(task.isSuccessful()) {
                                                mailEntriesRef.document(USER).collection("entries").document(oldAccBox.getName()).get().addOnCompleteListener(task33->{
                                                   if(task33.isSuccessful()){
                                                       if(task33.getResult().getLong("count")!=null) {
                                                           if (task33.getResult().getLong("count") <= 0) {
                                                               mailEntriesRef.document(USER).collection("entries").document(oldAccBox.getName()).delete()
                                                                       .addOnSuccessListener(aVoid1 -> Log.d("MAIL::STATUS", "DocumentSnapshot successfully deleted!"))
                                                                       .addOnFailureListener(e12 -> Log.w("MAIL::STATUS", "Error deleting document", e12));
                                                           }
                                                       }
                                                   }
                                                });
                                                mailEntriesRef.document(USER).collection("entries").document(mailBean.getPersonName()).set(mailBean).addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Log.i("Status:", "db mail list entry is successful");
                                                    } else {
                                                        Log.i("Status:", "db mail list entry is not successful");
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                        });
                        break;
                    case REMOVED:
                        for (AccountBox ac : accountEntryList) {
                            if (ac.getId().equals(dc.getDocument().getId())) {
                                AccEntriesMap.delete(ac.getId(), accountEntryList.indexOf(ac));
                                accountEntryList.remove(ac);
                                mailEntriesRef.document(USER).collection("entries").document(ac.getName()).update("count",FieldValue.increment(-1)).addOnCompleteListener(task -> {
                                    if(task.isComplete()) {
                                        mailEntriesRef.document(USER).collection("entries").document(ac.getName()).get().addOnCompleteListener(task3->{
                                            if(task3.isSuccessful()) {
                                                Long cnt = task3.getResult().getLong("count");
                                                if (cnt != null) {
                                                    if (cnt <= 0) {
                                                        mailEntriesRef.document(USER).collection("entries").document(ac.getName()).delete()
                                                                .addOnSuccessListener(aVoid1 -> Log.d("MAIL::STATUS", "DocumentSnapshot successfully deleted!"))
                                                                .addOnFailureListener(e12 -> Log.w("MAIL::STATUS", "Error deleting document", e12));
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                break;
                            }
                        }
                        break;
                }
            }
            adapter.notifyDataSetChanged();
            if (snapshots.size() != 0)
                lastVisible = snapshots.getDocuments().get(snapshots.size() - 1);
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

                    accountEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                String key = null;
                                AccountBoxDao accountBoxDao = null;

                                switch (dc.getType()) {
                                    case ADDED:
                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                        if (accountEntryList.size() > 0 && accountEntryList.get(0).getTimestamp().compareTo(accountBoxDao.getTimestamp().toDate()) < 0) {
                                            accountEntryList.add(0, new AccountBox(accountBoxDao, key));
                                            AccEntriesMap.addFirst(key);
                                        } else {
                                            accountEntryList.add(new AccountBox(accountBoxDao, key));
                                            AccEntriesIndex.put(key, accountEntryList.size() - 1);
                                        }
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                        int index = AccEntriesIndex.get(key);
                                        accountEntryList.set(index, new AccountBox(accountBoxDao, key));
                                        break;

                                    case REMOVED:
                                        for (AccountBox ac : accountEntryList) {
                                            if (ac.getId().equals(dc.getDocument().getId())) {
                                                AccEntriesMap.delete(ac.getId(), accountEntryList.indexOf(ac));
                                                accountEntryList.remove(ac);
                                                break;
                                            }
                                        }
                                        break;
                                }
                            }
                            adapter.notifyDataSetChanged();

                            if (snapshots.size() != 0) {
                                lastVisible = snapshots.getDocuments().get(snapshots.size() - 1);
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
        liveAccountEntries.remove();
    }
}
