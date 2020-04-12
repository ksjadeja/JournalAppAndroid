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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class AccountEntryEditActivity extends AppCompatActivity{

    TextView dateText;
    TextView timeText;
    EditText nameText,amountText,descText;
    MaterialButton discard_btn,save_btn;
    MaterialRadioButton giveRadio,takeRadio;
    ImageButton delete_btn;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

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

    public String nextWord(String str){

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
                AccEntriesMap.delete(accountBox.getId(), AccEntriesMap.AccEntriesIndex.get(accountBox.getId()));
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

}