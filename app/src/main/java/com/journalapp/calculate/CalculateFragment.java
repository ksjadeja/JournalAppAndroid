package com.journalapp.calculate;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.ExpEntriesMap;
import com.journalapp.R;
import com.journalapp.mail.GMailSender;
import com.journalapp.mail.MailSender;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;
import com.journalapp.models.MailBean;
import com.journalapp.utils.CalculateRecyclerViewAdapter;
import com.journalapp.utils.CustomSearchableSpinner;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CalculateFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static long e1=0,e2=0,a1=0,a2=0;

    private TextInputEditText avg_expense_start, avg_expense_end, total_account_start, total_account_end, account_email_box;
    private MaterialTextView avg_expense_total, avg_expense_average, total_account_total, avg_exp_message, total_account_message;
    private MaterialButton avg_expense_submit, total_account_submit, account_send_mail_btn;

    private RecyclerView avg_expense_recycler_view, total_account_recycler_view;

    private ArrayList<ExpenseBox> expense_list;
    private ArrayList<AccountBox> account_list;

    private LinearLayout average_n_total_bar, total_account_bar, account_mail_bar;

    private CustomSearchableSpinner account_names;
    private HashMap<String, String> names_map;
    private ArrayList<String> names_list;
    private Set<String> names_set;
    ArrayAdapter arrayAdapter;

    private String selected_name;

    private Calendar startAvgExp, endAvgExp, startTotalAcc, endTotalAcc;
    private int dayy, monthh, yearr;

    private float average, total, n, account_total;

    private CalculateRecyclerViewAdapter adapter, account_adapter;

    private CollectionReference expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");
    private CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
    private CollectionReference mailRef = FirebaseFirestore.getInstance().collection("mailing_list");

    String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        expense_list = new ArrayList<>();
        account_list = new ArrayList<>();

        names_map = new HashMap<>();
        names_list = new ArrayList<>();
        names_set = new HashSet<>();
        names_list.add("All");
        names_set.add("All");

        View view = inflater.inflate(R.layout.fragment_calculate, container, false);

        avg_expense_start = view.findViewById(R.id.avg_expense_start);
        avg_expense_end = view.findViewById(R.id.avg_expense_end);
        avg_expense_submit = view.findViewById(R.id.avg_expense_submit);
        average_n_total_bar = view.findViewById(R.id.average_n_total_bar);
        avg_exp_message = view.findViewById(R.id.avg_exp_message);

        avg_expense_recycler_view = view.findViewById(R.id.avg_expense_recycler_view);

        avg_expense_total = view.findViewById(R.id.avg_expense_total);
        avg_expense_average = view.findViewById(R.id.avg_expense_average);


        average_n_total_bar.setVisibility(View.GONE);

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
        adapter = new CalculateRecyclerViewAdapter<ExpenseBox>(getContext(), expense_list);
        avg_expense_recycler_view.setAdapter(adapter);


        total_account_start = view.findViewById(R.id.total_account_start);
        total_account_end = view.findViewById(R.id.total_account_end);
        total_account_submit = view.findViewById(R.id.total_account_submit);
        total_account_bar = view.findViewById(R.id.total_account_bar);
        account_names = view.findViewById(R.id.account_names);
        total_account_message = view.findViewById(R.id.total_account_message);


        account_mail_bar = view.findViewById(R.id.account_mail_bar);
        account_mail_bar.setVisibility(View.GONE);
        account_email_box = view.findViewById(R.id.account_email_box);

        account_send_mail_btn = view.findViewById(R.id.account_send_mail_btn);

        total_account_recycler_view = view.findViewById(R.id.total_account_recycler_view);
        total_account_bar.setVisibility(View.GONE);

        total_account_total = view.findViewById(R.id.total_account_total);

        total_account_start.setOnClickListener(this);
        total_account_end.setOnClickListener(this);
        total_account_submit.setOnClickListener(this);
        account_names.setOnItemSelectedListener(this);
        account_send_mail_btn.setOnClickListener(this);

        accountEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable QuerySnapshot snapshots,
                                @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                int i = 0;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    String key = null;
                    AccountBoxDao accountBoxDao = null;
                    switch (dc.getType()) {
                        case ADDED:
                            accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                            if (!names_map.containsKey(accountBoxDao.getName())) {
                                names_map.put(accountBoxDao.getName(), accountBoxDao.getName());
                                names_list.add(accountBoxDao.getName());
//                                arrayAdapter.notifyDataSetChanged();
                            }
                            break;

                        case MODIFIED:
                            accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                            if (!names_map.containsKey(accountBoxDao.getName())) {
                                names_map.put(accountBoxDao.getName(), accountBoxDao.getName());
                                names_list.add(accountBoxDao.getName());
//                                arrayAdapter.notifyDataSetChanged();
                            }
                            break;

                        case REMOVED:
                            accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                            if (names_map.containsKey(accountBoxDao.getName())) {
                                names_map.remove(accountBoxDao.getName());
                                for (String str : names_list) {
                                    if (str.equals(accountBoxDao.getName())) {
                                        names_list.remove(str);
                                        break;
                                    }
                                }
//                                arrayAdapter.notifyDataSetChanged();
                            }
                            break;
                    }
                }
                names_set.addAll(names_list);
                names_list.clear();
                names_list.addAll(names_set);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, names_list);

        account_names.setAdapter(arrayAdapter);
        total_account_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        account_adapter = new CalculateRecyclerViewAdapter<AccountBox>(getContext(), account_list);
        total_account_recycler_view.setAdapter(account_adapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.account_send_mail_btn:
                Log.i("MAILSTATUS:","send button clicked");
                total_account_submit.performClick();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String email = account_email_box.getText().toString();
                        if(email.trim().length() == 0){
                            Toast.makeText(getContext(), "Please enter email address in the mail fragment to send the mail......", Toast.LENGTH_LONG).show();
                            Log.i("MAILSTATUS:","email not entered but clicked on send");
                        }else{
                            if(total_account_total.getCurrentTextColor()==Color.RED){
                                Toast.makeText(getContext(), "Your account entries show you need to pay the person rather than take. So can't send email", Toast.LENGTH_LONG).show();
                                Log.i("MAILSTATUS:","email entered and clicked on send but has to give rather than send");
                            }else {
//                        Toast.makeText(getContext(), " can  send email", Toast.LENGTH_SHORT).show();
                                Log.i("MAILSTATUS:","email entered and clicked on send, eligible to send");
                                sendEmail(email,Double.parseDouble(total_account_total.getText().toString()),selected_name);
                            }
                        }
                    }
                },3000);
                break;
            case R.id.avg_expense_start:
                if (SystemClock.elapsedRealtime() - e1 < 1000) {
                    return;
                }
                e1 = SystemClock.elapsedRealtime();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 0, 0, 0);
                        startAvgExp = calendar;
                        avg_expense_start.setText(new SimpleDateFormat("dd/MM/yyyy").format(startAvgExp.getTime()));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog.show();
                break;
            case R.id.avg_expense_end:
                if (SystemClock.elapsedRealtime() - e2 < 1000) {
                    return;
                }
                e2 = SystemClock.elapsedRealtime();
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 23, 59, 59);
                        endAvgExp = calendar;
                        avg_expense_end.setText(new SimpleDateFormat("dd/MM/yyyy").format(endAvgExp.getTime()));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog2.show();
                break;

            case R.id.avg_expense_submit:
                if (startAvgExp != null && endAvgExp != null) {
                    expense_list.clear();
                    total = 0;
                    average = 0;
                    n = daysBetween(startAvgExp, endAvgExp);
                    if (startAvgExp.compareTo(endAvgExp) <= 0) {
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

                                            key = dc.getDocument().getId();
                                            expenseBoxDao = dc.getDocument().toObject(ExpenseBoxDao.class);
                                            expense_list.add(0, new ExpenseBox(expenseBoxDao, key));
                                            adapter.notifyDataSetChanged();
                                            total += expenseBoxDao.getAmount();
                                            average = total / n;

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
                                            for (ExpenseBox exp : expense_list) {
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
                                                average = total / n;
                                                avg_expense_total.setText(String.valueOf(total));
                                                avg_expense_average.setText(String.valueOf(average));
                                            }
                                            break;
                                    }
                                }
                                if (expense_list.size() > 0) {
                                    showExpenseTotalBar();
                                } else {
                                    hideExpenseTotalBar();
                                }
                            }
                        });
                    } else {
                        avg_exp_message.setVisibility(View.VISIBLE);
                        average_n_total_bar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                    }

                } else {
                    avg_exp_message.setVisibility(View.VISIBLE);
                    average_n_total_bar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                }
                break;


            case R.id.total_account_start:
                if (SystemClock.elapsedRealtime() - a1 < 1000) {
                    return;
                }
                a1 = SystemClock.elapsedRealtime();
                DatePickerDialog datePickerDialog3 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 0, 0, 0);
                        startTotalAcc = calendar;
                        total_account_start.setText(new SimpleDateFormat("dd/MM/yyyy").format(startTotalAcc.getTime()));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog3.show();
                break;
            case R.id.total_account_end:
                if (SystemClock.elapsedRealtime() - a2 < 1000) {
                    return;
                }
                a2 = SystemClock.elapsedRealtime();
                DatePickerDialog datePickerDialog4 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 23, 59, 59);
                        endTotalAcc = calendar;
                        total_account_end.setText(new SimpleDateFormat("dd/MM/yyyy").format(endTotalAcc.getTime()));
                    }
                }, yearr, monthh, dayy);
                datePickerDialog4.show();
                break;


            case R.id.total_account_submit:

                if (startTotalAcc != null && endTotalAcc != null) {
                    account_mail_bar.setVisibility(View.GONE);
                    account_email_box.setText("");
                    account_email_box.setInputType(InputType.TYPE_NULL);
                    account_email_box.setEnabled(false);

                    account_list.clear();
                    account_total = 0;
                    if (startTotalAcc.compareTo(endTotalAcc) <= 0) {
                        if (selected_name.equals("All")) {
                            accountEntriesRef.document(USER).collection("entries").whereGreaterThanOrEqualTo("timestamp", startTotalAcc.getTime()).whereLessThan("timestamp", endTotalAcc.getTime()).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                                key = dc.getDocument().getId();
                                                accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                                account_list.add(0, new AccountBox(accountBoxDao, key));
                                                account_adapter.notifyDataSetChanged();
                                                if (accountBoxDao.getT_type().equals("0")) {
                                                    account_total -= accountBoxDao.getAmount();
                                                } else {
                                                    account_total += accountBoxDao.getAmount();
                                                }
                                                if (account_total >= 0) {
                                                    total_account_total.setTextColor(Color.GREEN);
                                                    total_account_total.setText(String.valueOf(account_total));
                                                } else {
                                                    total_account_total.setTextColor(Color.RED);
                                                    total_account_total.setText(String.valueOf(Math.abs(account_total)));
                                                }
                                                break;

                                            case MODIFIED:
                                                key = dc.getDocument().getId();
                                                accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                                for (AccountBox ex : account_list) {
                                                    if (ex.getId().equals(key)) {
                                                        account_list.set(account_list.indexOf(ex), new AccountBox(accountBoxDao, key));
                                                        account_adapter.notifyDataSetChanged();
                                                        break;
                                                    }

                                                }
                                                account_total = 0;
                                                for (AccountBox exp : account_list) {
                                                    if (exp.getT_type().equals("0")) {
                                                        account_total -= exp.getAmount();
                                                    } else {
                                                        account_total += exp.getAmount();
                                                    }
                                                }
                                                if (account_total >= 0) {
                                                    total_account_total.setTextColor(Color.GREEN);
                                                    total_account_total.setText(String.valueOf(account_total));
                                                } else {
                                                    total_account_total.setTextColor(Color.RED);
                                                    total_account_total.setText(String.valueOf(Math.abs(account_total)));
                                                }
                                                break;

                                            case REMOVED:
                                                for (AccountBox ex : account_list) {
                                                    if (ex.getId().equals(dc.getDocument().getId())) {
                                                        if (ex.getT_type().equals("0")) {
                                                            account_total += ex.getAmount();
                                                        } else {
                                                            account_total -= ex.getAmount();
                                                        }
                                                        account_list.remove(ex);
                                                        account_adapter.notifyDataSetChanged();
                                                        break;
                                                    }
                                                }
                                                if (account_total >= 0) {
                                                    total_account_total.setTextColor(Color.GREEN);
                                                    total_account_total.setText(String.valueOf(account_total));
                                                } else {
                                                    total_account_total.setTextColor(Color.RED);
                                                    total_account_total.setText(String.valueOf(Math.abs(account_total)));
                                                }
                                                break;
                                        }
                                    }
                                    if (account_list.size() > 0) {
                                        showAccountTotalBar();
                                    } else {
                                        hideAccountTotalBar();
                                    }
                                }
                            });
                        } else {
                            accountEntriesRef.document(USER).collection("entries").whereEqualTo("name", selected_name).whereGreaterThanOrEqualTo("timestamp", startTotalAcc.getTime()).whereLessThan("timestamp", endTotalAcc.getTime()).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                                key = dc.getDocument().getId();
                                                accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                                account_list.add(0, new AccountBox(accountBoxDao, key));
                                                account_adapter.notifyDataSetChanged();
                                                if (accountBoxDao.getT_type().equals("0")) {
                                                    account_total -= accountBoxDao.getAmount();
                                                } else {
                                                    account_total += accountBoxDao.getAmount();
                                                }
                                                if (account_total >= 0) {
                                                    total_account_total.setTextColor(Color.GREEN);
                                                    total_account_total.setText(String.valueOf(account_total));
                                                } else {
                                                    total_account_total.setTextColor(Color.RED);
                                                    total_account_total.setText(String.valueOf(Math.abs(account_total)));
                                                }
                                                break;

                                            case MODIFIED:
                                                key = dc.getDocument().getId();
                                                accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                                                for (AccountBox ex : account_list) {
                                                    if (ex.getId().equals(key)) {
                                                        account_list.set(account_list.indexOf(ex), new AccountBox(accountBoxDao, key));
                                                        account_adapter.notifyDataSetChanged();
                                                        break;
                                                    }

                                                }
                                                account_total = 0;
                                                for (AccountBox exp : account_list) {
                                                    if (exp.getT_type().equals("0")) {
                                                        account_total -= exp.getAmount();
                                                    } else {
                                                        account_total += exp.getAmount();
                                                    }
                                                }
                                                if (account_total >= 0) {
                                                    total_account_total.setTextColor(Color.GREEN);
                                                    total_account_total.setText(String.valueOf(account_total));
                                                } else {
                                                    total_account_total.setTextColor(Color.RED);
                                                    total_account_total.setText(String.valueOf(Math.abs(account_total)));
                                                }
                                                break;

                                            case REMOVED:
                                                for (AccountBox ex : account_list) {
                                                    if (ex.getId().equals(dc.getDocument().getId())) {
                                                        if (ex.getT_type().equals("0")) {
                                                            account_total += ex.getAmount();
                                                        } else {
                                                            account_total -= ex.getAmount();
                                                        }
                                                        account_list.remove(ex);
                                                        account_adapter.notifyDataSetChanged();
                                                        break;
                                                    }
                                                }
                                                if (account_total >= 0) {
                                                    total_account_total.setTextColor(Color.GREEN);
                                                    total_account_total.setText(String.valueOf(account_total));
                                                } else {
                                                    total_account_total.setTextColor(Color.RED);
                                                    total_account_total.setText(String.valueOf(Math.abs(account_total)));
                                                }
                                                break;
                                        }
                                    }
                                    if(account_list.size()>0){
                                        showAccountTotalBar();
                                    }else {
                                        hideAccountTotalBar();
                                    }
                                }
                            });

                            mailRef.document(USER).collection("entries").whereEqualTo("personName", selected_name).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().getDocuments().size() > 0) {
                                                    MailBean mailBean = task.getResult().getDocuments().get(0).toObject(MailBean.class);
                                                    if (mailBean.getEmail() != null && !mailBean.getEmail().equals("")) {
                                                        account_email_box.setText(mailBean.getEmail());
                                                        account_email_box.setEnabled(false);
                                                        if (account_list.size() > 0) {
                                                            account_mail_bar.setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                } else {
                                                    Log.i("LOG:", "no email found for: " + selected_name, task.getException());
                                                }
                                            }else {
                                                Log.i("EXCEPTION:", "email finding query failed", task.getException());
                                            }
                                        }

                                    });


                        }
                    } else {
                        total_account_message.setVisibility(View.VISIBLE);
                        total_account_bar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                    }

                } else {
                    total_account_message.setVisibility(View.VISIBLE);
                    total_account_bar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Select appropriate start and end date", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void sendEmail(String email,double amount,String selectedName) {
        try {
            String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if(userName==null)
                userName=FirebaseAuth.getInstance().getCurrentUser().getEmail();
            Log.i("MAILSTATUS:","send mail to "+email+" name "+selectedName+" amt "+amount);

            MailSender mailSender  = new MailSender(getContext(),email,"Return the money","Hello Mr."+selectedName+", \n  Our user "+userName+" has reported" +
                    " in his daily account entries on our app that you have to pay him" +
                    " a net amount of Rs."+amount+". Kindly pay it on time.\n You can also join us on JournalApp which will help you keep" +
                    "track of your daily account with friends and family and also your daily expenses and your daily activities" +
                    ". Click Here to download " +
                    "our App <Link HERE>\n Regards, \n Team JournalApp");
            mailSender.execute();

        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            selected_name = "All";
        } else {
            selected_name = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
    public static int daysBetween(Calendar day1, Calendar day2) {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Calculate");
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Calculate");
    }

    private void showExpenseTotalBar() {
        avg_exp_message.setVisibility(View.GONE);
        average_n_total_bar.setVisibility(View.VISIBLE);
    }

    private void hideExpenseTotalBar() {
        avg_exp_message.setVisibility(View.VISIBLE);
        average_n_total_bar.setVisibility(View.GONE);
    }

    private void showAccountTotalBar() {
        total_account_message.setVisibility(View.GONE);
        total_account_bar.setVisibility(View.VISIBLE);
    }

    private void hideAccountTotalBar() {
        total_account_message.setVisibility(View.VISIBLE);
        total_account_bar.setVisibility(View.GONE);
    }
}
