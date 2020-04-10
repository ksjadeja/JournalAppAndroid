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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountEntryActivity extends AppCompatActivity{

    TextView accountEntryDate;
    TextView accountEntryTime;


    EditText edtAmount,description;
    AutoCompleteTextView edtName;
    public static SimpleDateFormat dateFormat, timeFormat;
    String date, time;
    private View myView;
    String user;
    String USER= "Kiran1901";
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
    CollectionReference byDateAccEntriesRef = FirebaseFirestore.getInstance().collection("by_date");

    private int t_type=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_entry);
        user = "Kiran1901";

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        timeFormat = new SimpleDateFormat("hh:mm:ss a");


        LayoutInflater layoutInflater = getLayoutInflater();
        myView = layoutInflater.inflate(R.layout.layout_account_entries, null, false);

        accountEntryDate = myView.findViewById(R.id.account_entry_date);
        accountEntryTime = myView.findViewById(R.id.account_entry_time);

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(AccountEntryActivity.this);
        if(myView.getParent() != null) {
            ((ViewGroup)myView.getParent()).removeView(myView); // <- fix
        }
        alertDialog2.setView(myView);
        Calendar c = Calendar.getInstance();
        date = dateFormat.format(c.getTime());
        time = timeFormat.format(c.getTime());

//                edtName = myView.findViewById(R.id.edt_person_name);
//                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item,
//                        accountEntriesRef.document(USER).collection("entries")
//////                                .whereGreaterThan("name",edtName.getText().toString())
//////                                .whereLessThan("name",nextWord(edtName.getText().toString())).get());
//////                edtName.setAdapter(adapter);


        accountEntryDate.setText(date);
        accountEntryTime.setText(time);
        alertDialog2.setPositiveButton("Add Account Entry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                edtName = myView.findViewById(R.id.edt_person_name);
                edtAmount = myView.findViewById(R.id.edt_amount);
                description = myView.findViewById(R.id.edt_desc);

                AccountBox accountBox = new AccountBox();

                accountBox.setName(edtName.getText().toString());
                try {
                    accountBox.setAmount(Integer.parseInt(edtAmount.getText().toString()));
                }catch (Exception e)
                {
                    Toast.makeText(AccountEntryActivity.this, "Enter amount in figures only", Toast.LENGTH_SHORT).show();
                }
                accountBox.setDesc(description.getText().toString());
                accountBox.setT_type(String.valueOf(t_type));
                accountBox.setDate(date);
                accountBox.setTime(time);
                AccountBoxDao accEntrybox = new AccountBoxDao(accountBox);
                accountEntriesRef.document(USER).collection("entries").add(accEntrybox).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            Map<String, Object> map= new HashMap<>();
                            map.put("array", FieldValue.arrayUnion(task.getResult().getId()));
                            byDateAccEntriesRef.document(USER).collection(date).document("account_entries").set(map, SetOptions.merge());
                        }else {
                            Log.i("Status:","db entry is not successful");
                        }
                    }
                });

                Toast.makeText(AccountEntryActivity.this,"Entry Saved",Toast.LENGTH_SHORT).show();
                finish();
            }

        });
        alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        alertDialog2.show();
//        addAccountEntry.setOnClickListener(this);


    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

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



    public String nextWord(String str)
    {

        // if string is empty
        if (str == "")
            return "a";

        // Find first character from
        // right which is not z.
        int i = str.length() - 1;
        while (i >= 0 && str.charAt(i) == 'z')
            i--;

        // If all characters are 'z',
        // append an 'a' at the end.
        if (i == -1)
            str = str + 'a';

        else
            str = str.substring(0, i) +
                    (char)((int)(str.charAt(i)) + 1) +
                    str.substring(i + 1);
        return str;
    }


}