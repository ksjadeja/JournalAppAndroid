package com.journalapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.textview.MaterialTextView;
import com.journalapp.R;
import com.journalapp.models.AccountBox;
import com.journalapp.models.ExpenseBox;

import java.util.ArrayList;

public class CalculateRecyclerViewAdapter<T> extends RecyclerView.Adapter<CalculateRecyclerViewAdapter.Holder>{

    ArrayList<T> list;
    Context context;

    public CalculateRecyclerViewAdapter(final Context context, final ArrayList<T> list){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_calculate_recycler_view, parent, false);
        CalculateRecyclerViewAdapter.Holder pvh = new CalculateRecyclerViewAdapter.Holder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        if(list.get(position) instanceof ExpenseBox){
            holder.date_time.setText( (((ExpenseBox) list.get(position)).getDate() + " " + ((ExpenseBox) list.get(position)).getTime()));
            holder.desc.setText(((ExpenseBox) list.get(position)).getDesc());
            holder.amount.setText(String.valueOf(((ExpenseBox) list.get(position)).getAmount()));
            holder.amount.setTextColor(Color.RED);
        } else if (list.get(position) instanceof AccountBox){
            holder.date_time.setText( (((AccountBox) list.get(position)).getDate() + " " + ((AccountBox) list.get(position)).getTime()));
            holder.desc.setText(((AccountBox) list.get(position)).getDesc());
            holder.amount.setText(String.valueOf(((AccountBox) list.get(position)).getAmount()));
            if(((AccountBox) list.get(position)).getT_type().equals("0")){
                holder.amount.setTextColor(Color.RED);
            }else {
                holder.amount.setTextColor(Color.GREEN);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class Holder extends RecyclerView.ViewHolder {
        MaterialTextView date_time, desc, amount;

        public Holder(final View itemView) {
            super(itemView);
            int radius = 5;
            MaterialCardView dcv =  itemView.findViewById(R.id.card_view);
            dcv.setShapeAppearanceModel(
                    dcv.getShapeAppearanceModel()
                            .toBuilder()
                            .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                            .setTopRightCorner(CornerFamily.ROUNDED,radius)
                            .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                            .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                            .build());
            date_time = itemView.findViewById(R.id.calculate_item_date_time);
            desc = itemView.findViewById(R.id.calculate_item_desc);
            amount = itemView.findViewById(R.id.calculate_item_amount);
        }

        public MaterialTextView getDate_time() {
            return date_time;
        }

        public void setDate_time(MaterialTextView date_time) {
            this.date_time = date_time;
        }

        public MaterialTextView getDesc() {
            return desc;
        }

        public void setDesc(MaterialTextView desc) {
            this.desc = desc;
        }

        public MaterialTextView getAmount() {
            return amount;
        }

        public void setAmount(MaterialTextView amount) {
            this.amount = amount;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
