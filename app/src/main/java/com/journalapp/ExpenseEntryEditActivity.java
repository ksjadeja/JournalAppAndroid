package com.journalapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class ExpenseEntryEditActivity extends AppCompatActivity {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
    TextView dateText;
    TextView timeText;
    EditText nameText,amountText,descText;
    MaterialButton discard_btn,save_btn;
    ImageButton delete_btn;
    String USER= FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";
    CollectionReference expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");

    ExpenseBox expenseBox;

    boolean update = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry_edit);

        dateText = findViewById(R.id.expense_entry_date);
        timeText = findViewById(R.id.expense_entry_time);
        nameText = findViewById(R.id.expense_entry_name);
        amountText = findViewById(R.id.expense_entry_amount);
        descText = findViewById(R.id.expense_entry_desc);
        discard_btn = findViewById(R.id.discard_button_expense_entry_dialog);
        save_btn = findViewById(R.id.save_button_expense_entry_dialog);
        delete_btn = findViewById(R.id.deleteEntryButton);

        Intent intent = getIntent();
        if(intent.hasExtra("expensebox")){
            expenseBox = ((ExpenseBox) intent.getSerializableExtra("expensebox"));
            dateText.setText(expenseBox.getDate());
            timeText.setText(expenseBox.getTime());
            nameText.setText(expenseBox.getItemName());
            amountText.setText(String.valueOf(expenseBox.getAmount()));
            descText.setText(expenseBox.getDesc());
            update = true;
        }else{

            Calendar c = Calendar.getInstance();
            expenseBox = new ExpenseBox();
            expenseBox.setTimestamp(new Date());
            expenseBox.setDate(dateFormat.format(c.getTime()));
            expenseBox.setTime(timeFormat.format(c.getTime()));
            dateText.setText(expenseBox.getDate());
            timeText.setText(expenseBox.getTime());
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
    }

    @Override
    public void onBackPressed() {
        if(isChanged()){
            AlertDialog.Builder saveAlert = new AlertDialog.Builder(ExpenseEntryEditActivity.this);
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

            expenseBox.setItemName(nameText.getText().toString().trim());
            expenseBox.setAmount(Integer.parseInt(amountText.getText().toString().trim()));
            expenseBox.setDesc(descText.getText().toString().trim());
            expenseBox.setDate(dateText.getText().toString());
            expenseBox.setTime(timeText.getText().toString());
            ExpenseBoxDao expEntrybox = new ExpenseBoxDao(expenseBox);
            expenseEntriesRef.document(USER).collection("entries").add(expEntrybox).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ExpenseEntryEditActivity.this,"Entry Saved..",Toast.LENGTH_SHORT).show();
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
            expenseBox.setItemName(nameText.getText().toString().trim());
            expenseBox.setAmount(Integer.parseInt(amountText.getText().toString().trim()));
            expenseBox.setDesc(descText.getText().toString().trim());
            expenseBox.setDate(dateText.getText().toString());
            expenseBox.setTime(timeText.getText().toString());
            ExpenseBoxDao expEntryDao = new ExpenseBoxDao(expenseBox);
            expenseEntriesRef.document(USER).collection("entries").document(expenseBox.getId()).set(expEntryDao).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ExpenseEntryEditActivity.this,"Entry Updated..",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.i("Status:","db exp_entry update is not successful");
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
                Pattern.matches(numberPattern,amountText.getText().toString().trim())){
            return true;
        }
        Toast.makeText(ExpenseEntryEditActivity.this,"Insert appropriate details..",Toast.LENGTH_LONG).show();
        return false;
    }

    private void deleteEntry(){
        final AlertDialog.Builder saveAlert = new AlertDialog.Builder(ExpenseEntryEditActivity.this);
        saveAlert.setTitle("Do you really want to Delete?");
        saveAlert.setCancelable(false);
        saveAlert.setPositiveButton("Yes", (dialog, which) -> {
            expenseEntriesRef.document(USER).collection("entries").document(expenseBox.getId()).delete();
            ExpEntriesMap.delete(expenseBox.getId(), ExpEntriesMap.ExpEntriesIndex.get(expenseBox.getId()));
            finish();
        });
        saveAlert.setNegativeButton("No", (dialog, which) -> {
            return;
        });
        saveAlert.show();
    }

    private boolean isChanged(){
        if(update){
            return !nameText.getText().toString().trim().equals(expenseBox.getItemName().trim()) ||
                    !amountText.getText().toString().trim().equals(String.valueOf(expenseBox.getAmount())) ||
                    !descText.getText().toString().trim().equals(expenseBox.getDesc().trim());
        }
        return true;
    }
}
