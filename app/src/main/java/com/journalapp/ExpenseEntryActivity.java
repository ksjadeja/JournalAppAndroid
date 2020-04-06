package com.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;
import com.journalapp.utils.ExpenseRecyclerViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExpenseEntryActivity extends AppCompatActivity {

    Button addExpenseButton;
    Button expSaveButton;
    RecyclerView expenseRecyclerView;
    ExpenseRecyclerViewAdapter expenseRecyclerViewAdapter;
    ArrayList<ExpenseBoxDao> expenseList;
    String USER= "Kiran1901";
    CollectionReference expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");
    ListenerRegistration liveAccountEntries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry);
        expenseList = new ArrayList<>();
       addExpenseButton = findViewById(R.id.btn_add_expense_entry);
       expSaveButton = findViewById(R.id.exp_btn_save);
       expenseRecyclerView = findViewById(R.id.expense_recycler_view);

        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(ExpenseEntryActivity.this));
        expenseRecyclerViewAdapter = new ExpenseRecyclerViewAdapter(ExpenseEntryActivity.this,expenseList);
        expenseRecyclerView.setAdapter(expenseRecyclerViewAdapter);
        expenseRecyclerViewAdapter.addNewData();

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               expenseRecyclerViewAdapter.addNewData();
           }
       });

//        LayoutInflater layoutInflater = getLayoutInflater();
//        View view = layoutInflater.inflate(R.layout.layout_expense_box,null,false);

        expSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(ExpenseEntryActivity.this, "items " + expenseList.get(expenseList.size() - 1).getItemName(), Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(ExpenseEntryActivity.this, "Sorry, No items to add", Toast.LENGTH_SHORT).show();
                }
                for(final ExpenseBoxDao expBoxDao:expenseList)
                {

                    expenseEntriesRef.document(USER).collection("entries").add(expBoxDao).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Log.i("Status:","db entry is successful");
//                                Map<String, Object> map= new HashMap<>();
//                                map.put("array", FieldValue.arrayUnion(task.getResult().getId()));
//                                String ddate = expBoxDao.getDate();
//                                ddate = ddate.replaceAll("/","-");
                            }else {
                                Log.i("Status:","db entry is not successful");
                            }
                        }
                    });
                }
                finish();
            }
        });

    }
}
