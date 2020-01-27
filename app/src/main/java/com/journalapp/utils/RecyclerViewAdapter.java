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


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.PersonViewHolder>{
        ArrayList<Feedbox> persons;
    Context context;
    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedbox_layout, parent, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }
    public RecyclerViewAdapter(Context context, ArrayList<Feedbox> persons){
        this.persons = persons;
        this.context=context;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.PersonViewHolder holder, final int position) {
        holder.date.setText(persons.get(position).getDate());
        holder.timeOfDay.setText(persons.get(position).getTime());
        holder.contentData.setText(persons.get(position).getData());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Selection position : "+persons.get(position).getDate(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return persons.size();
    }
        @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cv;
                TextView date;
        TextView timeOfDay;
        TextView contentData;

        PersonViewHolder(final View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.card_view);
            date = itemView.findViewById(R.id.date);
            timeOfDay = itemView.findViewById(R.id.time_of_day);
            contentData = itemView.findViewById(R.id.content_data);


        }
    }

}
