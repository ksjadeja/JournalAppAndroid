package com.journalapp.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.journalapp.models.AccountBoxDao;
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
        Toast.makeText(context, "view OnCreateViewHolder "+(getItemCount()-1), Toast.LENGTH_SHORT).show();
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
//        if (position > getItemCount() - 1 || position==0) {
//            holder.setIsRecyclable(false);
            final ExpenseBoxDao expenseBoxDao = entries.get(position);
            Toast.makeText(context, "onBindBefore "+position+" " + holder.getDateField().getText(), Toast.LENGTH_SHORT).show();
            holder.getDateField().setText(expenseBoxDao.getDate());
            Toast.makeText(context, "onBindAfter" +position+" " + holder.getDateField().getText(), Toast.LENGTH_SHORT).show();
            holder.getTimeField().setText(expenseBoxDao.getTime());
            Toast.makeText(context, "onBindBefore" +position+" name " + holder.getItemName().getText().toString(), Toast.LENGTH_SHORT).show();

            holder.itemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                expenseBoxDao.setItemName(holder.itemName.getText().toString());
                entries.get(holder.getAdapterPosition()).setItemName(holder.itemName.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        holder.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                expenseBoxDao.setDesc(holder.description.getText().toString());
                entries.get(holder.getAdapterPosition()).setDesc(holder.description.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        holder.amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                expenseBoxDao.setAmount(holder.amount.getText().toString());
                entries.get(holder.getAdapterPosition()).setAmount(holder.amount.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
            holder.getItemName().setText(expenseBoxDao.getItemName());
            Toast.makeText(context, "onBindAfter" +position+" name " + expenseBoxDao.getItemName(), Toast.LENGTH_SHORT).show();
            holder.getAmount().setText(expenseBoxDao.getAmount());
            holder.getDescription().setText(expenseBoxDao.getDesc());
//        }
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
//                    holder.setIsRecyclable(true);
                    entries.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
//                    notifyItemRangeRemoved(position, 1);
                    Toast.makeText(context, "Item removed " + (holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            });
//        holder.itemName.setText(entries.get(position).getItemName());
//        holder.amount.setText(entries.get(position).getAmount());
//        holder.description.setText(entries.get(position).getDesc());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"Selection position : "+ entries.get(position).getDate(),Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(context, EntriesViewPad.class);
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
        return entries != null ? entries.size() : 0;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        entries.clear();
        notifyDataSetChanged();
    }

    public void addNewData(){
        Toast.makeText(context, "add start "+getItemCount(), Toast.LENGTH_SHORT).show();
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
        notifyItemInserted(getItemCount()-1);
        Toast.makeText(context, "Item added "+(getItemCount()-1), Toast.LENGTH_SHORT).show();

//        notifyDataSetChanged();

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
//            deleteExpenseButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    holder.setIsRecyclable(true);
//                    entries.remove(getAdapterPosition());
//                    notifyItemRemoved(getAdapterPosition());
//                    notifyItemRangeRemoved(getAdapterPosition(), 1);
//                    Toast.makeText(context, "Item removed " + (getAdapterPosition()), Toast.LENGTH_SHORT).show();
//                }
//            });

        }

        public MaterialCardView getCv() { return cv; }
        public Button getDeleteExpenseButton() { return deleteExpenseButton; }
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
