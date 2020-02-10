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

import java.util.ArrayList;

public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.EntryHolder>{

    ArrayList<AccountBox> entries;
    Context context;
//    DatabaseReference entriesDb= FirebaseDatabase.getInstance().getReference("journal_entries").child("Kiran1901");

    @NonNull
    @Override
    public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_account_entry_box, parent, false);
        EntryHolder pvh = new EntryHolder(v);
        return pvh;
    }

    public AccountRecyclerViewAdapter(final Context context, final ArrayList<AccountBox> entries){
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
        holder.personName.setText(entries.get(position).getName());
        holder.amount.setText(entries.get(position).getAmount());
        holder.description.setText(entries.get(position).getDesc());
        int x=Integer.parseInt(entries.get(position).getT_type());
        if(x==0)
        {
            holder.radioGive.setChecked(true);
        }else{
            holder.radioTake.setChecked(true);
        }
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
        TextView personName;
        TextView amount;
        TextView description;
//        RadioGroup radioGroup;
        RadioButton radioGive;
        RadioButton radioTake;
//        String id;

        public EntryHolder(final View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.account_card_view);
            dateField = itemView.findViewById(R.id.account_entry_date2);
            timeField = itemView.findViewById(R.id.account_entry_time2);
            personName = itemView.findViewById(R.id.acc_tv_person_name);
            amount = itemView.findViewById(R.id.acc_tv_amount);
            description= itemView.findViewById(R.id.acc_tv_desc);
//            radioGroup = itemView.findViewById(R.id.acc_radio_category);
            radioGive  = itemView.findViewById(R.id.acc_radio_category_give);
            radioTake  = itemView.findViewById(R.id.acc_radio_category_take);
        }


        public TextView getDateField() {
            return dateField;
        }
        public TextView getTimeField() {
            return timeField;
        }

        public TextView getPersonName() {
            return personName;
        }

        public TextView getAmount() {
            return amount;
        }

        public TextView getDescription() {
            return description;
        }
    }
}
