package com.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.journalapp.AccEntriesMap.AccEntriesIndex;

public class AccountEntryActivity extends AppCompatActivity{

    TextView accountEntryDate;
    TextView accountEntryTime;
    EditText edtAmount,description;
    AutoCompleteTextView edtName;
    ListenerRegistration liveAccountEntries;
    ArrayList<String> accountNameList=new ArrayList<>();

    public static SimpleDateFormat dateFormat, timeFormat;
    String date, time;
    private View myView;
    String user;
    String USER= "Kiran1901";
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
    CollectionReference byDateAccEntriesRef = FirebaseFirestore.getInstance().collection("by_date");
    ArrayAdapter adapter;
    private int t_type=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_entry);
        LayoutInflater layoutInflater = getLayoutInflater();
        myView = layoutInflater.inflate(R.layout.layout_account_entries, null, false);
        user = "Kiran1901";
        accountEntryDate = myView.findViewById(R.id.account_entry_date);
        accountEntryTime = myView.findViewById(R.id.account_entry_time);
        edtName = myView.findViewById(R.id.edt_person_name);
        edtAmount = myView.findViewById(R.id.edt_amount);
        description = myView.findViewById(R.id.edt_desc);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item,accountNameList);


        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        timeFormat = new SimpleDateFormat("hh:mm:ss a");

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(AccountEntryActivity.this);
        if(myView.getParent() != null) {
            ((ViewGroup)myView.getParent()).removeView(myView); // <- fix
        }
        alertDialog2.setView(myView);
        fillUserSeggestions();
        edtName.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        edtName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(AccountEntryActivity.this, "Selected Item is: \t" + item, Toast.LENGTH_LONG).show();
            }
        });
        Calendar c = Calendar.getInstance();
        date = dateFormat.format(c.getTime());
        time = timeFormat.format(c.getTime());

//                edtName = myView.findViewById(R.id.edt_person_name);


                //                        accountEntriesRef.document(USER).collection("entries")
//                                .whereGreaterThan("name",edtName.getText().toString())
//                                .whereLessThan("name",nextWord(edtName.getText().toString())).get());



        accountEntryDate.setText(date);
        accountEntryTime.setText(time);
        alertDialog2.setPositiveButton("Add Account Entry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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
//                            byDateAccEntriesRef.document(USER).collection(date).document("account_entries").set(map, SetOptions.merge());
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

    private void fillUserSeggestions() {
        AccEntriesMap.clearMap();
        liveAccountEntries = accountEntriesRef.document(USER).collection("entries").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }
                int i=0;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    String key=null;
                    AccountBoxDao accountBoxDao= null;
                    switch (dc.getType()) {
                        case ADDED:
                            key = dc.getDocument().getId();
                            Log.i("CntAAA:",(i++)+":::"+key);

                            accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                            Log.i("CntAAA:",(i++)+":::"+accountBoxDao.getName());
                            accountNameList.add(accountBoxDao.getName());
                            AccEntriesMap.addFirst(key);
                            adapter.notifyDataSetChanged();
                            break;

                        case MODIFIED:
                            key = dc.getDocument().getId();
                            accountBoxDao= dc.getDocument().toObject(AccountBoxDao.class);
                            int index = AccEntriesIndex.get(key);
                            Log.i("CntAAA:",(i++)+"Modified :::"+accountBoxDao.getName()+"index  "+index);
                            Log.i("CntAAA:",(i++)+"Old :::"+accountNameList.get(index)+"index  "+index);
                            accountNameList.set(index,accountBoxDao.getName());
                            Log.i("CntAAA:",(i++)+"Modified :::"+accountNameList.get(index)+"index  "+index);
                            adapter.notifyDataSetChanged();
                            break;

                        case REMOVED:
                            for(String ac:accountNameList){
                                if(AccEntriesMap.isKeyPresent(dc.getDocument().getId())){
                                    AccEntriesMap.delete(dc.getDocument().getId(),accountNameList.indexOf(ac));
                                    accountNameList.remove(ac);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            break;
                    }
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liveAccountEntries.remove();
    }
}