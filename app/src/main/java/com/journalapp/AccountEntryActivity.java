package com.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
                        final String key = entriesDb.push().getKey();
                        entriesDb.child(key).setValue(accEntrybox);
                        byDateDb.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String newKey = byDateDb.child(date).child("account_entries").push().getKey();
                                byDateDb.child(date).child("account_entries").child(newKey).setValue(key);
                                Log.i("msg2","added in by_entry");
                                Toast.makeText(AccountEntryActivity.this,"added in by_entry",Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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