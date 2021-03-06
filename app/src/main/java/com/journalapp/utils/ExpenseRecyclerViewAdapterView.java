package com.journalapp.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.EntriesViewPad;
import com.journalapp.ExpenseEntryEditActivity;
import com.journalapp.R;
import com.journalapp.models.ExpenseBox;
import com.journalapp.models.Feedbox;

import java.util.ArrayList;

public class ExpenseRecyclerViewAdapterView extends RecyclerView.Adapter<ExpenseRecyclerViewAdapterView.EntryHolder>{

    ArrayList<ExpenseBox> entries;
    Context context;
    View myView;
    String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";
    CollectionReference expenseEntriesRef = FirebaseFirestore.getInstance().collection("expense_entries");

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
    }

    @Override
    public void onBindViewHolder(@NonNull final EntryHolder holder, final int position) {
        holder.dateField.setText(entries.get(position).getDate());
        holder.timeField.setText(entries.get(position).getTime());
        holder.itemName.setText(entries.get(position).getItemName());
        holder.amount.setText(String.valueOf(entries.get(position).getAmount()));
        holder.description.setText(entries.get(position).getDesc());

        holder.itemView.setOnLongClickListener(v -> {
            Intent intent = new Intent(context, ExpenseEntryEditActivity.class);
            ExpenseBox expensebox = new ExpenseBox();
            expensebox.setId(entries.get(holder.getAdapterPosition()).getId());
            intent.putExtra("expensebox",entries.get(holder.getAdapterPosition()));
            context.startActivity(intent);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class EntryHolder extends RecyclerView.ViewHolder {
        MaterialCardView cv;
        TextView dateField;
        TextView timeField;
        TextView itemName;
        TextView amount;
        TextView description;
        String id;

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
