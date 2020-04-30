package com.journalapp.charts;

import android.annotation.SuppressLint;
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

    private BarChart datewiseAccChart, datewiseExpChart, datewisePersonAccChart;
    private EditText startDate, endDate, startDateExp, endDateExp, startDatePerson, endDatePerson;
    private CollectionReference accountEntriesRef;
    private CollectionReference expenseEntriesRef;
    private Button submit, submitExp, submitPerson;
    private Date startAcc, endAcc, startExp, endExp, startPerson, endPerson;
    private int dayy, monthh, yearr;
    static boolean firstTime = false;
    String USER = "Kiran1901";

    ArrayList<AccountBox> accountEntryList;
    ArrayList<AccountBox> accountEntryList2;
    ArrayList<ExpenseBox> expenseEntryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
        expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");
        firstTime = false;
        View rootView = inflater.inflate(R.layout.fragment_charts, container, false);

        datewiseAccChart = rootView.findViewById(R.id.datewiseAccChart);
        startDate = rootView.findViewById(R.id.start_date);
        endDate = rootView.findViewById(R.id.end_date);
        submit = rootView.findViewById(R.id.submit);

        datewiseExpChart = rootView.findViewById(R.id.datewiseExpChart);
        startDateExp = rootView.findViewById(R.id.start_date_exp);
        endDateExp = rootView.findViewById(R.id.end_date_exp);
        submitExp = rootView.findViewById(R.id.submit_exp);

        datewisePersonAccChart = rootView.findViewById(R.id.datewisePersonAccChart);
        startDatePerson = rootView.findViewById(R.id.start_date_person);
        endDatePerson = rootView.findViewById(R.id.end_date_person);
        submitPerson = rootView.findViewById(R.id.submit_person);

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

        startDatePerson.setOnClickListener(this);
        endDatePerson.setOnClickListener(this);
        submitPerson.setOnClickListener(this);
        return rootView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 0, 0, 0);
                        startAcc = calendar.getTime();
                        startDate.setText(new SimpleDateFormat("dd/MM/YYYY").format(startAcc));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog.show();
                break;
            case R.id.end_date:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 23, 59, 59);
                        endAcc = calendar.getTime();
//                        Toast.makeText(getActivity(), "year endAcc "+year, Toast.LENGTH_SHORT).show();
                        endDate.setText(new SimpleDateFormat("dd/MM/YYYY").format(endAcc));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog2.show();
                break;


            case R.id.submit:
                if (startAcc != null && endAcc != null) {
                    System.out.println("  startAcc::" + startAcc + "  endAcc::" + endAcc + "  today::" + new Date());
                    Toast.makeText(getActivity(), "button clicked", Toast.LENGTH_LONG).show();
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
//                                Toast.makeText(getActivity(),"inside listener type "+dc.getType(),Toast.LENGTH_LONG).show();
                                String key = null;
                                AccountBoxDao accountBoxDao = null;
                                switch (dc.getType()) {
                                    case ADDED:
                                        System.out.println("in the add::::");

                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
//                                        Toast.makeText(getActivity(), "in add with "+key, Toast.LENGTH_SHORT).show();
                                        accountEntryList.add(0, new AccountBox(accountBoxDao, key));
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                        for (AccountBox ac : accountEntryList) {
                                            if (ac.getId().equals(key)) {
                                                accountEntryList.set(accountEntryList.indexOf(ac), new AccountBox(accountBoxDao, key));
                                                break;
                                            }
                                        }
                                        break;

                                    case REMOVED:
                                        for (AccountBox ac : accountEntryList) {
                                            if (ac.getId().equals(dc.getDocument().getId())) {
                                                AccEntriesMap.delete(ac.getId(), accountEntryList.indexOf(ac));
                                                accountEntryList.remove(ac);
                                                break;
                                            }
                                        }
                                        break;
                                }
                            }
//                            Toast.makeText(getActivity(), "list  "+accountEntryList, Toast.LENGTH_SHORT).show();
                            drawAccountBarChart(accountEntryList);
                        }
                    });

                    System.out.println("myList::::" + accountEntryList);

                }
                break;
            case R.id.start_date_exp:
                DatePickerDialog datePickerDialog3 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 0, 0, 0);
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
                        calendar.set(year, month, day, 23, 59, 59);
                        endExp = calendar.getTime();
