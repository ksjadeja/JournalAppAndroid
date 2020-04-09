package com.journalapp.charts;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.AccEntriesMap;
import com.journalapp.ExpEntriesMap;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChartsFragment extends Fragment implements View.OnClickListener {

    private BarChart datewiseAccChart,datewiseExpChart;
    EditText startDate, endDate,startDateExp, endDateExp;
    CollectionReference accountEntriesRef;
    CollectionReference expenseEntriesRef;
    Button submit,submitExp;
    Date startAcc, endAcc,startExp,endExp;
    int dayy, monthh, yearr;

    String USER = "Kiran1901";

    ArrayList<AccountBox> accountEntryList;
    ArrayList<ExpenseBox> expenseEntryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        accountEntriesRef= FirebaseFirestore.getInstance().collection("account_entries");
        expenseEntriesRef=FirebaseFirestore.getInstance().collection("expense_entries");

        View rootView = inflater.inflate(R.layout.fragment_charts, container, false);

        datewiseAccChart = rootView.findViewById(R.id.datewiseAccChart);
        startDate = rootView.findViewById(R.id.start_date);
        endDate = rootView.findViewById(R.id.end_date);
        submit = rootView.findViewById(R.id.submit);

        datewiseExpChart = rootView.findViewById(R.id.datewiseExpChart);
        startDateExp = rootView.findViewById(R.id.start_date_exp);
        endDateExp = rootView.findViewById(R.id.end_date_exp);
        submitExp = rootView.findViewById(R.id.submit_exp);


        Calendar calendar = Calendar.getInstance();

        dayy = calendar.get(Calendar.DAY_OF_MONTH);
        monthh = calendar.get(Calendar.MONTH);
        yearr = calendar.get(Calendar.YEAR);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        submit.setOnClickListener(this);

        startDateExp.setOnClickListener(this);
        endDateExp.setOnClickListener(this);
        submitExp.setOnClickListener(this);

        return rootView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year,month,day,0,0,0);
                        startAcc = calendar.getTime();
                        startDate.setText(formatter.format(startAcc));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog.show();
                break;
            case R.id.end_date:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year,month,day,23,59,59);
                        endAcc = calendar.getTime();
//                        Toast.makeText(getActivity(), "year endAcc "+year, Toast.LENGTH_SHORT).show();
                        endDate.setText(formatter.format(endAcc));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog2.show();
                break;


            case R.id.submit:
                if (startAcc != null && endAcc != null) {
                    System.out.println("  startAcc::"+ startAcc +"  endAcc::"+ endAcc +"  today::"+new Date());
                    Toast.makeText(getActivity(),"button clicked",Toast.LENGTH_LONG).show();
                    accountEntryList = new ArrayList<>();
                    accountEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startAcc).whereLessThan("timestamp", endAcc).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(getActivity(), "listener error", Toast.LENGTH_SHORT).show();
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }
//                            Toast.makeText(getActivity(),"inside listener",Toast.LENGTH_LONG).show();
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                Toast.makeText(getActivity(),"inside listener type "+dc.getType(),Toast.LENGTH_LONG).show();
                                String key = null;
                                AccountBoxDao accountBoxDao = null;
                                switch (dc.getType()) {
                                    case ADDED:
                                        System.out.println("in the add::::");

                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                        Toast.makeText(getActivity(), "in add with "+key, Toast.LENGTH_SHORT).show();
                                        accountEntryList.add(0, new AccountBox(accountBoxDao, key));
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                        for(AccountBox ac: accountEntryList)
                                        {
                                            if(ac.getId().equals(key))
                                            {
                                                accountEntryList.set(accountEntryList.indexOf(ac),new AccountBox(accountBoxDao,key));
                                                break;
                                            }
                                        }
                                        break;

                                    case REMOVED:
                                        for(AccountBox ac:accountEntryList){
                                            if(ac.getId().equals(dc.getDocument().getId())){
                                                AccEntriesMap.delete(ac.getId(),accountEntryList.indexOf(ac));
                                                accountEntryList.remove(ac);
                                                break;
                                            }
                                        }
                                        break;
                                }
                            }
                            Toast.makeText(getActivity(), "list  "+accountEntryList, Toast.LENGTH_SHORT).show();
                            drawAccountBarChart(accountEntryList);
                        }
                    });

                    System.out.println("myList::::"+accountEntryList);

                    }
                break;
            case R.id.start_date_exp:
                DatePickerDialog datePickerDialog3 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year,month,day,0,0,0);
                        startExp = calendar.getTime();
                        startDateExp.setText(formatter.format(startExp));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog3.show();
                break;
            case R.id.end_date_exp:
                DatePickerDialog datePickerDialog4 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year,month,day,23,59,59);
                        endExp = calendar.getTime();
