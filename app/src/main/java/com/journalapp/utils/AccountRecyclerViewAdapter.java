package com.journalapp.utils;

import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.AccountBoxDao;

import java.util.ArrayList;

public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.EntryHolder>{

    ArrayList<AccountBox> entries;
    Context context;
    View myView;
    String USER = "Kiran1901";
    CollectionReference accountEntriesRef = FirebaseFirestore.getInstance().collection("account_entries");

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
        holder.amount.setText(Integer.toString(entries.get(position).getAmount()));
        holder.description.setText(entries.get(position).getDesc());
        int x=Integer.parseInt(entries.get(position).getT_type());
        if(x==0)
        {
            holder.type.setText("GIVE");
        }else{
            holder.type.setText("TAKE");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AccountBox accountBox=entries.get(position);
                final TextView accountEntryDate;
                final TextView accountEntryTime;
                final EditText personName;
                final EditText amount;
                final EditText desc;
                final RadioButton giveRadio;
                final RadioButton takeRadio;
                final String accountBoxKey = entries.get(holder.getAdapterPosition()).getId();

                Toast.makeText(context,"Selection position : "+ entries.get(position).getDate(),Toast.LENGTH_LONG).show();
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myView = layoutInflater.inflate(R.layout.layout_account_entries, null, false);
                accountEntryDate = myView.findViewById(R.id.account_entry_date);
                accountEntryTime = myView.findViewById(R.id.account_entry_time);
                personName = myView.findViewById(R.id.edt_person_name);
                amount = myView.findViewById(R.id.edt_amount);
                desc = myView.findViewById(R.id.edt_desc);
                giveRadio = myView.findViewById(R.id.radio_category_give);
                takeRadio = myView.findViewById(R.id.radio_category_take);
//                giveRadio.setClickable(false);
//                takeRadio.setClickable(false);
                giveRadio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        giveRadio.setChecked(true);
                    }
                });
                takeRadio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takeRadio.setChecked(true);
                    }
                });
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
//                if(myView.getParent() != null) {
//                    ((ViewGroup)myView.getParent()).removeView(myView); // <- fix
//                }
                alertDialog2.setView(myView);
                accountEntryDate.setText(holder.getDateField().getText());
                accountEntryTime.setText(holder.getTimeField().getText());
                personName.setText(holder.getPersonName().getText());
                amount.setText(holder.getAmount().getText());
                desc.setText(holder.getDescription().getText());
                if(holder.type.getText().equals("GIVE")) {
                    giveRadio.setChecked(true);
                }else if(holder.type.getText().equals("TAKE")){
                    takeRadio.setChecked(true);
                }
                alertDialog2.setPositiveButton("Modify Account Entry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                finish();

//                        final AccountBoxDao accEntryBoxDao = new AccountBoxDao();
                        accountBox.setName(personName.getText().toString());
                        try {
                            accountBox.setAmount(Integer.parseInt(amount.getText().toString()));
                        }catch (Exception e)
                        {
                            Toast.makeText(context, "Enter amount in figures only", Toast.LENGTH_SHORT).show();
                        }
                        accountBox.setDesc(desc.getText().toString());
                        int t_type = giveRadio.isChecked()?0:1;
                        accountBox.setT_type(String.valueOf(t_type));
//                        accountBox.setDate(accountEntryDate.getText().toString());
//                        accountBox.setTime(accountEntryTime.getText().toString());
//
//                        accountBox.setTimestamp(new SimpleDateFormat("dd-MM-yyyy"));
                        AccountBoxDao accEntryBoxDao = new AccountBoxDao(accountBox);
                        accountEntriesRef.document(USER).collection("entries").document(accountBoxKey).set(accEntryBoxDao);
                        entries.set(holder.getAdapterPosition(),new AccountBox(accEntryBoxDao,accountBoxKey));
                        notifyDataSetChanged();
//
                        Toast.makeText(context,"Entry Saved",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                alertDialog2.show();
            }
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
