package com.journalapp.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.journalapp.R;
import com.journalapp.models.ExpenseBoxDao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseRecyclerViewAdapter extends RecyclerView.Adapter<ExpenseRecyclerViewAdapter.EntryHolder> {

    ArrayList<ExpenseBoxDao> entries;
    Context context;

    @NonNull
    @Override
    public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_new_expense_box, parent, false);
        EntryHolder pvh = new EntryHolder(v);
        return pvh;
    }

    public ExpenseRecyclerViewAdapter(final Context context, final ArrayList<ExpenseBoxDao> entries) {
        this.entries = entries;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull final EntryHolder holder, final int position) {
        final ExpenseBoxDao expenseBoxDao = entries.get(position);
        holder.getDateField().setText(expenseBoxDao.getDate());
        holder.getTimeField().setText(expenseBoxDao.getTime());

        holder.itemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                expenseBoxDao.setItemName(holder.itemName.getText().toString());
                entries.get(holder.getAdapterPosition()).setItemName(holder.itemName.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        holder.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                expenseBoxDao.setDesc(holder.description.getText().toString());
                entries.get(holder.getAdapterPosition()).setDesc(holder.description.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        holder.amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!holder.amount.getText().toString().equals("")) {
                    expenseBoxDao.setAmount(Integer.parseInt(holder.amount.getText().toString()));
                    entries.get(holder.getAdapterPosition()).setAmount(Integer.parseInt(holder.amount.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        holder.getItemName().setText(expenseBoxDao.getItemName());
        holder.getAmount().setText(String.valueOf(expenseBoxDao.getAmount()));
        holder.getDescription().setText(expenseBoxDao.getDesc());

        holder.deleteExpenseButton.setOnClickListener(view -> {
            entries.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
        });

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

    public void addNewData() {
        ExpenseBoxDao expenseboxDao = new ExpenseBoxDao();
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat, timeFormat;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeFormat = new SimpleDateFormat("hh:mm:ss a");
        expenseboxDao.setDate(dateFormat.format(calendar.getTime()));
        expenseboxDao.setTime(timeFormat.format(calendar.getTime()));
        expenseboxDao.setTimeStampp(calendar.getTime());
        expenseboxDao.setItemName("");
        expenseboxDao.setAmount(0);
        expenseboxDao.setDesc("");
        entries.add(expenseboxDao);
        notifyItemInserted(getItemCount() - 1);
    }

    public static class EntryHolder extends RecyclerView.ViewHolder {
        MaterialCardView cv;
        TextView dateField;
        TextView timeField;
        EditText itemName;
        EditText amount;
        EditText description;
        ImageButton deleteExpenseButton;

        public EntryHolder(final View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.expense_card_view);
            dateField = itemView.findViewById(R.id.expense_date_field);
            timeField = itemView.findViewById(R.id.expense_time_field);
            itemName = itemView.findViewById(R.id.edt_item_name);
            amount = itemView.findViewById(R.id.expense_amount);
            description = itemView.findViewById(R.id.expense_desc);
            deleteExpenseButton = itemView.findViewById(R.id.btn_delete_expense_entry);

        }

        public MaterialCardView getCv() {
            return cv;
        }

        public ImageButton getDeleteExpenseButton() {
            return deleteExpenseButton;
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