//                        Toast.makeText(getActivity(), "year endAcc "+year, Toast.LENGTH_SHORT).show();
                        endDateExp.setText(formatter.format(endExp));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog4.show();
                break;


            case R.id.submit_exp:
                if (startExp != null && endExp!= null) {
                    System.out.println("  start ::"+ startExp+"  endAcc::"+ endExp+"  today::"+new Date());
                    Toast.makeText(getActivity(),"button clicked",Toast.LENGTH_LONG).show();
                    expenseEntryList = new ArrayList<>();
                    expenseEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startExp).whereLessThan("timestamp", endExp).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(getActivity(), "listener error", Toast.LENGTH_SHORT).show();
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }
//                            Toast.makeText(getActivity(),"inside listener",Toast.LENGTH_LONG).show();
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                Toast.makeText(getActivity(),"inside listener type "+dc.getType(),Toast.LENGTH_LONG).show();
                                String key = null;
                                ExpenseBoxDao expenseBoxDao = null;
                                switch (dc.getType()) {
                                    case ADDED:
                                        System.out.println("in the add::::");

                                        key = dc.getDocument().getId();
                                        expenseBoxDao = dc.getDocument().toObject(ExpenseBoxDao.class);
                                        Toast.makeText(getActivity(), "in add with "+key, Toast.LENGTH_SHORT).show();
                                        expenseEntryList.add(0, new ExpenseBox(expenseBoxDao, key));
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        expenseBoxDao  = dc.getDocument().toObject(ExpenseBoxDao.class);
                                        for(ExpenseBox ex: expenseEntryList)
                                        {
                                            if(ex.getId().equals(key))
                                            {
                                                expenseEntryList.set(expenseEntryList.indexOf(ex),new ExpenseBox(expenseBoxDao,key));
                                                break;
                                            }
                                        }
                                        break;

                                    case REMOVED:
                                        for(ExpenseBox ex:expenseEntryList){
                                            if(ex.getId().equals(dc.getDocument().getId())){
                                                ExpEntriesMap.delete(ex.getId(),expenseEntryList.indexOf(ex));
                                                expenseEntryList.remove(ex);
                                                break;
                                            }
                                        }
                                        break;
                                }
                            }
                            Toast.makeText(getActivity(), "list  "+expenseEntryList, Toast.LENGTH_SHORT).show();
                            drawExpenseChart(expenseEntryList);
                        }
                    });

                    System.out.println("myList::::"+expenseEntryList.toString());
