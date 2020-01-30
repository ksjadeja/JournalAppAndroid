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
import com.journalapp.R;
import com.journalapp.TimelineViewPad;
import com.journalapp.models.Feedbox;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.EntryHolder>{
        ArrayList<Feedbox> entries;
    Context context;
    @NonNull
    @Override
    public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedbox_layout, parent, false);
        EntryHolder pvh = new EntryHolder(v);
        return pvh;
    }

    public RecyclerViewAdapter(Context context, ArrayList<Feedbox> persons){
        this.entries = persons;
        this.context=context;
    }

    @Override
    public void onBindViewHolder(@NonNull final EntryHolder holder, final int position) {
        holder.dateField.setText(entries.get(position).getDate());
        holder.timeField.setText(entries.get(position).getTime());
        holder.dataField.setText(entries.get(position).getData());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Selection position : "+ entries.get(position).getDate(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, TimelineViewPad.class);
                intent.putExtra("dateField",holder.getDateField().getText());
                intent.putExtra("time",holder.getTimeField().getText());
                intent.putExtra("data",holder.getDataField().getText());
                context.startActivity(intent);
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
        TextView dataField;
        String id;

        public EntryHolder(final View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.card_view);
            dateField = itemView.findViewById(R.id.dateField);
            timeField = itemView.findViewById(R.id.timeField);
            dataField = itemView.findViewById(R.id.dataField);
        }

        public TextView getDateField() {
            return dateField;
        }

        public TextView getTimeField() {
            return timeField;
        }

        public TextView getDataField() {
            return dataField;
        }
    }

}
