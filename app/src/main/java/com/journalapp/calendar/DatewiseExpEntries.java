package com.journalapp.calendar;

import android.content.Context;
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
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;
import com.journalapp.utils.AccountRecyclerViewAdapter;
import com.journalapp.utils.ExpenseRecyclerViewAdapterView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class DatewiseExpEntries extends Fragment implements CalendarFragment.EDatePickerSelectionListener {

    private RecyclerView recyclerView;
    private ArrayList<ExpenseBox> expenseBoxList;
    private ExpenseRecyclerViewAdapterView expenseRecyclerViewAdapter;
    CollectionReference expenseEntriesRef;
    CollectionReference byDateEntriesRef;
    private String USER = "Kiran1901";
    DateFormat dateFormat;
    private String selectedDate;
    Context context;
    public DatewiseExpEntries(){}

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        expenseBoxList = new ArrayList<>();
        dateFormat= new SimpleDateFormat("dd-MM-yyyy");
        selectedDate= dateFormat.format(Calendar.getInstance().getTime());
         byDateEntriesRef = FirebaseFirestore.getInstance().collection("by_date");
        expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");

        DrawerLayoutActivity2.calendarFragment.edatePickerSelectionListener=this;
        final View expenseView =  inflater.inflate(R.layout.fragment_home_expense_entries, container, false);
        recyclerView=expenseView.findViewById(R.id.exp_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseRecyclerViewAdapter = new ExpenseRecyclerViewAdapterView(getContext(), expenseBoxList);


        byDateEntriesRef.document(USER).collection(selectedDate).document("expense_entries").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                expenseBoxList.clear();
                List<String> expenseEntryKeys = (ArrayList<String>)documentSnapshot.get("array");
                if(expenseEntryKeys!=null && expenseEntryKeys.size()>0)
                {
                    for(final String expKey:expenseEntryKeys)
                    {
                        expenseEntriesRef.document(USER).collection("entries").document(expKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    ExpenseBoxDao expenseBoxDao = task.getResult().toObject(ExpenseBoxDao.class);
                                    expenseBoxList.add(new ExpenseBox(expenseBoxDao,expKey));
                                    expenseRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        expenseEntriesRef.document(USER).collection("entries").document(expKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if(documentSnapshot.exists())
                                {
                                    ExpenseBoxDao expenseBoxDao= documentSnapshot.toObject(ExpenseBoxDao.class);
                                    for(int i=0;i<expenseBoxList.size();i++){
                                        if(expenseBoxList.get(i).getId().equals(expKey)){
                                            expenseBoxList.set(i, new ExpenseBox(expenseBoxDao,expKey));
                                        }
                                    }
                                    expenseRecyclerViewAdapter.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(context, "entry deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(context, "No entries exp", Toast.LENGTH_SHORT).show();
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
        recyclerView.setAdapter(expenseRecyclerViewAdapter);
        return expenseView;
    }


    @Override
    public void onDatePickerSelection(String date) {
        selectedDate=date;
        expenseBoxList.clear();
        expenseRecyclerViewAdapter.notifyDataSetChanged();

        byDateEntriesRef.document(USER).collection(selectedDate).document("expense_entries").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                expenseBoxList.clear();
                List<String> expenseEntryKeys = ((List<String>)documentSnapshot.get("array"));
                if(expenseEntryKeys!=null && expenseEntryKeys.size()>0)
                {
                    for(final String expKey:expenseEntryKeys)
                    {
                        expenseEntriesRef.document(USER).collection("entries").document(expKey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    ExpenseBoxDao expenseBoxDao = task.getResult().toObject(ExpenseBoxDao.class);
                                    expenseBoxList.add(new ExpenseBox(expenseBoxDao,expKey));
                                    expenseRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        expenseEntriesRef.document(USER).collection("entries").document(expKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if(documentSnapshot.exists())
                                {
                                    ExpenseBoxDao expenseBoxDao= documentSnapshot.toObject(ExpenseBoxDao.class);
                                    for(int i=0;i<expenseBoxList.size();i++){
                                        if(expenseBoxList.get(i).getId().equals(expKey)){
                                            expenseBoxList.set(i, new ExpenseBox(expenseBoxDao,expKey));
                                        }
                                    }
                                    expenseRecyclerViewAdapter.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(context, "entry deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(context, "No entries exp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
