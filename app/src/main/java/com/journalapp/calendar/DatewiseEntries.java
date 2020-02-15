package com.journalapp.calendar;

import android.os.Build;
import android.os.Bundle;
import android.view.DragAndDropPermissions;
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
import com.journalapp.DrawerLayoutActivity2;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.RecyclerViewAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DatewiseEntries extends Fragment implements CalendarFragment.DatePickerSelectionListener {

    private RecyclerView recyclerView;
    private ArrayList<Feedbox> feedboxesList;
    private DatabaseReference entriesDb,byDateDb;
    private RecyclerViewAdapter recyclerViewAdapter;

    private String user = "Kiran1901";
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private String selectedDate = dateFormat.format(Calendar.getInstance().getTime());


    public DatewiseEntries() {
    }

    @Override
    public void onDatePickerSelection(String date) {
        selectedDate = date;
        feedboxesList.clear();
        byDateDb.child(selectedDate).child("journal_entries").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key, newKey;
                key = dataSnapshot.getKey();
                newKey = dataSnapshot.getValue(String.class);

                entriesDb.child(newKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FeedboxDao feedboxDao;
                        feedboxDao = dataSnapshot.getValue(FeedboxDao.class);
                        for (int i=0;i<feedboxesList.size();i++){
                            if(feedboxesList.get(i).getId().equals(dataSnapshot.getKey())){
                                feedboxesList.set(i, new Feedbox(feedboxDao, dataSnapshot.getKey()));
                                recyclerViewAdapter.notifyDataSetChanged();
                                return;
                            }
                        }
                        feedboxesList.add(0, new Feedbox(feedboxDao, dataSnapshot.getKey()));
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

                for(Feedbox fb:feedboxesList){
                    if(fb.getId().equals(dataSnapshot.getKey())){
//                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
                        feedboxesList.remove(fb);
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
        Toast.makeText(getContext(),"date selected:"+selectedDate,Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DrawerLayoutActivity2.calendarFragment.datePickerSelectionListener=this;

        final View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);
        entriesDb = FirebaseDatabase.getInstance().getReference("journal_entries").child(user);
        byDateDb = FirebaseDatabase.getInstance().getReference("by_date").child(user);

        feedboxesList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), feedboxesList);
        byDateDb.child(selectedDate).child("journal_entries").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key, newKey;
                key = dataSnapshot.getKey();
                newKey = dataSnapshot.getValue(String.class);

                entriesDb.child(newKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FeedboxDao feedboxDao;
                        feedboxDao = dataSnapshot.getValue(FeedboxDao.class);
                        for (int i=0;i<feedboxesList.size();i++){
                            if(feedboxesList.get(i).getId().equals(dataSnapshot.getKey())){
                                feedboxesList.set(i, new Feedbox(feedboxDao, dataSnapshot.getKey()));
                                recyclerViewAdapter.notifyDataSetChanged();
                                return;
                            }
                        }
                        feedboxesList.add(0, new Feedbox(feedboxDao, dataSnapshot.getKey()));
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

                for(Feedbox fb:feedboxesList){
                    if(fb.getId().equals(dataSnapshot.getKey())){
//                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
                        feedboxesList.remove(fb);
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


        return entriesView;
    }
}
