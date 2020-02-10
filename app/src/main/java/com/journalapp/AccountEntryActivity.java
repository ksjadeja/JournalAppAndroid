package com.journalapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journalapp.models.AccountBoxDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AccountEntryActivity extends AppCompatActivity implements View.OnClickListener {

    TextView accountEntryDate;
    TextView accountEntryTime;
    Button addAccountEntry;
    Button addExpenseEntry;
    public static SimpleDateFormat dateFormat, timeFormat;
    String date, time;
    private View myView;
    String user;
    DatabaseReference entriesDb,byDateDb;
    private int t_type=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_entry);
        user = "Kiran1901";
        entriesDb = FirebaseDatabase.getInstance().getReference("account_entries").child(user);
        byDateDb = FirebaseDatabase.getInstance().getReference("by_date").child(user);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        timeFormat = new SimpleDateFormat("hh:mm:ss a");

        addAccountEntry = findViewById(R.id.btn_add_account_entry);
        addExpenseEntry = findViewById(R.id.btn_add_expense_entry);


        LayoutInflater layoutInflater = getLayoutInflater();
        myView = layoutInflater.inflate(R.layout.layout_account_entries, null, false);

        accountEntryDate = myView.findViewById(R.id.account_entry_date);
        accountEntryTime = myView.findViewById(R.id.account_entry_time);


        addAccountEntry.setOnClickListener(this);
        addExpenseEntry.setOnClickListener(this);


    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_category_give:
                if (checked)
                        t_type=0;
                    break;
            case R.id.radio_category_take:
                if (checked)
                    t_type=1;
                    break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_account_entry:
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(AccountEntryActivity.this);
                if(myView.getParent() != null) {
                    ((ViewGroup)myView.getParent()).removeView(myView); // <- fix
                }
                alertDialog2.setView(myView);
                Calendar c = Calendar.getInstance();
                date = dateFormat.format(c.getTime());
                time = timeFormat.format(c.getTime());
                accountEntryDate.setText(date);
                accountEntryTime.setText(time);
                alertDialog2.setPositiveButton("Add Account Entry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                finish();
                        final EditText edtName = myView.findViewById(R.id.edt_person_name);
                        final EditText edtAmount = myView.findViewById(R.id.edt_amount);
                        final EditText description = myView.findViewById(R.id.edt_desc);

                        AccountBoxDao accEntrybox = new AccountBoxDao();
                        accEntrybox.setName(edtName.getText().toString());
                        try {
                            accEntrybox.setAmount(edtAmount.getText().toString());
                        }catch (Exception e)
                        {
                            Toast.makeText(AccountEntryActivity.this, "Enter amount in figures only", Toast.LENGTH_SHORT).show();
                        }
                        accEntrybox.setDesc(description.getText().toString());
                        accEntrybox.setT_type(String.valueOf(t_type));
                        accEntrybox.setDate(date);
                        accEntrybox.setTime(time);
                        String key = entriesDb.push().getKey();
                        entriesDb.child(key).setValue(accEntrybox);
//                        byDateDb.child(date).child("journal_entries").addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                                final String key,newKey;
//                                key = dataSnapshot.getKey();
//                                newKey = dataSnapshot.getValue(String.class);
//                                entriesDb.child(newKey);
//                                entriesDb.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
//                                        AccountBoxDao accountEntryBoxDao;
//                                        accountEntryBoxDao= dataSnapshot1.getValue(AccountBoxDao.class);
//                                        feedboxesList.add(0, new AccountBoxDao(accountEntryBoxDao, dataSnapshot1.getKey()));
////                        EntriesMap.addFirst(key);
//                                        recyclerViewAdapter.notifyItemInserted(0);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//
//                            }
//
//                            @Override
//                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                                String newKey;
//                                FeedboxDao feedboxDao;
//                                newKey = dataSnapshot.getValue(String.class);
//                                feedboxDao = dataSnapshot.getValue(FeedboxDao.class);
//                                // TODO add something which reflects changes in real-time
//
//                                for(int i=0; i<feedboxesList.size();i++){
//                                    if(feedboxesList.get(i).getId().equals(newKey)){
//                                        feedboxesList.set(i,new Feedbox(feedboxDao,newKey));
//                                    }
//                                }
//                                recyclerViewAdapter.notifyDataSetChanged();
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                                for(Feedbox fb:feedboxesList){
//                                    if(fb.getId().equals(dataSnapshot.getKey())){
////                        EntriesMap.delete(fb.getId(),feedboxesList.indexOf(fb));
//                                        feedboxesList.remove(fb);
//                                        recyclerViewAdapter.notifyDataSetChanged();
//                                        return;
//                                    }
//                                }
//
//                            }
//
//                            @Override
//                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                Toast.makeText(getContext(),"Firebase Error: "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
//                            }
//                        });
                        Toast.makeText(AccountEntryActivity.this,"Entry Saved",Toast.LENGTH_SHORT).show();

                    }
                });
                alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                alertDialog2.show();
                break;
            case R.id.btn_add_expense_entry:
                Intent intent = new Intent(AccountEntryActivity.this,ExpenseEntryActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}