//                        Toast.makeText(getActivity(), "year endAcc "+year, Toast.LENGTH_SHORT).show();
                        endDateExp.setText(formatter.format(endExp));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog4.show();
                break;


            case R.id.submit_exp:
                if (startExp != null && endExp != null) {
                    System.out.println("  start ::" + startExp + "  endAcc::" + endExp + "  today::" + new Date());
//                    Toast.makeText(getActivity(),"button clicked",Toast.LENGTH_LONG).show();
                    expenseEntryList = new ArrayList<>();
                    expenseEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startExp).whereLessThan("timestamp", endExp).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
//                                Toast.makeText(getActivity(), "listener error", Toast.LENGTH_SHORT).show();
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }
//                            Toast.makeText(getActivity(),"inside listener",Toast.LENGTH_LONG).show();
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                                Toast.makeText(getActivity(),"inside listener type "+dc.getType(),Toast.LENGTH_LONG).show();
                                String key = null;
                                ExpenseBoxDao expenseBoxDao = null;
                                switch (dc.getType()) {
                                    case ADDED:
                                        System.out.println("in the add::::");

                                        key = dc.getDocument().getId();
                                        expenseBoxDao = dc.getDocument().toObject(ExpenseBoxDao.class);
//                                        Toast.makeText(getActivity(), "in add with "+key, Toast.LENGTH_SHORT).show();
                                        expenseEntryList.add(0, new ExpenseBox(expenseBoxDao, key));
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        expenseBoxDao = dc.getDocument().toObject(ExpenseBoxDao.class);
                                        for (ExpenseBox ex : expenseEntryList) {
                                            if (ex.getId().equals(key)) {
                                                expenseEntryList.set(expenseEntryList.indexOf(ex), new ExpenseBox(expenseBoxDao, key));
                                                break;
                                            }
                                        }
                                        break;

                                    case REMOVED:
                                        for (ExpenseBox ex : expenseEntryList) {
                                            if (ex.getId().equals(dc.getDocument().getId())) {
                                                ExpEntriesMap.delete(ex.getId(), expenseEntryList.indexOf(ex));
                                                expenseEntryList.remove(ex);
                                                break;
                                            }
                                        }
                                        break;
                                }
                            }
//                            Toast.makeText(getActivity(), "list  "+expenseEntryList, Toast.LENGTH_SHORT).show();
                            drawExpenseChart(expenseEntryList);
                        }
                    });

                    System.out.println("myList::::" + expenseEntryList.toString());
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
                break;
            case R.id.start_date_person:
                DatePickerDialog datePickerDialog5 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 0, 0, 0);
                        startPerson = calendar.getTime();
                        startDatePerson.setText(new SimpleDateFormat("dd/MM/YYYY").format(startPerson));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog5.show();
                break;
            case R.id.end_date_person:
                DatePickerDialog datePickerDialog6 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 23, 59, 59);
                        endPerson = calendar.getTime();
//                        Toast.makeText(getActivity(), "year endAcc "+year, Toast.LENGTH_SHORT).show();
                        endDatePerson.setText(new SimpleDateFormat("dd/MM/YYYY").format(endPerson));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog6.show();
                break;


            case R.id.submit_person:
                if (startPerson != null && endPerson != null) {
                    System.out.println("  startAcc::" + startPerson + "  endAcc::" + endPerson + "  today::" + new Date());
                    Toast.makeText(getActivity(), "button clicked", Toast.LENGTH_LONG).show();
                    accountEntryList2 = new ArrayList<>();
                    accountEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startPerson).whereLessThan("timestamp", endPerson).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
//                                Toast.makeText(getActivity(), "listener error", Toast.LENGTH_SHORT).show();
                                Log.i("ERROR:", "listen:error", e);
                                return;
                            }
//                            Toast.makeText(getActivity(),"inside listener",Toast.LENGTH_LONG).show();
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//                                Toast.makeText(getActivity(),"inside listener type "+dc.getType(),Toast.LENGTH_LONG).show();
                                String key = null;
                                AccountBoxDao accountBoxDao = null;
                                switch (dc.getType()) {
                                    case ADDED:
                                        System.out.println("in the add::::");

                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                        accountEntryList2.add(0, new AccountBox(accountBoxDao, key));
                                        break;

                                    case MODIFIED:
                                        key = dc.getDocument().getId();
                                        accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                        for (AccountBox ac : accountEntryList2) {
                                            if (ac.getId().equals(key)) {
                                                accountEntryList2.set(accountEntryList2.indexOf(ac), new AccountBox(accountBoxDao, key));
                                                break;
                                            }
                                        }
                                        break;

                                    case REMOVED:
                                        for (AccountBox ac : accountEntryList2) {
                                            if (ac.getId().equals(dc.getDocument().getId())) {
                                                AccEntriesMap.delete(ac.getId(), accountEntryList2.indexOf(ac));
                                                accountEntryList2.remove(ac);
                                                break;
                                            }
                                        }
                                        break;
                                }
                            }
