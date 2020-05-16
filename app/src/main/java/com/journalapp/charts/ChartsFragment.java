package com.journalapp.charts;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Objects;
import java.util.TreeMap;

public class ChartsFragment extends Fragment implements View.OnClickListener {

    private BarChart datewiseAccChart, datewiseExpChart, datewisePersonAccChart;
    private LineChart expenseChart;

    private EditText startDate, endDate, startDateExp, endDateExp, startDatePerson, endDatePerson,startDateExpense,endDateExpense;
    private CollectionReference accountEntriesRef;
    private CollectionReference expenseEntriesRef;
    private Button submit, submitExp, submitPerson,submitExpense;
    private Date startAcc, endAcc, startExp, endExp, startPerson, endPerson,startExpense,endExpense;
    private int dayy, monthh, yearr;
    static boolean firstTime = false;
    String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";

    ArrayList<AccountBox> accountEntryList;
    ArrayList<AccountBox> accountEntryList2;
    ArrayList<ExpenseBox> expenseEntryList;
    MyMarkerView myMarkerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
        expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");
        firstTime = false;
        View rootView = inflater.inflate(R.layout.fragment_charts, container, false);
        myMarkerView = new MyMarkerView(rootView.getContext(), R.layout.custom_marker_view);

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

        expenseChart = rootView.findViewById(R.id.expense_chart);
        startDateExpense = rootView.findViewById(R.id.start_date_expense);
        endDateExpense = rootView.findViewById(R.id.end_date_expense);
        submitExpense = rootView.findViewById(R.id.submit_expense);

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

        startDateExpense.setOnClickListener(this);
        endDateExpense.setOnClickListener(this);
        submitExpense.setOnClickListener(this);

