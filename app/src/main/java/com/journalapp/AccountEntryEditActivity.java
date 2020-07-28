package com.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.MailBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
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
    String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");
    CollectionReference mailEntriesRef = FirebaseFirestore.getInstance().collection("mailing_list");
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

        save_btn.setOnClickListener(v -> {
            if(update){
                updateEntry();
            }else {
                saveEntry();
            }

        });

        discard_btn.setOnClickListener(v -> finish());
        delete_btn.setOnClickListener(v -> deleteEntry());
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item,accountNameList);
        fillUserSuggestions();
        nameText.setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        nameText.setOnItemClickListener((adapterView, view, i, l) -> {
            String item = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(AccountEntryEditActivity.this, "Selected Item is: \t" + item, Toast.LENGTH_LONG).show();
//            Toast.makeText(AccountEntryEditActivity.this, "Selected Item is: \t" + item, Toast.LENGTH_LONG).show();
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
            saveAlert.setPositiveButton("Save", (dialog, which) -> saveEntry());
            saveAlert.setNegativeButton("Discard", (dialog, which) -> finish());
            saveAlert.show();
        }else {
            finish();
        }
    }

    private void saveEntry(){

        if(validateInput()){

            accountBox.setName(nameText.getText().toString().trim());
            accountBox.setAmount(Integer.parseInt(amountText.getText().toString().trim()));
            accountBox.setDesc(descText.getText().toString().trim());
            accountBox.setT_type(String.valueOf(t_type));
            accountBox.setDate(dateText.getText().toString());
            accountBox.setTime(timeText.getText().toString());
            final AccountBoxDao accEntrybox = new AccountBoxDao(accountBox);
            accountEntriesRef.document(USER).collection("entries").add(accEntrybox).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        MailBean mailBean = new MailBean();
                        String name = accEntrybox.getName();
                        mailBean.setPersonName(name);
                        mailBean.setEmail(null);
                        mailBean.setEmailEntered(false);
                        mailEntriesRef.document(USER).collection("entries").add(mailBean).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful())
                                {
                                    Log.i("Status:", "db mail list entry is successful");
                                }else{
                                    Log.i("Status:", "db mail list entry is not successful");
                                }
                            }
                        });
                    } else {
                        Log.i("Status:", "db acc entry is not successful");
                    }
                }
            });
            finish();
        }
    }

    private void updateEntry() {

        if(validateInput()){
            accountBox.setName(nameText.getText().toString().trim());
            accountBox.setAmount(Integer.parseInt(amountText.getText().toString().trim()));
            accountBox.setDesc(descText.getText().toString().trim());
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

        if(Pattern.matches(textPattern,nameText.getText().toString().trim()) &&
                Pattern.matches(numberPattern,amountText.getText().toString().trim()) &&
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
        saveAlert.setPositiveButton("Yes", (dialog, which) -> {
            accountEntriesRef.document(USER).collection("entries").document(accountBox.getId()).delete();
            AccEntriesMap.delete(accountBox.getId(), AccEntriesIndex.get(accountBox.getId()));
            finish();
        });
        saveAlert.setNegativeButton("No", (dialog, which) -> {
            return;
        });
        saveAlert.show();
    }

    private boolean isChanged(){
        if(update){
            return !nameText.getText().toString().trim().equals(accountBox.getName().trim()) ||
                    !amountText.getText().toString().trim().equals(String.valueOf(accountBox.getAmount())) ||
                    t_type != Integer.parseInt(accountBox.getT_type()) ||
                    !descText.getText().toString().trim().equals(accountBox.getDesc().trim());
        }
        return true;
    }

    private void fillUserSuggestions() {
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
                            accountBoxDao = dc.getDocument().toObject(AccountBoxDao.class);
                            accountNameList.add(accountBoxDao.getName());
                            AccEntriesMap.addFirst(key);
                            adapter.notifyDataSetChanged();
                            break;

                        case MODIFIED:
                            key = dc.getDocument().getId();
                            accountBoxDao= dc.getDocument().toObject(AccountBoxDao.class);
                            int index = AccEntriesIndex.get(key);
                            accountNameList.set(index,accountBoxDao.getName());
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