//                    ArrayList<BarDataSet> dataSets = null;
//
////                    datewiseAccChart.getXAxis().
//
//                    ArrayList<BarEntry> valueSet1 = new ArrayList<>();
//                    BarEntry v1e2 = new BarEntry(2f, new float[]{2,4,6}); // Feb
//                    valueSet1.add(v1e2);
//                    BarEntry v1e3 = new BarEntry(4f, new float[]{2,3,5,6}); // Mar
//                    valueSet1.add(v1e3);
//                    BarEntry v1e40 = new BarEntry(3f, new float[]{-2,-6,3,6}); // Mar
//                    valueSet1.add(v1e40);
//
//
//                    BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
//                    barDataSet1.setColors(ColorTemplate.MATERIAL_COLORS);
//
//                    barDataSet1.setStackLabels(new String[]{"kr","kh","kd","jn"});
//
//                    dataSets = new ArrayList<>();
//                    dataSets.add(barDataSet1);
//
//
//                    BarData data = new BarData(barDataSet1);
////                            BarData(getXAxisValues(), dataSets);
//
////                    datewiseAccChart.getAxisLeft().setAxisMinimum(0f);
//
//                    datewiseAccChart.setData(data);
//                    datewiseAccChart.getBarData().setBarWidth(.9f);
//                    datewiseAccChart.animateXY(2000, 2000);
////                    datewiseAccChart.groupBars(2f,0.2f,0.02f);
//
//                    datewiseAccChart.setTouchEnabled(true);
//                    datewiseAccChart.setScaleEnabled(true);
//
//                    datewiseAccChart.setFitBars(true);
//
//                    datewiseAccChart.invalidate();
                }
                }
        }

        @Override
        public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState){
            super.onViewCreated(view, savedInstanceState);

            getActivity().setTitle("Slideshow");
        }

        private void drawAccountBarChart(ArrayList<AccountBox> accountEntryList){

            final HashMap<String, ValueAndLabel<Float,String> > map = new HashMap<>();
            ValueAndLabel<Float,String> vl;
            for(AccountBox acc : accountEntryList){
                Toast.makeText(getActivity(), "date "+acc.getDate(), Toast.LENGTH_SHORT).show();
                if(map.containsKey(acc.getDate())){
                    Toast.makeText(getActivity(), "same date ", Toast.LENGTH_SHORT).show();
                    vl=map.get(acc.getDate());
                    vl.values.add(((float) acc.getAmount()));
                    vl.labels.add(acc.getName());
                }else {
                    Toast.makeText(getActivity(), "different date ", Toast.LENGTH_SHORT).show();
                    map.put(acc.getDate(),new ValueAndLabel<>(((float) acc.getAmount()),acc.getName()));
                }
            }

            ArrayList<BarEntry> dataset = new ArrayList<>();
            BarEntry bar = null;
            float[] values;
            float i=0;
            for(Map.Entry<String,ValueAndLabel<Float,String>> mapEntry : map.entrySet()){
                if(mapEntry.getValue().values.size()>1){
                    Toast.makeText(getActivity(), "multiple entry on  "+mapEntry.getKey(), Toast.LENGTH_SHORT).show();
                    values = new float[mapEntry.getValue().values.size()];
                    for (int p=0;p<mapEntry.getValue().values.size();p++){
                        values[p] = mapEntry.getValue().values.get(p);
                    }
                    bar = new BarEntry(i, values, mapEntry.getValue().labels);
                    i+=1;
                }else if(mapEntry.getValue().values.size()==1) {
                    Toast.makeText(getActivity(), "single entry on  "+mapEntry.getKey(), Toast.LENGTH_SHORT).show();
                    bar = new BarEntry(i, mapEntry.getValue().values.get(0), mapEntry.getValue().labels.get(0));
                    i+=1;
                }
                dataset.add(bar);
            }
            BarDataSet barDataSet = new BarDataSet(dataset, "Entries");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            BarData data = new BarData(barDataSet);
            datewiseAccChart.setData(data);
            Toast.makeText(getActivity(), "keys are "+map.keySet().toString(), Toast.LENGTH_SHORT).show();
            datewiseAccChart.getXAxis().setLabelCount(map.keySet().size());
            datewiseAccChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()){
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return super.getFormattedValue(value-0.5f, axis);
                }
            });
//            class MyXAxisValueFormatter implements XAxisValueFormatter {
//                @Override
//                public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
//                    if (index == 0) return "";
//                    else return original;
//                }
//            }
//            datewiseAccChart.getXAxis().setGranularity(1f);
            datewiseAccChart.getXAxis().setDrawLabels(true);
            datewiseAccChart.getXAxis().setCenterAxisLabels(true);
