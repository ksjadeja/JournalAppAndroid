package com.journalapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.models.ExpenseBoxDao;
import com.journalapp.models.FeedboxDao;
import com.journalapp.utils.ExpenseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ExpenseEntryNewActivity extends AppCompatActivity {

    ImageButton addExpenseButton;
    FloatingActionButton expSaveButton;
    RecyclerView expenseRecyclerView;
    ExpenseRecyclerViewAdapter expenseRecyclerViewAdapter;
    ArrayList<ExpenseBoxDao> expenseList;
    String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";
    CollectionReference expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry);
        expenseList = new ArrayList<>();
        addExpenseButton = findViewById(R.id.btn_add_expense_entry);
        expSaveButton = findViewById(R.id.exp_btn_save);
        expenseRecyclerView = findViewById(R.id.expense_recycler_view);

        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(ExpenseEntryNewActivity.this));
        expenseRecyclerViewAdapter = new ExpenseRecyclerViewAdapter(ExpenseEntryNewActivity.this, expenseList);
        expenseRecyclerView.setAdapter(expenseRecyclerViewAdapter);
        expenseRecyclerViewAdapter.addNewData();

        addExpenseButton.setOnClickListener(view -> expenseRecyclerViewAdapter.addNewData());
        expSaveButton.setOnClickListener(view -> saveEntry());
    }

    private boolean validateInput() {

        String textPattern = "[a-zA-Z\"\']+[ a-zA-Z0-9()/\"\'+-_]*";
        String numberPattern = "[0-9.]+";

        for(ExpenseBoxDao exp : expenseList ){
            if (!Pattern.matches(textPattern, exp.getItemName()) ||
                    !Pattern.matches(numberPattern, String.valueOf(exp.getAmount()))) {
                Toast.makeText(ExpenseEntryNewActivity.this, "Insert appropriate details in entry: "+(expenseList.indexOf(exp)+1), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(expenseList.size()>0){
            AlertDialog.Builder saveAlert = new AlertDialog.Builder(ExpenseEntryNewActivity.this);
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

            for (final ExpenseBoxDao expBoxDao : expenseList) {

                expenseEntriesRef.document(USER).collection("entries").add(expBoxDao).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.i("Status:", "db exp entry is successful");
                            finish();
                        } else {
                            Log.i("Status:", "db exp entry is not successful");
                        }
                    }
                });
            }
        }
    }
}
