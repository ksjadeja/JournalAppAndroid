package com.journalapp.calculate;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.ExpEntriesMap;
import com.journalapp.R;
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;
import com.journalapp.utils.CalculateRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalculateFragment extends Fragment implements View.OnClickListener {

    private TextInputEditText avg_expense_start, avg_expense_end;
    private MaterialTextView avg_expense_total, avg_expense_average;
    private MaterialButton avg_expense_submit;

    private RecyclerView avg_expense_recycler_view;

    private ArrayList<ExpenseBox> expense_list;

    private LinearLayout average_n_total_bar;

    private Calendar startAvgExp, endAvgExp;
    private int dayy, monthh, yearr;

    private float average, total, n;

    private CalculateRecyclerViewAdapter adapter;

    private CollectionReference expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");

    String USER = "Kiran1901";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        expense_list = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_calculate, container, false);

        avg_expense_start = view.findViewById(R.id.avg_expense_start);
        avg_expense_end = view.findViewById(R.id.avg_expense_end);
        avg_expense_submit = view.findViewById(R.id.avg_expense_submit);
        average_n_total_bar = view.findViewById(R.id.average_n_total_bar);

        avg_expense_recycler_view = view.findViewById(R.id.avg_expense_recycler_view);

        avg_expense_total = view.findViewById(R.id.avg_expense_total);
        avg_expense_average = view.findViewById(R.id.avg_expense_average);


        average_n_total_bar.setVisibility(View.INVISIBLE);

        avg_expense_total.setTextColor(Color.RED);
        avg_expense_average.setTextColor(Color.RED);

        avg_expense_start.setOnClickListener(this);
        avg_expense_end.setOnClickListener(this);
        avg_expense_submit.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        dayy = calendar.get(Calendar.DAY_OF_MONTH);
        monthh = calendar.get(Calendar.MONTH);
        yearr = calendar.get(Calendar.YEAR);


        avg_expense_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CalculateRecyclerViewAdapter(getContext(), expense_list);
        avg_expense_recycler_view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avg_expense_start:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 0, 0, 0);
                        startAvgExp = calendar;
                        avg_expense_start.setText(new SimpleDateFormat("dd/MM/YYYY").format(startAvgExp.getTime()));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog.show();
                break;
            case R.id.avg_expense_end:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 23, 59, 59);
                        endAvgExp = calendar;
                        avg_expense_end.setText(new SimpleDateFormat("dd/MM/YYYY").format(endAvgExp.getTime()));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog2.show();
                break;


            case R.id.avg_expense_submit:
                if (startAvgExp != null && endAvgExp != null) {
                    expense_list.clear();
                    total = 0;
                    average = 0;
                    n= daysBetween(startAvgExp, endAvgExp);
//                    n = Math.abs(startAvgExp.getTimeInMillis()-endAvgExp.getTimeInMillis());
//                    n = n/(1000 * 60 * 60 * 24);
                    expenseEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startAvgExp.getTime()).whereLessThan("timestamp", endAvgExp.getTime()).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                String key = null;
                                ExpenseBoxDao expenseBoxDao = null;
                                switch (dc.getType()) {
                                    case ADDED:
                                        System.out.println("in the add::::");

                                        key = dc.getDocument().getId();
                                        expenseBoxDao = dc.getDocument().toObject(ExpenseBoxDao.class);
                                        expense_list.add(0, new ExpenseBox(expenseBoxDao, key));
                                        adapter.notifyDataSetChanged();
                                        total += expenseBoxDao.getAmount();
                                        average = total / n ;

                                        avg_expense_total.setText(String.valueOf(total));
                                        avg_expense_average.setText(String.valueOf(average));
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        expenseBoxDao = dc.getDocument().toObject(ExpenseBoxDao.class);
                                        for (ExpenseBox ex : expense_list) {
                                            if (ex.getId().equals(key)) {
                                                expense_list.set(expense_list.indexOf(ex), new ExpenseBox(expenseBoxDao, key));
                                                adapter.notifyDataSetChanged();
                                                break;
                                            }

                                        }
                                        total = 0;
                                        for(ExpenseBox exp : expense_list){
                                            total += exp.getAmount();
                                        }
                                        average = total / n;
                                        avg_expense_total.setText(String.valueOf(total));
                                        avg_expense_average.setText(String.valueOf(average));
                                        break;

                                    case REMOVED:
                                        for (ExpenseBox ex : expense_list) {
                                            if (ex.getId().equals(dc.getDocument().getId())) {
                                                ExpEntriesMap.delete(ex.getId(), expense_list.indexOf(ex));
                                                expense_list.remove(ex);
                                                adapter.notifyDataSetChanged();
                                                break;
                                            }
                                            total -= expenseBoxDao.getAmount();
                                            average = total / n ;

                                            avg_expense_total.setText(String.valueOf(total));
                                            avg_expense_average.setText(String.valueOf(average));
                                        }
                                        break;
                                }
                            }
                        }
                    });

                    average_n_total_bar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public static int daysBetween(Calendar day1, Calendar day2){
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();
        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;
            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }
            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

}
