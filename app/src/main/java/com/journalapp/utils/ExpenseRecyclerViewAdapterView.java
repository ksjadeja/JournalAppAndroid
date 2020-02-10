package com.journalapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.ExpenseBox;

import java.util.ArrayList;

public class ExpenseRecyclerViewAdapterView extends RecyclerView.Adapter<ExpenseRecyclerViewAdapterView.EntryHolder>{

    ArrayList<ExpenseBox> entries;
    Context context;
//    DatabaseReference entriesDb= FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901");

    @NonNull
    @Override
    public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_entry_box, parent, false);
        EntryHolder pvh = new EntryHolder(v);
        return pvh;
    }

    public ExpenseRecyclerViewAdapterView(final Context context, final ArrayList<ExpenseBox> entries){
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
        holder.dateField.setText(entries.get(position).getDate());
        holder.timeField.setText(entries.get(position).getTime());
        holder.itemName.setText(entries.get(position).getItemName());
        holder.amount.setText(entries.get(position).getAmount());
        holder.description.setText(entries.get(position).getDesc());
    }



    @Override
    public int getItemCount() {
        return entries.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

//    public void addNewData(){
//        ExpenseBox expensebox = new ExpenseBox();
//        Calendar calendar = Calendar.getInstance();
//        DateFormat dateFormat,timeFormat;
//        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        timeFormat = new SimpleDateFormat("hh:mm:ss a");
//        Toast.makeText(context, "date is "+dateFormat.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
//        expensebox.setDate(dateFormat.format(calendar.getTime()));
//        expensebox.setTime(timeFormat.format(calendar.getTime()));
//        expensebox.setItemName("");
//        expensebox.setAmount("");
//        expensebox.setDesc("");
//
//        entries.add(expensebox);
//        notifyDataSetChanged();
//    }


    public static class EntryHolder extends RecyclerView.ViewHolder {
        MaterialCardView cv;
        TextView dateField;
        TextView timeField;
        TextView itemName;
        TextView amount;
        TextView description;
//        String id;

        public EntryHolder(final View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.expense_card_view2);
            dateField = itemView.findViewById(R.id.expense_entry_date2);
            timeField = itemView.findViewById(R.id.expense_entry_time2);
            itemName = itemView.findViewById(R.id.exp_tv_item_name);
            amount = itemView.findViewById(R.id.exp_tv_amount);
            description= itemView.findViewById(R.id.exp_tv_desc);
        }


        public TextView getDateField() {
            return dateField;
        }
        public TextView getTimeField() {
            return timeField;
        }

        public TextView getItemName() {
            return itemName;
        }

        public TextView getAmount() {
            return amount;
        }

        public TextView getDescription() {
            return description;
        }
    }
}