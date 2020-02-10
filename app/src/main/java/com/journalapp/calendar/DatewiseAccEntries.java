package com.journalapp.calendar;

import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.AccountRecyclerViewAdapter;
import com.journalapp.utils.RecyclerViewAdapter;

import java.util.ArrayList;

public class DatewiseAccEntries extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<AccountBox> accountBoxList;
    private DatabaseReference accountDb,byDateDb;
    private AccountRecyclerViewAdapter recyclerViewAdapter;

    private String user = "Kiran1901";
    private String selectedDate = "11-02-2020";
    public DatewiseAccEntries(){}

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View accountView =  inflater.inflate(R.layout.fragment_home_acc_entries, container, false);
        recyclerView=accountView.findViewById(R.id.acc_recycler_view);
        accountDb = FirebaseDatabase.getInstance().getReference("account_entries").child(user);
        byDateDb = FirebaseDatabase.getInstance().getReference("by_date").child(user);

        accountBoxList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new AccountRecyclerViewAdapter(getContext(), accountBoxList);

        byDateDb.child(selectedDate).child("account_entries").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key, newKey;
                key = dataSnapshot.getKey();
                newKey = dataSnapshot.getValue(String.class);

                accountDb.child(newKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AccountBoxDao accountBoxDao;
                        accountBoxDao = dataSnapshot.getValue(AccountBoxDao.class);
                        for (int i=0;i<accountBoxList.size();i++){
                            if(accountBoxList.get(i).getId().equals(dataSnapshot.getKey())){
                                accountBoxList.set(i, new AccountBox(accountBoxDao, dataSnapshot.getKey()));
                                recyclerViewAdapter.notifyDataSetChanged();
                                return;
                            }
                        }
                        accountBoxList.add(0, new AccountBox(accountBoxDao, dataSnapshot.getKey()));
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Not happening
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for(AccountBox ab:accountBoxList){
                    if(ab.getId().equals(dataSnapshot.getKey())){
//                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
                        accountBoxList.remove(ab);
                        recyclerViewAdapter.notifyDataSetChanged();
                        return;
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Firebase Error: "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.setAdapter(recyclerViewAdapter);


        return accountView;
    }
}