//                            Toast.makeText(getActivity(), "list  "+accountEntryList, Toast.LENGTH_SHORT).show();
                            drawAccountBarChartPerson(accountEntryList2);
                        }
                    });

                    System.out.println("myList::::" + accountEntryList2);

                }
                break;
        }
    }

    private void drawAccountBarChartPerson(ArrayList<AccountBox> accountEntryList2) {
        final HashMap<String, ValueAndLabel<Float, String>> map = new HashMap<>();
        ValueAndLabel<Float, String> vl;
        for (AccountBox acc : accountEntryList2) {
            if (map.containsKey(acc.getName())) {
                vl = map.get(acc.getName());
                if (acc.getT_type().equals("0")) {
                    float tmpAmt = vl.values.get(0);
                    if (tmpAmt < 0) {
                        tmpAmt = tmpAmt - (float) acc.getAmount();
                        vl.values.set(0, tmpAmt);
                    } else if (tmpAmt > 0) {
                        if (vl.values.size() > 1) {
                            tmpAmt = vl.values.get(1);
                            tmpAmt = tmpAmt - (float) acc.getAmount();
                            vl.values.set(1, tmpAmt);
                        } else {
                            vl.values.add(-(float) acc.getAmount());
                        }
                    }
                } else if (acc.getT_type().equals("1")) {
                    float tmpAmt = vl.values.get(0);
                    if (tmpAmt > 0) {
                        tmpAmt = tmpAmt + (float) acc.getAmount();
                        vl.values.set(0, tmpAmt);
                    } else if (tmpAmt < 0) {
                        if (vl.values.size() > 1) {
                            tmpAmt = vl.values.get(1);
                            tmpAmt = tmpAmt + (float) acc.getAmount();
                            vl.values.set(1, tmpAmt);
                        } else {
                            vl.values.add((float) acc.getAmount());
                        }
                    }
                }
                vl.labels.add(acc.getName());
            } else {
                if (acc.getT_type().equals("0")) {
                    map.put(acc.getName(), new ValueAndLabel<>((-(float) acc.getAmount()), acc.getName()));
                } else if (acc.getT_type().equals("1")) {
                    map.put(acc.getName(), new ValueAndLabel<>(((float) acc.getAmount()), acc.getName()));
                }
            }
        }
        ArrayList<BarEntry> dataset = new ArrayList<>();
        BarEntry bar = null;
        float[] values;
        float i = 0.5f;
        for (Map.Entry<String, ValueAndLabel<Float, String>> mapEntry : map.entrySet()) {
            if (mapEntry.getValue().values.size() > 1) {
                values = new float[mapEntry.getValue().values.size()];
                for (int p = 0; p < mapEntry.getValue().values.size(); p++) {
                    values[p] = mapEntry.getValue().values.get(p);
                }
                bar = new BarEntry(i, values, mapEntry.getValue().labels);
                i += 1;
            } else if (mapEntry.getValue().values.size() == 1) {
                bar = new BarEntry(i, mapEntry.getValue().values.get(0), mapEntry.getValue().labels.get(0));
                i += 1;
            }
            dataset.add(bar);
        }
        BarDataSet barDataSet = new BarDataSet(dataset, "Entries");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(barDataSet);
        datewisePersonAccChart.setData(data);
        datewisePersonAccChart.getXAxis().setLabelCount(map.keySet().size());
        datewisePersonAccChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()) {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return super.getFormattedValue(value, axis);
            }
        });

//            datewiseAccChart.getXAxis().setGranularity(1f);
        datewisePersonAccChart.getXAxis().setDrawLabels(true);
        datewisePersonAccChart.getXAxis().setCenterAxisLabels(true);
        datewisePersonAccChart.getXAxis().setAxisMinimum(0);
        datewisePersonAccChart.getBarData().setBarWidth(.5f);
        datewisePersonAccChart.setDoubleTapToZoomEnabled(false);
        datewisePersonAccChart.setPinchZoom(false);
        datewisePersonAccChart.animateXY(2000, 2000);
//            datewisePersonAccChart.setTouchEnabled(false);
        datewisePersonAccChart.setScaleEnabled(true);
//            datewisePersonAccChart.setFitBars(true);

        datewisePersonAccChart.fitScreen();


        datewisePersonAccChart.setDragEnabled(true);
