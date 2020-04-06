package com.journalapp.home;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.AccEntriesMap;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.AccountRecyclerViewAdapter;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.journalapp.AccEntriesMap.AccEntriesIndex;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccEntriesTab extends Fragment {

    RecyclerView recyclerView;
    ArrayList<AccountBox> accountEntryList;
    String USER= "Kiran1901";
//    DatabaseReference accountEntriesDb;
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
    AccountRecyclerViewAdapter adapter;
    ListenerRegistration liveAccountEntries;
    public AccEntriesTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_home_acc_entries, container, false);
        recyclerView=rootView.findViewById(R.id.acc_recycler_view);
        accountEntryList= new ArrayList<>();


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AccountRecyclerViewAdapter(getContext(), accountEntryList);
        liveAccountEntries = accountEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    AccountBoxDao accountBoxDao= null;
                    switch (dc.getType()) {
                        case ADDED:
                            key = dc.getDocument().getId();
                            Log.i("CntA:",(i++)+":::"+key);
//                            Log.e("Type",""+dc.getType());
                            accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                            accountEntryList.add(0,new AccountBox(accountBoxDao,key));
                            AccEntriesMap.addFirst(key);
                            adapter.notifyDataSetChanged();
//                            SharedPreferences sharedPreferences = rootView.getContext().getSharedPreferences("AccountData",MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("currentKey",key);
//                            editor.commit();
                            break;

                        case MODIFIED:
                            key = dc.getDocument().getId();
                            accountBoxDao= dc.getDocument().toObject(AccountBoxDao.class);
                            int index = AccEntriesIndex.get(key);
                            accountEntryList.set(index,new AccountBox(accountBoxDao,key));
                            adapter.notifyDataSetChanged();
                            break;

                        case REMOVED:
                            for(AccountBox ac:accountEntryList){            //TODO optimize it futher
                                if(ac.getId().equals(dc.getDocument().getId())){
                                    AccEntriesMap.delete(ac.getId(),accountEntryList.indexOf(ac));
                                    accountEntryList.remove(ac);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            break;
                    }
                }
            }
        });

//        accountEntriesDb.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String key;
//                AccountBoxDao accountBoxDao;
//                key = dataSnapshot.getKey();
//                accountBoxDao = dataSnapshot.getValue(AccountBoxDao.class);
//
//                accountEntryList.add(0,new AccountBox(accountBoxDao,key));
////                AccEntriesMap.addFirst(key);
//                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                String key;
////                AccountBoxDao accountEntryBoxDao;
////                key = dataSnapshot.getKey();
////                accountEntryBoxDao= dataSnapshot.getValue(AccountBoxDao.class);
////
////                int index = AccEntriesIndex.get(key);
////                accountEntryList.set(index,new AccountBox(accountEntryBoxDao,key));
////                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
////                int index = AccEntriesIndex.get(dataSnapshot.getKey());
////                entries.remove(index);
////                AccEntriesMap.delete(dataSnapshot.getKey(),index);
////                notifyDataSetChanged();
//
////                for(Feedbox fb:feedboxesList){
////                    if(fb.getId().equals(dataSnapshot.getKey())){
////                        AccEntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
////                        feedboxesList.remove(fb);
////                        adapter.notifyDataSetChanged();
////                        return;
////                    }
////                }
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

        return rootView;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        liveAccountEntries.remove();
    }
}
