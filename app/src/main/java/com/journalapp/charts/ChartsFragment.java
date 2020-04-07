package com.journalapp.charts;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.graphics.Color;
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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.AccEntriesMap;
import com.journalapp.DrawerLayoutActivity2;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChartsFragment extends Fragment implements View.OnClickListener {

    private BarChart datewiseAccChart;
    EditText startDate, endDate;
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
    Button submit;
    Date start, end;
    int day, month, year;

    String USER = "Kiran1901";

    ArrayList<AccountBox> accountEntryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_charts, container, false);
        datewiseAccChart = rootView.findViewById(R.id.datewiseAccChart);
        startDate = rootView.findViewById(R.id.start_date);
        endDate = rootView.findViewById(R.id.end_date);
        submit = rootView.findViewById(R.id.submit);
        Calendar calendar = Calendar.getInstance();

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        submit.setOnClickListener(this);
        return rootView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        start = new Date();
                        start.setDate(day);
                        start.setMonth(month);
                        start.setYear(year);
                        start.setSeconds(0);
                        start.setMinutes(0);
                        start.setHours(0);
                        startDate.setText(start.toString());
                    }
                }, year, month, day);
                datePickerDialog.show();
                break;


            case R.id.end_date:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        end = new Date();
                        end.setDate(day);
                        end.setMonth(month);
                        end.setYear(year);
                        end.setSeconds(0);
                        end.setMinutes(0);
                        end.setHours(0);
                        endDate.setText(end.toString());
                    }
                }, year, month, day);
                datePickerDialog2.show();
                break;


            case R.id.submit:


                if (start != null && end != null) {
                    System.out.println("  start::"+start+"  end::"+end+"  today::"+new Date());
                    Toast.makeText(getActivity(),"button clicked",Toast.LENGTH_LONG).show();
                    accountEntryList = new ArrayList<>();
                    accountEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", start).whereLessThan("timestamp", end).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }

                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                String key = null;
                                AccountBoxDao accountBoxDao = null;
                                switch (dc.getType()) {
                                    case ADDED:
                                        System.out.println("in the add::::");
                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
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
                        }
                    });

                    System.out.println("myList::::"+accountEntryList);

                    drawAccountBarChart(accountEntryList);

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

            HashMap<String, ValueAndLabel<Float,String> > map = new HashMap<>();
            ValueAndLabel<Float,String> vl;
            for(AccountBox acc : accountEntryList){
                if(map.containsKey(acc.getDate())){
                    vl=map.get(acc.getDate());
                    vl.values.add(((float) acc.getAmount()));
                    vl.labels.add(acc.getName());
                }else {
                    map.put(acc.getDate(),new ValueAndLabel<Float, String>(((float) acc.getAmount()),acc.getName()));
                }
            }

            ArrayList<BarEntry> dataset = new ArrayList<>();
            BarEntry bar = null;
            float[] values;
            float i=1;
            for(Map.Entry<String,ValueAndLabel<Float,String>> mapEntry : map.entrySet()){
                if(mapEntry.getValue().values.size()>1){
                    values = new float[mapEntry.getValue().values.size()];
                    for (int p=0;p<mapEntry.getValue().values.size();p++){
                        values[p] = mapEntry.getValue().values.get(p);
                    }
                    bar = new BarEntry(i, values, mapEntry.getValue().labels);
                }else {
                    bar = new BarEntry(i, mapEntry.getValue().values.get(0), mapEntry.getValue().labels.get(0));
                }
                dataset.add(bar);
            }

            BarDataSet barDataSet = new BarDataSet(dataset, "Entries");
            barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            BarData data = new BarData(barDataSet);

            datewiseAccChart.setData(data);
            datewiseAccChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()));
            datewiseAccChart.getBarData().setBarWidth(.9f);
            datewiseAccChart.animateXY(2000, 2000);

            datewiseAccChart.setTouchEnabled(true);
            datewiseAccChart.setScaleEnabled(true);

            datewiseAccChart.setFitBars(true);

            datewiseAccChart.invalidate();


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