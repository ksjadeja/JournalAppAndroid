package com.journalapp.calendar;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.journalapp.DrawerLayoutActivity2;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.utils.AccountRecyclerViewAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class DatewiseAccEntries extends Fragment implements CalendarFragment.ADatePickerSelectionListener {
    private RecyclerView recyclerView;
    private ArrayList<AccountBox> accountBoxList= new ArrayList<>();;
//    private DatabaseReference accountDb,byDateDb;
    private AccountRecyclerViewAdapter accountRecyclerViewAdapter;
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
    CollectionReference byDateEntriesRef = FirebaseFirestore.getInstance().collection("by_date");
    private String USER = "Kiran1901";
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private String selectedDate = dateFormat.format(Calendar.getInstance().getTime());;
    public DatewiseAccEntries(){}

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View accountView =  inflater.inflate(R.layout.fragment_home_acc_entries, container, false);
        DrawerLayoutActivity2.calendarFragment.adatePickerSelectionListener =this;
        recyclerView=accountView.findViewById(R.id.acc_recycler_view);
//        accountDb = FirebaseDatabase.getInstance().getReference("account_entries").child(user);
//        byDateDb = FirebaseDatabase.getInstance().getReference("by_date").child(user);

        accountBoxList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        accountRecyclerViewAdapter = new AccountRecyclerViewAdapter(getContext(), accountBoxList);
        // database code
        byDateEntriesRef.document(USER).collection(selectedDate).document("account_entries").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                accountBoxList.clear();
                List<String> accountEntryKeys = new ArrayList<>();
                accountEntryKeys = (ArrayList<String>)documentSnapshot.get("array");
                if(accountEntryKeys!=null && accountEntryKeys.size()>0)
                {
                    for(final String accKey:accountEntryKeys)
                    {
                        accountEntriesRef.document(USER).collection("entries").document(accKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    AccountBoxDao accountBoxDao = task.getResult().toObject(AccountBoxDao.class);
                                    accountBoxList.add(new AccountBox(accountBoxDao,accKey));
                                    accountRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        accountEntriesRef.document(USER).collection("entries").document(accKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if(documentSnapshot.exists())
                                {
                                    AccountBoxDao accountBoxDao = documentSnapshot.toObject(AccountBoxDao.class);
                                    for(int i=0;i<accountBoxList.size();i++){
                                        if(accountBoxList.get(i).getId().equals(accKey)){
                                            accountBoxList.set(i, new AccountBox(accountBoxDao,accKey));

                                        }
                                    }
                                    accountRecyclerViewAdapter.notifyDataSetChanged();
                                }else{
//                                    Toast.makeText(getContext(), "entry deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else{
//                    Toast.makeText(getContext(), "No entries", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        byDateDb.child(selectedDate).child("account_entries").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                final String key, newKey;
//                key = dataSnapshot.getKey();
//                newKey = dataSnapshot.getValue(String.class);
//
//                accountDb.child(newKey).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        AccountBoxDao accountBoxDao;
//                        accountBoxDao = dataSnapshot.getValue(AccountBoxDao.class);
//                        for (int i=0;i<accountBoxList.size();i++){
//                            if(accountBoxList.get(i).getId().equals(dataSnapshot.getKey())){
//                                accountBoxList.set(i, new AccountBox(accountBoxDao, dataSnapshot.getKey()));
//                                accountRecyclerViewAdapter.notifyDataSetChanged();
//                                return;
//                            }
//                        }
//                        accountBoxList.add(0, new AccountBox(accountBoxDao, dataSnapshot.getKey()));
//                        accountRecyclerViewAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                // Not happening
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                for(AccountBox ab:accountBoxList){
//                    if(ab.getId().equals(dataSnapshot.getKey())){
////                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
//                        accountBoxList.remove(ab);
//                        accountRecyclerViewAdapter.notifyDataSetChanged();
//                        return;
//                    }
//                }
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(),"Firebase Error: "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });

        recyclerView.setAdapter(accountRecyclerViewAdapter);


        return accountView;
    }

    @Override
    public void onDatePickerSelection(String date) {
        selectedDate=date;
        accountBoxList.clear();
        accountRecyclerViewAdapter.notifyDataSetChanged();

        byDateEntriesRef.document(USER).collection(selectedDate).document("account_entries").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                accountBoxList.clear();
                List<String> accountEntryKeys;
                accountEntryKeys = (ArrayList<String>)documentSnapshot.get("array");
                if(accountEntryKeys!=null && accountEntryKeys.size()>0)
                {
                    for(final String accKey:accountEntryKeys)
                    {
                        accountEntriesRef.document(USER).collection("entries").document(accKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    AccountBoxDao accountBoxDao = task.getResult().toObject(AccountBoxDao.class);
                                    accountBoxList.add(new AccountBox(accountBoxDao,accKey));
                                    accountRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        accountEntriesRef.document(USER).collection("entries").document(accKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if(documentSnapshot.exists())
                                {
                                    AccountBoxDao accountBoxDao = documentSnapshot.toObject(AccountBoxDao.class);
                                    for(int i=0;i<accountBoxList.size();i++){
                                        if(accountBoxList.get(i).getId().equals(accKey)){
                                                accountBoxList.set(i, new AccountBox(accountBoxDao,accKey));
                                        }
                                    }
                                    accountRecyclerViewAdapter.notifyDataSetChanged();
                                }else{
//                                    Toast.makeText(getContext(), "acc entry deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else{
//                    Toast.makeText(getContext(), "No acc entries", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
