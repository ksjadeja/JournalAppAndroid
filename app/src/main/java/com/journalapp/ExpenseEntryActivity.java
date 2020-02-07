package com.journalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.journalapp.models.Expensebox;
import com.journalapp.utils.ExpenseRecyclerViewAdapter;

import java.util.ArrayList;

public class ExpenseEntryActivity extends AppCompatActivity {

    Button addExpenseButton;

    RecyclerView expenseRecyclerView;
    ExpenseRecyclerViewAdapter expenseRecyclerViewAdapter;
    ArrayList<Expensebox> expenseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_entry);
        expenseList = new ArrayList<>();
       addExpenseButton = findViewById(R.id.btn_add_expense_entry);
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

        LayoutInflater layoutInflater = getLayoutInflater();
        layoutInflater.inflate(R.layout.layout_expense_box,null,false);

    }
}
