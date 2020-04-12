package com.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import static com.google.firebase.firestore.Query.*;
import static com.journalapp.AccEntriesMap.AccEntriesIndex;

public class AccountEntryEditActivity extends AppCompatActivity{

    TextView dateText;
    TextView timeText;
    EditText amountText,descText;
    AutoCompleteTextView nameText;
    MaterialButton discard_btn,save_btn;
    MaterialRadioButton giveRadio,takeRadio;
    ImageButton delete_btn;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

    ListenerRegistration liveAccountEntries;
    ArrayList<String> accountNameList=new ArrayList<>();
    ArrayAdapter adapter;


    String USER= "Kiran1901";
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");

    AccountBox accountBox;

    boolean update = false;
    private int t_type=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_pad);

        dateText = findViewById(R.id.account_entry_date);
        timeText = findViewById(R.id.account_entry_time);
        nameText = findViewById(R.id.account_entry_name);
        amountText = findViewById(R.id.account_entry_amount);
        descText = findViewById(R.id.account_entry_desc);
        giveRadio = findViewById(R.id.radio_category_give);
        takeRadio = findViewById(R.id.radio_category_take);
        discard_btn = findViewById(R.id.discard_button_account_entry_dialog);
        save_btn = findViewById(R.id.save_button_account_entry_dialog);
        delete_btn = findViewById(R.id.deleteEntryButton);

        Intent intent = getIntent();
        if(intent.hasExtra("accountbox")){
            accountBox = ((AccountBox) intent.getSerializableExtra("accountbox"));
            dateText.setText(accountBox.getDate());
            timeText.setText(accountBox.getTime());
            nameText.setText(accountBox.getName());
            amountText.setText(String.valueOf(accountBox.getAmount()));
            if(accountBox.getT_type().equals("0")){
                t_type=0;
                giveRadio.setChecked(true);
                takeRadio.setChecked(false);
            }else if(accountBox.getT_type().equals("1")){
                t_type=1;
                takeRadio.setChecked(true);
                giveRadio.setChecked(false);
            }
            descText.setText(accountBox.getDesc());
            update = true;
        }else{

            Calendar c = Calendar.getInstance();
            accountBox = new AccountBox();
            accountBox.setTimestamp(new Date());
            accountBox.setDate(dateFormat.format(c.getTime()));
            accountBox.setTime(timeFormat.format(c.getTime()));
            dateText.setText(accountBox.getDate());
            timeText.setText(accountBox.getTime());
            update=false;
        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(update){
                    updateEntry();
                }else {
                    saveEntry();
                }

            }
        });

        discard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item,accountNameList);
        fillUserSeggestions();
        nameText.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        nameText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(AccountEntryEditActivity.this, "Selected Item is: \t" + item, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((MaterialRadioButton) view).isChecked();

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
    public void onBackPressed() {
        if(isChanged()){
            AlertDialog.Builder saveAlert = new AlertDialog.Builder(AccountEntryEditActivity.this);
            saveAlert.setTitle("Do you want to save?");
            saveAlert.setCancelable(false);
            saveAlert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveEntry();
                }
            });
            saveAlert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            saveAlert.show();
        }else {
            finish();
        }
    }

    private void saveEntry(){

        if(validateInput()){

            accountBox.setName(nameText.getText().toString());
            accountBox.setAmount(Integer.parseInt(amountText.getText().toString()));
            accountBox.setDesc(descText.getText().toString());
            accountBox.setT_type(String.valueOf(t_type));
            accountBox.setDate(dateText.getText().toString());
            accountBox.setTime(timeText.getText().toString());
            AccountBoxDao accEntrybox = new AccountBoxDao(accountBox);
            accountEntriesRef.document(USER).collection("entries").add(accEntrybox).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AccountEntryEditActivity.this,"Entry Saved..",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.i("Status:","db entry is not successful");
                    }
                }
            });
            finish();
        }
    }

    private void updateEntry() {

        if(validateInput()){
            accountBox.setName(nameText.getText().toString());
            accountBox.setAmount(Integer.parseInt(amountText.getText().toString()));
            accountBox.setDesc(descText.getText().toString());
            accountBox.setT_type(String.valueOf(t_type));
            accountBox.setDate(dateText.getText().toString());
            accountBox.setTime(timeText.getText().toString());
            AccountBoxDao accEntryDao = new AccountBoxDao(accountBox);
            accountEntriesRef.document(USER).collection("entries").document(accountBox.getId()).set(accEntryDao).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AccountEntryEditActivity.this,"Entry Updated..",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.i("Status:","db acc_entry update is not successful");
                    }
                }
            });
            finish();
        }
    }

    private boolean validateInput(){

        String textPattern = "[a-zA-Z\"\']+[ a-zA-Z0-9()/\"\'+-_]*";
        String numberPattern = "[0-9.]+";

        if(Pattern.matches(textPattern,nameText.getText().toString()) &&
                Pattern.matches(numberPattern,amountText.getText().toString()) &&
                t_type != -1){
            return true;
        }
        Toast.makeText(AccountEntryEditActivity.this,"Insert appropriate details..",Toast.LENGTH_LONG).show();
        return false;
    }

    private void deleteEntry(){
        final AlertDialog.Builder saveAlert = new AlertDialog.Builder(AccountEntryEditActivity.this);
        saveAlert.setTitle("Do you really want to Delete?");
        saveAlert.setCancelable(false);
        saveAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                accountEntriesRef.document(USER).collection("entries").document(accountBox.getId()).delete();
                AccEntriesMap.delete(accountBox.getId(), AccEntriesIndex.get(accountBox.getId()));
                finish();
            }
        });
        saveAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        saveAlert.show();

    }

    private boolean isChanged(){
        if(update){
            if(nameText.getText().toString().equals(accountBox.getName()) &&
                    amountText.getText().toString().equals(String.valueOf(accountBox.getAmount())) &&
                    t_type==Integer.parseInt(accountBox.getT_type()) &&
                    descText.getText().toString().equals(accountBox.getDesc())){
                return false;
            }
        }
        return true;
    }

    private void fillUserSeggestions() {
        AccEntriesMap.clearMap();
        liveAccountEntries = accountEntriesRef.document(USER).collection("entries").orderBy("timestamp", Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

}