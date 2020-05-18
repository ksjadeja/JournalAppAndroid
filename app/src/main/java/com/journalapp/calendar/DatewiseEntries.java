package com.journalapp.calendar;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.DrawerLayoutActivity;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.RecyclerViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class DatewiseEntries extends Fragment implements CalendarFragment.JDatePickerSelectionListener {

    private RecyclerView recyclerView;
    private ArrayList<Feedbox> feedboxesList= new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;

    private String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private String selectedDate = dateFormat.format(Calendar.getInstance().getTime());

    CollectionReference journalEntriesRef = FirebaseFirestore.getInstance().collection("journal_entries");

    public DatewiseEntries() {}

    @Override
    public void onDatePickerSelection(String date) {
        selectedDate = date;
        feedboxesList.clear();
        recyclerViewAdapter.notifyDataSetChanged();

        Calendar cal = Calendar.getInstance();

        try {
            Date start = new SimpleDateFormat("dd-MM-yyyy").parse(selectedDate);
            cal.setTime(start);
            cal.add(Calendar.DATE,1);
            Date end = cal.getTime();

            Log.i("MESSAGE(Others)  ==>","start:"+start+"  end:"+end);

            journalEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp",start).whereLessThan("timestamp",end).orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        Log.i("ERROR:", "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        String key=null;
                        FeedboxDao feedboxDao = null;

                        switch (dc.getType()) {
                            case ADDED:
                                key = dc.getDocument().getId();
                                feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
                                feedboxesList.add(0,new Feedbox(feedboxDao,key));
                                recyclerViewAdapter.notifyDataSetChanged();
                                break;

                            case MODIFIED:
                                key = dc.getDocument().getId();
                                feedboxDao = dc.getDocument().toObject(FeedboxDao.class);
                                for(Feedbox fb:feedboxesList) {
                                    if (fb.getId().equals(key)) {
                                        feedboxesList.set(feedboxesList.indexOf(fb),new Feedbox(feedboxDao, key));
                                        recyclerViewAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                for(Feedbox fb:feedboxesList){
                                    if(fb.getId().equals(dc.getDocument().getId())){
                                        feedboxesList.remove(fb);
                                        recyclerViewAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                break;
                        }
                    }

                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DrawerLayoutActivity.calendarFragment.jdatePickerSelectionListener = this;
        final View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);
        feedboxesList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), feedboxesList);
        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        start.setHours(0);
        start.setMinutes(0);
        start.setSeconds(0);
        cal.setTime(start);
        String startDate = dateFormat.format(start);
        onDatePickerSelection(startDate);
        recyclerView.setAdapter(recyclerViewAdapter);
        return entriesView;
    }
}