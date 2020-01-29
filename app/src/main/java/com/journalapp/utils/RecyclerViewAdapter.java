package com.journalapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.journalapp.R;
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
    public void onBindViewHolder(@NonNull EntryHolder holder, final int position) {
        holder.date.setText(entries.get(position).getDate());
        holder.timeOfDay.setText(entries.get(position).getTime());
        holder.contentData.setText(entries.get(position).getData());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Selection position : "+ entries.get(position).getDate(),Toast.LENGTH_LONG).show();
                //TODO open with new view pad activity;
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
                TextView date;
        TextView timeOfDay;
        TextView contentData;

        public EntryHolder(final View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.card_view);
            date = itemView.findViewById(R.id.dateField);
            timeOfDay = itemView.findViewById(R.id.timeField);
            contentData = itemView.findViewById(R.id.dataField);


        }
    }

}