//        datewisePersonAccChart.setVisibleXRangeMaximum(3f);
//            datewisePersonAccChart.setVisibleXRange(2,4);
//            datewisePersonAccChart.moveViewToX(data.getEntryCount());
        datewisePersonAccChart.setHorizontalScrollBarEnabled(true);
//            datewisePersonAccChart.enableScroll();
        datewisePersonAccChart.invalidate();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Charts");
    }

    private void drawAccountBarChart(ArrayList<AccountBox> accountEntryList) {
        final HashMap<String, ValueAndLabel<Float, String>> map = new HashMap<>();
        ValueAndLabel<Float, String> vl;
        for (AccountBox acc : accountEntryList) {
//                Toast.makeText(getActivity(), "date "+acc.getDate(), Toast.LENGTH_SHORT).show();
            if (map.containsKey(acc.getDate())) {
//                    Toast.makeText(getActivity(), "same date ", Toast.LENGTH_SHORT).show();
                vl = map.get(acc.getDate());
                if (acc.getT_type().equals("0")) {
//                        Toast.makeText(getActivity(), "t_type 0 give -ve ", Toast.LENGTH_SHORT).show();
                    vl.values.add((-(float) acc.getAmount()));
                } else if (acc.getT_type().equals("1")) {
//                        Toast.makeText(getActivity(), "t_type 1 take +ve ", Toast.LENGTH_SHORT).show();
                    vl.values.add(((float) acc.getAmount()));
                }
                vl.labels.add(acc.getName());
            } else {
//                    Toast.makeText(getActivity(), "different date ", Toast.LENGTH_SHORT).show();
                if (acc.getT_type().equals("0")) {
//                        Toast.makeText(getActivity(), "t_type 0 give -ve ", Toast.LENGTH_SHORT).show();
                    map.put(acc.getDate(), new ValueAndLabel<>((-(float) acc.getAmount()), acc.getName()));
                } else if (acc.getT_type().equals("1")) {
//                        Toast.makeText(getActivity(), "t_type 1 take +ve ", Toast.LENGTH_SHORT).show();
                    map.put(acc.getDate(), new ValueAndLabel<>(((float) acc.getAmount()), acc.getName()));
                }
            }
        }
        ArrayList<BarEntry> dataset = new ArrayList<>();
        BarEntry bar = null;
        float[] values;
        float i = 0.5f;
        for (Map.Entry<String, ValueAndLabel<Float, String>> mapEntry : map.entrySet()) {
            if (mapEntry.getValue().values.size() > 1) {
//                    Toast.makeText(getActivity(), "multiple entry on  "+mapEntry.getKey(), Toast.LENGTH_SHORT).show();
                values = new float[mapEntry.getValue().values.size()];
                for (int p = 0; p < mapEntry.getValue().values.size(); p++) {
                    values[p] = mapEntry.getValue().values.get(p);
                }
                bar = new BarEntry(i, values, mapEntry.getValue().labels);
                i += 1;
            } else if (mapEntry.getValue().values.size() == 1) {
//                    Toast.makeText(getActivity(), "single entry on  "+mapEntry.getKey(), Toast.LENGTH_SHORT).show();
                bar = new BarEntry(i, mapEntry.getValue().values.get(0), mapEntry.getValue().labels.get(0));
                i += 1;
            }
            dataset.add(bar);
        }
        BarDataSet barDataSet = new BarDataSet(dataset, "Entries");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(barDataSet);
        datewiseAccChart.setData(data);
//            Toast.makeText(getActivity(), "keys are "+map.keySet().toString(), Toast.LENGTH_SHORT).show();
        datewiseAccChart.getXAxis().setLabelCount(map.keySet().size());
        datewiseAccChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()) {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return super.getFormattedValue(value, axis);
            }
        });

//            datewiseAccChart.getXAxis().setGranularity(1f);
        datewiseAccChart.getXAxis().setDrawLabels(true);
        datewiseAccChart.getXAxis().setCenterAxisLabels(true);
        datewiseAccChart.getXAxis().setAxisMinimum(0);
        datewiseAccChart.getBarData().setBarWidth(.5f);
        datewiseAccChart.setDoubleTapToZoomEnabled(false);
        datewiseAccChart.setPinchZoom(false);
        datewiseAccChart.animateXY(2000, 2000);
//            datewiseAccChart.setTouchEnabled(false);
        datewiseAccChart.setScaleEnabled(true);
        datewiseAccChart.setFitBars(true);

        datewiseAccChart.fitScreen();


        datewiseAccChart.setDragEnabled(true);
