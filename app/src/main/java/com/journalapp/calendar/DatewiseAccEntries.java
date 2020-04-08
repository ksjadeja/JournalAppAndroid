package com.journalapp.calendar;

import android.content.Context;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.DrawerLayoutActivity2;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.utils.AccountRecyclerViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class DatewiseAccEntries extends Fragment implements CalendarFragment.ADatePickerSelectionListener {

    private RecyclerView recyclerView;
    private ArrayList<AccountBox> accountBoxList= new ArrayList<>();;
    private AccountRecyclerViewAdapter accountRecyclerViewAdapter;

    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");

    private String USER = "Kiran1901";
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private String selectedDate = dateFormat.format(Calendar.getInstance().getTime());;
    Context context;
    public DatewiseAccEntries(){}

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getContext();
        final View accountView =  inflater.inflate(R.layout.fragment_home_acc_entries, container, false);
        DrawerLayoutActivity2.calendarFragment.adatePickerSelectionListener =this;
        recyclerView=accountView.findViewById(R.id.acc_recycler_view);
        accountBoxList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        accountRecyclerViewAdapter = new AccountRecyclerViewAdapter(getContext(), accountBoxList);

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        start.setHours(0);
        start.setMinutes(0);
        start.setSeconds(0);
        cal.setTime(start);

        String startDate = dateFormat.format(start);
        onDatePickerSelection(startDate);
        recyclerView.setAdapter(accountRecyclerViewAdapter);
        return accountView;
    }

    @Override
    public void onDatePickerSelection(String date) {
        selectedDate=date;
        accountBoxList.clear();
        accountRecyclerViewAdapter.notifyDataSetChanged();

        Calendar calendar = Calendar.getInstance();
        try{
            Date start = new SimpleDateFormat("dd-MM-yyyy").parse(selectedDate);
            calendar.setTime(start);
            calendar.add(Calendar.DATE,1);
            Date end = calendar.getTime();
            accountEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp",start).whereLessThan("timestamp",end).orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.i("ERROR:", "listen:error", e);
                        return;
                    }
                    for(DocumentChange dc:queryDocumentSnapshots.getDocumentChanges())
                    {
                        String key=null;
                        AccountBoxDao accountBoxDao=null;

                        switch (dc.getType())
                        {
                            case ADDED:
                                key = dc.getDocument().getId();
                                accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                accountBoxList.add(0,new AccountBox(accountBoxDao,key));
                                accountRecyclerViewAdapter.notifyItemInserted(0);
                                break;
                            case MODIFIED:
                                key = dc.getDocument().getId();
                                accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                for(AccountBox ac: accountBoxList)
                                {
                                    if(ac.getId().equals(key))
                                    {
                                        accountBoxList.set(accountBoxList.indexOf(ac),new AccountBox(accountBoxDao,key));
                                        accountRecyclerViewAdapter.notifyItemChanged(accountBoxList.indexOf(ac));
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                for(AccountBox ac : accountBoxList){
                                    if(ac.getId().equals(dc.getDocument().getId())){
                                        accountBoxList.remove(ac);
                                        accountRecyclerViewAdapter.notifyItemRemoved(accountBoxList.indexOf(ac));
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                }
            });
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
