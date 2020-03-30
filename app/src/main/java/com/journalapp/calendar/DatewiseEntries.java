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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.journalapp.DrawerLayoutActivity2;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.RecyclerViewAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class DatewiseEntries extends Fragment implements CalendarFragment.JDatePickerSelectionListener {

    private RecyclerView recyclerView;
    private ArrayList<Feedbox> feedboxesList= new ArrayList<>();
    private DatabaseReference entriesDb,byDateDb;
    private RecyclerViewAdapter recyclerViewAdapter;

    private String USER = "Kiran1901";
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private String selectedDate = dateFormat.format(Calendar.getInstance().getTime());

    CollectionReference journalEntriesRef = FirebaseFirestore.getInstance().collection("journal_entries");
    CollectionReference byDateEntriesRef = FirebaseFirestore.getInstance().collection("by_date");

    public DatewiseEntries() {
    }

    @Override
    public void onDatePickerSelection(String date) {
        selectedDate = date;
        feedboxesList.clear();
        recyclerViewAdapter.notifyDataSetChanged();

        byDateEntriesRef.document(USER).collection(selectedDate).document("journal_entries").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                feedboxesList.clear();
                List<String> arr = ((List<String>) documentSnapshot.get("array"));
                if (arr != null && arr.size()>0) {
                    for (final String entry : arr) {
                        journalEntriesRef.document(USER).collection("entries").document(entry).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    FeedboxDao feedboxDao = task.getResult().toObject(FeedboxDao.class);
                                    feedboxesList.add(new Feedbox(feedboxDao, entry));
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        journalEntriesRef.document(USER).collection("entries").document(entry).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (documentSnapshot.exists()){
                                    FeedboxDao feedboxDao = documentSnapshot.toObject(FeedboxDao.class);
                                    for(int i=0;i<feedboxesList.size();i++){
                                        if(feedboxesList.get(i).getId().equals(entry)){
                                            feedboxesList.set(i, new Feedbox(feedboxDao,entry));
                                        }
                                    }
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(getContext(), "entry deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    Toast.makeText(getContext(), "No entries", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Toast.makeText(getContext(),"date selected:"+selectedDate,Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DrawerLayoutActivity2.calendarFragment.jdatePickerSelectionListener =this;
        final View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);
        entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child(USER);
        byDateDb = FirebaseDatabase.getInstance().getReference("by_date").child(USER);

        feedboxesList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), feedboxesList);


        byDateEntriesRef.document(USER).collection(selectedDate).document("journal_entries").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                feedboxesList.clear();
                List<String> arr = ((List<String>) documentSnapshot.get("array"));
                if (arr != null && arr.size()>0) {
                    for (final String entry : arr) {
                        journalEntriesRef.document(USER).collection("entries").document(entry).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    FeedboxDao feedboxDao = task.getResult().toObject(FeedboxDao.class);
                                    feedboxesList.add(new Feedbox(feedboxDao, entry));
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        journalEntriesRef.document(USER).collection("entries").document(entry).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (documentSnapshot.exists()){
                                    FeedboxDao feedboxDao = documentSnapshot.toObject(FeedboxDao.class);
                                    for(int i=0;i<feedboxesList.size();i++){
                                        if(feedboxesList.get(i).getId().equals(entry)){
                                            feedboxesList.set(i, new Feedbox(feedboxDao,entry));
                                        }
                                    }
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(getContext(), "entry deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    Toast.makeText(getContext(), "No entries", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(recyclerViewAdapter);


        return entriesView;
    }
}
