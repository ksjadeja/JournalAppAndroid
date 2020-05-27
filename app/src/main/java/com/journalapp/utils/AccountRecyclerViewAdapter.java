package com.journalapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.AccountEntryEditActivity;
import com.journalapp.EntriesViewPad;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;
import com.journalapp.models.Feedbox;

import java.util.ArrayList;

public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.EntryHolder>{

    ArrayList<AccountBox> entries;
    Context context;
    String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";

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
    }

    @Override
    public void onBindViewHolder(@NonNull final EntryHolder holder, final int position) {
        holder.dateField.setText(entries.get(position).getDate());
        holder.timeField.setText(entries.get(position).getTime());
        holder.personName.setText(entries.get(position).getName());
        holder.amount.setText(Integer.toString(entries.get(position).getAmount()));
        holder.description.setText(entries.get(position).getDesc());
        int x=Integer.parseInt(entries.get(position).getT_type());
        if(x==0)
        {
            holder.type.setText("GIVE");
//            holder.cv.setCardBackgroundColor(Color.rgb(255,146,146));
            holder.cv.setCardBackgroundColor(Color.rgb(255,100,100));
        }else{
            holder.type.setText("TAKE");
            holder.cv.setCardBackgroundColor(Color.rgb(119,221,119));
        }
        holder.itemView.setOnLongClickListener(view -> {
            Toast.makeText(context,"Selection position : "+ entries.get(position).getDate(),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, AccountEntryEditActivity.class);
            AccountBox accountBox = new AccountBox();
            accountBox.setId(entries.get(holder.getAdapterPosition()).getId());
            intent.putExtra("accountbox",entries.get(holder.getAdapterPosition()));
            context.startActivity(intent);
            return  true;
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
        TextView personName;
        TextView amount;
        TextView description;
        TextView type;
//        String id;

        public EntryHolder(final View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.account_card_view);
            dateField = itemView.findViewById(R.id.account_entry_date2);
            timeField = itemView.findViewById(R.id.account_entry_time2);
            personName = itemView.findViewById(R.id.acc_tv_person_name);
            amount = itemView.findViewById(R.id.acc_tv_amount);
            description= itemView.findViewById(R.id.acc_tv_desc);
            type  = itemView.findViewById(R.id.acc_category);

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

        public TextView getType() {
            return type;
        }

        public void setType(TextView type) {
            this.type = type;
        }
    }
}
