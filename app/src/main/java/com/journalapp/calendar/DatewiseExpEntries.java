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
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;
import com.journalapp.utils.ExpenseRecyclerViewAdapterView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class DatewiseExpEntries extends Fragment implements CalendarFragment.EDatePickerSelectionListener {

    private RecyclerView recyclerView;
    private ArrayList<ExpenseBox> expenseBoxList;
    private ExpenseRecyclerViewAdapterView expenseRecyclerViewAdapter;
    CollectionReference expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");
    private String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";
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

        DrawerLayoutActivity.calendarFragment.edatePickerSelectionListener=this;
        final View expenseView =  inflater.inflate(R.layout.fragment_home_expense_entries, container, false);
        recyclerView=expenseView.findViewById(R.id.exp_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseRecyclerViewAdapter = new ExpenseRecyclerViewAdapterView(getContext(), expenseBoxList);
        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        start.setHours(0);
        start.setMinutes(0);
        start.setSeconds(0);
        cal.setTime(start);

        String startDate = dateFormat.format(start);
        onDatePickerSelection(startDate);

        recyclerView.setAdapter(expenseRecyclerViewAdapter);
        return expenseView;
    }


    @Override
    public void onDatePickerSelection(String date) {
        selectedDate=date;
        expenseBoxList.clear();
        expenseRecyclerViewAdapter.notifyDataSetChanged();
        Calendar calendar = Calendar.getInstance();
        try{
            Date start = new SimpleDateFormat("dd-MM-yyyy").parse(selectedDate);
            calendar.setTime(start);
            calendar.add(Calendar.DATE,1);
            Date end = calendar.getTime();
            expenseEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp",start).whereLessThan("timestamp",end).orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.i("ERROR:", "listen:error", e);
                        return;
                    }
                    for(DocumentChange dc:queryDocumentSnapshots.getDocumentChanges())
                    {
                        String key=null;
                        ExpenseBoxDao expenseBoxDao=null;

                        switch (dc.getType())
                        {
                            case ADDED:
                                key = dc.getDocument().getId();
                                expenseBoxDao = dc.getDocument().toObject(ExpenseBoxDao.class);
                                expenseBoxList.add(0,new ExpenseBox(expenseBoxDao,key));
                                expenseRecyclerViewAdapter.notifyItemInserted(0);
                                break;
                            case MODIFIED:
                                key = dc.getDocument().getId();
                                expenseBoxDao= dc.getDocument().toObject(ExpenseBoxDao.class);
                                for(ExpenseBox exp: expenseBoxList)
                                {
                                    if(exp.getId().equals(key))
                                    {
                                        expenseBoxList.set(expenseBoxList.indexOf(exp),new ExpenseBox(expenseBoxDao,key));
                                        expenseRecyclerViewAdapter.notifyItemChanged(expenseBoxList.indexOf(exp));
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                for(ExpenseBox exp: expenseBoxList){
                                    if(exp.getId().equals(dc.getDocument().getId())){
                                        expenseBoxList.remove(exp);
                                        expenseRecyclerViewAdapter.notifyItemRemoved(expenseBoxList.indexOf(exp));
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