//            datewiseAccChart.getXAxis().setXOffset(-1f);
            datewiseAccChart.getBarData().setBarWidth(.5f);
            datewiseAccChart.animateXY(2000, 2000);
            datewiseAccChart.setTouchEnabled(true);
            datewiseAccChart.setScaleEnabled(true);
            datewiseAccChart.setFitBars(true);
            datewiseAccChart.fitScreen();
            datewiseAccChart.invalidate();
        }
        private void drawExpenseChart(ArrayList<ExpenseBox> expenseEntryList)
        {
            final HashMap<String, ValueAndLabel<Float,String> > map = new HashMap<>();
            ValueAndLabel<Float,String> vl;
            for(ExpenseBox ex : expenseEntryList){
                Toast.makeText(getActivity(), "date "+ex.getDate(), Toast.LENGTH_SHORT).show();
                if(map.containsKey(ex.getDate())){
                    Toast.makeText(getActivity(), "same date ", Toast.LENGTH_SHORT).show();
                    vl=map.get(ex.getDate());
                    vl.values.add(((float) ex.getAmount()));
                    vl.labels.add(ex.getItemName());
                }else {
                    Toast.makeText(getActivity(), "different date ", Toast.LENGTH_SHORT).show();
                    map.put(ex.getDate(),new ValueAndLabel<>(((float) ex.getAmount()),ex.getItemName()));
                }
            }

            ArrayList<BarEntry> dataset = new ArrayList<>();
            BarEntry bar = null;
            float[] values;
            float i=0;
            for(Map.Entry<String,ValueAndLabel<Float,String>> mapEntry : map.entrySet()){
                if(mapEntry.getValue().values.size()>1){
                    Toast.makeText(getActivity(), "multiple entry on  "+mapEntry.getKey(), Toast.LENGTH_SHORT).show();
                    values = new float[mapEntry.getValue().values.size()];
                    for (int p=0;p<mapEntry.getValue().values.size();p++){
                        values[p] = mapEntry.getValue().values.get(p);
                    }
                    bar = new BarEntry(i, values, mapEntry.getValue().labels);
                    i+=1;
                }else if(mapEntry.getValue().values.size()==1) {
                    Toast.makeText(getActivity(), "single entry on  "+mapEntry.getKey(), Toast.LENGTH_SHORT).show();
                    bar = new BarEntry(i, mapEntry.getValue().values.get(0), mapEntry.getValue().labels.get(0));
                    i+=1;
                }
                dataset.add(bar);
            }
            BarDataSet barDataSet = new BarDataSet(dataset, "Expense Entries");
            barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            BarData data = new BarData(barDataSet);
            datewiseExpChart.setData(data);
            Toast.makeText(getActivity(), "keys are "+map.keySet().toString(), Toast.LENGTH_SHORT).show();
            datewiseExpChart.getXAxis().setLabelCount(map.keySet().size());
//            datewiseAccChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()));
            datewiseExpChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()){
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return super.getFormattedValue(value-0.5f, axis);
                }
            });
//            class MyXAxisValueFormatter implements XAxisValueFormatter {
//                @Override
//                public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
//                    if (index == 0) return "";
//                    else return original;
//                }
//            }
//            datewiseAccChart.getXAxis().setGranularity(1f);
            datewiseExpChart.getXAxis().setDrawLabels(true);
            datewiseExpChart.getXAxis().setCenterAxisLabels(true);
//            datewiseAccChart.getXAxis().setXOffset(-1f);
            datewiseExpChart.getBarData().setBarWidth(.5f);
            datewiseExpChart.animateXY(2000, 2000);
            datewiseExpChart.setTouchEnabled(true);
            datewiseExpChart.setScaleEnabled(true);
            datewiseExpChart.setFitBars(true);
            datewiseExpChart.fitScreen();
            datewiseExpChart.invalidate();

        }
        private ArrayList<String> getXAxisValues () {
            ArrayList<String> xAxis = new ArrayList<>();
            xAxis.add("JAN");
            xAxis.add("FEB");
            xAxis.add("MAR");
            xAxis.add("APR");
            xAxis.add("MAY");
            xAxis.add("JUN");
            return xAxis;
        }

    }
    
class ValueAndLabel<V,L>{
    
    ArrayList<V> values;
    ArrayList<L> labels;

    public ValueAndLabel(V value,L label) {
        this.values = new ArrayList<>();
        values.add(value);
        this.labels = new ArrayList<>();
        labels.add(label);
    }

    public ValueAndLabel(ArrayList<V> values, ArrayList<L> labels) {
        this.values = values;
        this.labels = labels;
    }
}