        return rootView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> {
//                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 0, 0, 0);
                    startAcc = calendar.getTime();
                    startDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(startAcc));
                }, yearr, monthh, dayy);
                datePickerDialog.show();
                break;
            case R.id.end_date:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> {
//                        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 23, 59, 59);
                    endAcc = calendar.getTime();
//                        Toast.makeText(getActivity(), "year endAcc "+year, Toast.LENGTH_SHORT).show();
                    endDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(endAcc));
                }, yearr, monthh, dayy);
                datePickerDialog2.show();
                break;
            case R.id.submit:
                if (startAcc != null && endAcc != null) {
                    if (startAcc.compareTo(endAcc) <= 0) {
                        accountEntryList = new ArrayList<>();
                        accountEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startAcc).whereLessThan("timestamp", endAcc).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Toast.makeText(getActivity(), "listener error", Toast.LENGTH_SHORT).show();
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
                            drawAccountBarChart(accountEntryList);
                        });
                    } else {
                        Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                }
                break;


            case R.id.start_date_exp:
                DatePickerDialog datePickerDialog3 = new DatePickerDialog(Objects.requireNonNull(getContext()), (datePicker, year, month, day) -> {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 0, 0, 0);
                    startExp = calendar.getTime();
                    startDateExp.setText(formatter.format(startExp));
                }, yearr, monthh, dayy);
                datePickerDialog3.show();
                break;
            case R.id.end_date_exp:
                DatePickerDialog datePickerDialog4 = new DatePickerDialog(Objects.requireNonNull(getContext()), (datePicker, year, month, day) -> {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 23, 59, 59);
                    endExp = calendar.getTime();
                    endDateExp.setText(formatter.format(endExp));
                }, yearr, monthh, dayy);
                datePickerDialog4.show();
                break;


            case R.id.submit_exp:
                if (startExp != null && endExp != null) {
                    if (startExp.compareTo(endExp) <= 0) {
                        expenseEntryList = new ArrayList<>();
                        expenseEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startExp).whereLessThan("timestamp", endExp).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                drawExpenseChart(expenseEntryList);
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.start_date_person:
                DatePickerDialog datePickerDialog5 = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> {
                    DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 0, 0, 0);
                    startPerson = calendar.getTime();
                    startDatePerson.setText(new SimpleDateFormat("dd/MM/yyyy").format(startPerson));
                }, yearr, monthh, dayy);
                datePickerDialog5.show();
                break;
            case R.id.end_date_person:
                DatePickerDialog datePickerDialog6 = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> {
                    DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 23, 59, 59);
                    endPerson = calendar.getTime();
                    endDatePerson.setText(new SimpleDateFormat("dd/MM/yyyy").format(endPerson));
                }, yearr, monthh, dayy);
                datePickerDialog6.show();
                break;


            case R.id.submit_person:
                if (startPerson != null && endPerson != null) {
                    if (startPerson.compareTo(endPerson) <= 0) {
                        accountEntryList2 = new ArrayList<>();
                        accountEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startPerson).whereLessThan("timestamp", endPerson).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((queryDocumentSnapshots, e) -> {
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
                            drawAccountBarChartPerson(accountEntryList2);
                        });

                    } else {
                        Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.start_date_expense:
                DatePickerDialog datePickerDialog7 = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 0, 0, 0);
                    startExpense = calendar.getTime();
                    startDateExpense.setText(formatter.format(startExpense));
                }, yearr, monthh, dayy);
                datePickerDialog7.show();
                break;
            case R.id.end_date_expense:
                DatePickerDialog datePickerDialog8 = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, 23, 59, 59);
                    endExpense = calendar.getTime();
                    endDateExpense.setText(formatter.format(endExpense));
                }, yearr, monthh, dayy);
                datePickerDialog8.show();
                break;


            case R.id.submit_expense:
                if (startExpense != null && endExpense != null) {
                    if (startExpense.compareTo(endExpense) <= 0) {
                        expenseEntryList = new ArrayList<>();
                        expenseEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startExpense).whereLessThan("timestamp", endExpense).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((queryDocumentSnapshots, e) -> {
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
                            drawExpenseLineChart(expenseEntryList);
                        });
                    } else {
                        Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void drawExpenseLineChart(ArrayList<ExpenseBox> expenseEntryList) {
        myMarkerView.setChartView(expenseChart);
        expenseChart.setMarker(myMarkerView);

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = expenseChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(10f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll1 = new LimitLine(215f, "Maximum Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(70f, "Minimum Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        YAxis leftAxis = expenseChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setDrawLimitLinesBehindData(false);

        expenseChart.getAxisRight().setEnabled(false);

        final TreeMap<String, ValueAndLabel<Float, String>> map = new TreeMap<>();
        ValueAndLabel<Float, String> vl;
        for (ExpenseBox ex : expenseEntryList) {
            if (map.containsKey(ex.getDate())) {
                vl = map.get(ex.getDate());
                float tmpAmt2 = vl.values.get(0);
                tmpAmt2+=((float) ex.getAmount());
                vl.values.set(0,tmpAmt2);
            } else {
                map.put(ex.getDate(), new ValueAndLabel<>(((float) ex.getAmount()), ex.getItemName()));
            }
        }

        int i=1;
        ArrayList<Entry> values = new ArrayList<>();
        for (Map.Entry<String, ValueAndLabel<Float, String>> mapEntry : map.entrySet()) {
            if (mapEntry.getValue().values.size() == 1) {
                values.add(new Entry(i,mapEntry.getValue().values.get(0)));
                i += 1;
            }
        }
        LineDataSet set1;
        if (expenseChart.getData() != null &&
                expenseChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) expenseChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            expenseChart.getData().notifyDataChanged();
            expenseChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Daily Expenses");
            set1.setDrawIcons(true);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);

            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_blue);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);

            expenseChart.setData(data);
            expenseChart.getXAxis().setLabelCount(map.keySet().size());
            expenseChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()) {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return super.getFormattedValue(value, axis);
                }
            });
            Toast.makeText(getContext(), "keys "+map.keySet(), Toast.LENGTH_SHORT).show();

            expenseChart.getXAxis().setAvoidFirstLastClipping(true);
            expenseChart.getXAxis().setLabelCount(map.keySet().size());
            expenseChart.getXAxis().setDrawLabels(true);
            expenseChart.getXAxis().setAxisMinimum(0.7f);
            expenseChart.invalidate();
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

        datewisePersonAccChart.getXAxis().setDrawLabels(true);
        datewisePersonAccChart.getXAxis().setCenterAxisLabels(true);
        datewisePersonAccChart.getXAxis().setAxisMinimum(0);
        datewisePersonAccChart.getBarData().setBarWidth(.5f);
        datewisePersonAccChart.setDoubleTapToZoomEnabled(false);
        datewisePersonAccChart.setPinchZoom(false);
        datewisePersonAccChart.animateXY(2000, 2000);

        datewisePersonAccChart.fitScreen();


        datewisePersonAccChart.setDragEnabled(true);
        datewisePersonAccChart.setHorizontalScrollBarEnabled(true);
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
            if (map.containsKey(acc.getDate())) {
                vl = map.get(acc.getDate());
                if (acc.getT_type().equals("0")) {
                    vl.values.add((-(float) acc.getAmount()));
                } else if (acc.getT_type().equals("1")) {
                    vl.values.add(((float) acc.getAmount()));
                }
                vl.labels.add(acc.getName());
            } else {
                if (acc.getT_type().equals("0")) {
                    map.put(acc.getDate(), new ValueAndLabel<>((-(float) acc.getAmount()), acc.getName()));
                } else if (acc.getT_type().equals("1")) {
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
        datewiseAccChart.setData(data);
        datewiseAccChart.getXAxis().setLabelCount(map.keySet().size());
        datewiseAccChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()) {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return super.getFormattedValue(value, axis);
            }
        });

        datewiseAccChart.getXAxis().setDrawLabels(true);
        datewiseAccChart.getXAxis().setCenterAxisLabels(true);
        datewiseAccChart.getXAxis().setAxisMinimum(0);
        datewiseAccChart.getBarData().setBarWidth(.5f);
        datewiseAccChart.setDoubleTapToZoomEnabled(false);
        datewiseAccChart.setPinchZoom(false);
        datewiseAccChart.animateXY(2000, 2000);
        datewiseAccChart.setFitBars(true);

        datewiseAccChart.fitScreen();
        datewiseAccChart.setDragEnabled(true);
        datewiseAccChart.invalidate();
    }

    private void drawExpenseChart(ArrayList<ExpenseBox> expenseEntryList) {
        final HashMap<String, ValueAndLabel<Float, String>> map = new HashMap<>();
        ValueAndLabel<Float, String> vl;
        for (ExpenseBox ex : expenseEntryList) {
            if (map.containsKey(ex.getDate())) {
                vl = map.get(ex.getDate());
                vl.values.add(((float) ex.getAmount()));
                vl.labels.add(ex.getItemName());
            } else {
                map.put(ex.getDate(), new ValueAndLabel<>(((float) ex.getAmount()), ex.getItemName()));
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
        BarDataSet barDataSet = new BarDataSet(dataset, "Expense Entries");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(barDataSet);
        datewiseExpChart.setData(data);

        datewiseExpChart.getXAxis().setLabelCount(map.keySet().size());

        datewiseExpChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(map.keySet()) {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return super.getFormattedValue(value, axis);
            }
        });
        datewiseExpChart.getXAxis().setDrawLabels(true);
        datewiseExpChart.getXAxis().setCenterAxisLabels(true);
        datewiseExpChart.getBarData().setBarWidth(.5f);
        datewiseExpChart.animateXY(2000, 2000);
        datewiseExpChart.setTouchEnabled(true);
        datewiseExpChart.setDoubleTapToZoomEnabled(false);
        datewiseExpChart.setPinchZoom(false);
        datewiseExpChart.setFitBars(true);
        datewiseExpChart.fitScreen();
        datewiseExpChart.invalidate();
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