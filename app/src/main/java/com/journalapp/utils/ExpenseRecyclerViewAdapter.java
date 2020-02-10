package com.journalapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.journalapp.R;

import com.journalapp.models.ExpenseBox;
import com.journalapp.models.ExpenseBoxDao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseRecyclerViewAdapter extends RecyclerView.Adapter<ExpenseRecyclerViewAdapter.EntryHolder>{

    ArrayList<ExpenseBoxDao> entries;
    Context context;
//    DatabaseReference entriesDb= FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901");

    @NonNull
    @Override
    public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_box, parent, false);
        EntryHolder pvh = new EntryHolder(v);
        return pvh;
    }

    public ExpenseRecyclerViewAdapter(final Context context, final ArrayList<ExpenseBoxDao> entries){
        this.entries = entries;
        this.context=context;
//        if(entries.size()==0)
//        {
//            Toast.makeText(context, "list is empty", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Toast.makeText(context, "list is not empty", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull final EntryHolder holder, final int position) {
//        holder.setIsRecyclable(false);
//        holder.cv.removeAllViews();
        
//        String dt = "12/12/2020";
//        boolean isNew=true;
//        isNew = holder.getDateField().getText().equals(dt)?true:false;
        Toast.makeText(context, "onBindBefore"+holder.getDateField().getText(), Toast.LENGTH_SHORT).show();
        holder.getDateField().setText(entries.get(position).getDate());
        Toast.makeText(context, "onBindAfter"+holder.getDateField().getText(), Toast.LENGTH_SHORT).show();
        holder.getTimeField().setText(entries.get(position).getTime());
//        if(isNew) {
//            holder.getItemName().setText("");
//            holder.getAmount().setText("");
//            holder.getDescription().setText("");
//        }else{
//            holder.getItemName().setText(entries.get(position).getItemName());
//            holder.getAmount().setText(entries.get(position).getAmount());
//            holder.getDescription().setText(entries.get(position).getDesc());
//        }
        holder.deleteExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entries.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), entries.size());

            }
        });
//        holder.itemName.setText(entries.get(position).getItemName());
//        holder.amount.setText(entries.get(position).getAmount());
//        holder.description.setText(entries.get(position).getDesc());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"Selection position : "+ entries.get(position).getDate(),Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(context, TimelineViewPad.class);
//                intent.putExtra("dateField",holder.getDateField().getText());
//                intent.putExtra("time",holder.getTimeField().getText());
//                intent.putExtra("data",holder.getItemName().getText());
//                intent.putExtra("id", entries.get(holder.getAdapterPosition()).getId());
//                context.startActivity(intent);
//            }
//        });
    }



    @Override
    public int getItemCount() {
        return entries.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        entries.clear();
    }

    public void addNewData(){
        ExpenseBoxDao expenseboxDao = new ExpenseBoxDao();
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat,timeFormat;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeFormat = new SimpleDateFormat("hh:mm:ss a");
//        Toast.makeText(context, " "+expenseboxDao, Toast.LENGTH_SHORT).show();
        expenseboxDao.setDate(dateFormat.format(calendar.getTime()));
        expenseboxDao.setTime(timeFormat.format(calendar.getTime()));
        expenseboxDao.setItemName("");
        expenseboxDao.setAmount("");
        expenseboxDao.setDesc("");

        entries.add(expenseboxDao);
        notifyDataSetChanged();
//        notifyItemInserted(entries.size()-1);
//        notifyItemRangeChanged(entries.size()-1,entries.size());
    }


    public static class EntryHolder extends RecyclerView.ViewHolder {
        MaterialCardView cv;
        TextView dateField;
        TextView timeField;
        EditText itemName;
        EditText amount;
        EditText description;
        Button deleteExpenseButton;

        public EntryHolder(final View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.expense_card_view);
            dateField = itemView.findViewById(R.id.expense_date_field);
            timeField = itemView.findViewById(R.id.expense_time_field);
            itemName = itemView.findViewById(R.id.edt_item_name);
            amount = itemView.findViewById(R.id.expense_amount);
            description= itemView.findViewById(R.id.expense_desc);
            deleteExpenseButton = itemView.findViewById(R.id.btn_delete_expense_entry);
        }


        public TextView getDateField() {
            return dateField;
        }
        public TextView getTimeField() {
            return timeField;
        }

        public EditText getItemName() {
            return itemName;
        }

        public EditText getAmount() {
            return amount;
        }

        public EditText getDescription() {
            return description;
        }
    }
}
