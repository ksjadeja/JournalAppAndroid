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
import com.journalapp.EntriesViewPad;
import com.journalapp.R;
import com.journalapp.models.Feedbox;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.EntryHolder>{

    ArrayList<Feedbox> entries;
    Context context;

    public void setEntries(ArrayList<Feedbox> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedbox_layout, parent, false);
        EntryHolder pvh = new EntryHolder(v);
        return pvh;
    }

    public RecyclerViewAdapter(final Context context, final ArrayList<Feedbox> entries){
        this.entries = entries;
        this.context=context;


        if(entries.size()==0)
        {
            Toast.makeText(context, "list is empty", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Keep patience...", Toast.LENGTH_SHORT).show();
        }
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
                Intent intent = new Intent(context, EntriesViewPad.class);
                Feedbox feedbox = new Feedbox();
                feedbox.setId(entries.get(holder.getAdapterPosition()).getId());
                intent.putExtra("feedbox",entries.get(holder.getAdapterPosition()));
//                intent.putExtra("dateField",holder.getDateField().getText());
//                intent.putExtra("time",holder.getTimeField().getText());
//                intent.putExtra("data",holder.getDataField().getText());
//                intent.putExtra("id", entries.get(holder.getAdapterPosition()).getId());
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
