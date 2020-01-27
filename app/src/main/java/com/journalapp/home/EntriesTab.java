package com.journalapp.home;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.utils.FeedboxListAdapter;
import com.journalapp.utils.RecyclerViewAdapter;

import java.util.ArrayList;

//public class EntriesTab extends RecyclerView.Adapter<EntriesTab.PersonViewHolder>{
//
//    ArrayList<Feedbox> persons;
//    @NonNull
//    @Override
//    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedbox_layout, parent, false);
//        PersonViewHolder pvh = new PersonViewHolder(v);
//        return pvh;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
//        holder.date.setText(persons.get(position).getDate());
//        holder.timeOfDay.setText(persons.get(position).getTime());
//        holder.contentData.setText(persons.get(position).getData());
//    }
//
//    @Override
//    public int getItemCount() {
//        return persons.size();
//    }
//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//    }
//    public EntriesTab(ArrayList<Feedbox> persons){
//        this.persons = persons;
//    }
//    public static class PersonViewHolder extends RecyclerView.ViewHolder {
//        MaterialCardView cv;
//        TextView date;
//        TextView timeOfDay;
//        TextView contentData;
//
//
//
//        PersonViewHolder(View itemView) {
//            super(itemView);
//            cv =  itemView.findViewById(R.id.card_view);
//            date = itemView.findViewById(R.id.date);
//            timeOfDay = itemView.findViewById(R.id.time_of_day);
//            contentData = itemView.findViewById(R.id.content_data);
//
//        }
//    }
//
//}
/**
 * A simple {@link Fragment} subclass.
 */
public class EntriesTab extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Feedbox> feedboxesList;

    public EntriesTab() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        recyclerView=entriesView.findViewById(R.id.recycler_view);

        feedboxesList = new ArrayList<>();
        Feedbox Feedbox;
        for (int i=0;i<15;i++)
        {
            Feedbox = new Feedbox();
            Feedbox.setDate("date"+i);
            Feedbox.setTime("time"+i);
            Feedbox.setData("Descr"+i);

            feedboxesList.add(Feedbox);

        }
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
//        FeedboxListAdapter feedboxListAdapter = new FeedboxListAdapter(recyclerView.getContext(),feedboxesList);
//        recyclerView.setAdapter(feedboxListAdapter);
        RecyclerViewAdapter adapter= new RecyclerViewAdapter(getContext(),feedboxesList);
        recyclerView.setAdapter(adapter);
        return entriesView;
    }

}
