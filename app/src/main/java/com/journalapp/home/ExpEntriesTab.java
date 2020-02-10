package com.journalapp.home;

import android.os.Bundle;
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
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;
import com.journalapp.utils.AccountRecyclerViewAdapter;
import com.journalapp.utils.ExpenseRecyclerViewAdapter;
import com.journalapp.utils.ExpenseRecyclerViewAdapterView;

import java.util.ArrayList;

public class ExpEntriesTab extends Fragment {
    RecyclerView recyclerView;
    ArrayList<ExpenseBox> expenseEntryList;
    DatabaseReference expenseEntriesDb;

    ExpenseRecyclerViewAdapterView adapter;
    public ExpEntriesTab (){
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View expenseView = inflater.inflate(R.layout.fragment_home_expense_entries, container, false);
//        recyclerView=expenseView.findViewById(R.id.acc_recycler_view);
//        expenseEntriesDb = FirebaseDatabase.getInstance().getReference("expense_entries").child("Kiran1901");
//
//        expenseEntryList= new ArrayList<>();
//
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new ExpenseRecyclerViewAdapterView(getContext(), expenseEntryList);
//
//        expenseEntriesDb.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String key;
//                ExpenseBoxDao expenseBoxDao;
//                key = dataSnapshot.getKey();
//                expenseBoxDao = dataSnapshot.getValue(ExpenseBoxDao.class);
//
//                expenseEntryList.add(0,new ExpenseBox(expenseBoxDao,key));
////                EntriesMap.addFirst(key);
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
////                int index = EntriesIndex.get(key);
////                accountEntryList.set(index,new AccountBox(accountEntryBoxDao,key));
////                adapter.notifyDataSetChanged();
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
////                for(Feedbox fb:feedboxesList){
////                    if(fb.getId().equals(dataSnapshot.getKey())){
////                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
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
//
//        recyclerView.setAdapter(adapter);
        return expenseView;
    }
}