//            datewiseAccChart.setVisibleXRangeMaximum(3f);
//            datewiseAccChart.setVisibleXRange(2,4);
//            datewiseAccChart.moveViewToX(data.getEntryCount());
//            datewiseAccChart.
//            datewiseAccChart.setHorizontalScrollBarEnabled(true);
//            datewiseAccChart.enableScroll();
//            datewiseAccChart.setVerticalScrollBarEnabled(true);
        datewiseAccChart.invalidate();
    }

    private void drawExpenseChart(ArrayList<ExpenseBox> expenseEntryList) {
        final HashMap<String, ValueAndLabel<Float, String>> map = new HashMap<>();
        ValueAndLabel<Float, String> vl;
        for (ExpenseBox ex : expenseEntryList) {
//                Toast.makeText(getActivity(), "date "+ex.getDate(), Toast.LENGTH_SHORT).show();
            if (map.containsKey(ex.getDate())) {
//                    Toast.makeText(getActivity(), "same date ", Toast.LENGTH_SHORT).show();
                vl = map.get(ex.getDate());
                vl.values.add(((float) ex.getAmount()));
                vl.labels.add(ex.getItemName());
            } else {
//                    Toast.makeText(getActivity(), "different date ", Toast.LENGTH_SHORT).show();
                map.put(ex.getDate(), new ValueAndLabel<>(((float) ex.getAmount()), ex.getItemName()));
            }
        }

        ArrayList<BarEntry> dataset = new ArrayList<>();
        BarEntry bar = null;
        float[] values;
        float i = 0.5f;
        for (Map.Entry<String, ValueAndLabel<Float, String>> mapEntry : map.entrySet()) {
            if (mapEntry.getValue().values.size() > 1) {
//                    Toast.makeText(getActivity(), "multiple entry on  "+mapEntry.getKey(), Toast.LENGTH_SHORT).show();
                values = new float[mapEntry.getValue().values.size()];
                for (int p = 0; p < mapEntry.getValue().values.size(); p++) {
                    values[p] = mapEntry.getValue().values.get(p);
                }
                bar = new BarEntry(i, values, mapEntry.getValue().labels);
                i += 1;
            } else if (mapEntry.getValue().values.size() == 1) {
//                    Toast.makeText(getActivity(), "single entry on  "+mapEntry.getKey(), Toast.LENGTH_SHORT).show();
                bar = new BarEntry(i, mapEntry.getValue().values.get(0), mapEntry.getValue().labels.get(0));
                i += 1;
            }
            dataset.add(bar);
        }
        BarDataSet barDataSet = new BarDataSet(dataset, "Expense Entries");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(barDataSet);
        datewiseExpChart.setData(data);
//            Toast.makeText(getActivity(), "keys are "+map.keySet().toString(), Toast.LENGTH_SHORT).show();

        datewiseExpChart.getXAxis().setLabelCount(map.keySet().size());

        datewiseExpChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()) {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return super.getFormattedValue(value, axis);
            }
        });

//            datewiseExpChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

//            datewiseAccChart.getXAxis().setGranularity(1f);
        datewiseExpChart.getXAxis().setDrawLabels(true);
        datewiseExpChart.getXAxis().setCenterAxisLabels(true);
        datewiseExpChart.getBarData().setBarWidth(.5f);
        datewiseExpChart.animateXY(2000, 2000);
        datewiseExpChart.setTouchEnabled(true);
        datewiseExpChart.setDoubleTapToZoomEnabled(false);
        datewiseExpChart.setPinchZoom(false);
        datewiseExpChart.setScaleEnabled(true);
        datewiseExpChart.setFitBars(true);
        datewiseExpChart.fitScreen();
        datewiseExpChart.invalidate();
    }

    private ArrayList<String> getXAxisValues() {
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

class ValueAndLabel<V, L> {
    ArrayList<V> values;
    ArrayList<L> labels;

    public ValueAndLabel(V value, L label) {
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


//public class MyBarDataSet extends BarDataSet {
//
//    public MyBarDataSet(List<BarEntry> yVals, String label) {
//        super(yVals, label);
//    }
//
//    @Override
//    public int getColor(int index) {
//        if(getEntryForXIndex(index).getVal() < 95) // less than 95 green
//            return mColors.get(0);
//        else if(getEntryForXIndex(index).getVal() < 100) // less than 100 orange
//            return mColors.get(1);
//        else // greater or equal than 100 red
//            return mColors.get(2);
//    }
//
//}