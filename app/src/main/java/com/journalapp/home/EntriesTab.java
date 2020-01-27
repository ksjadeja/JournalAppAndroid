package com.journalapp.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.utils.FeedboxListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EntriesTab extends Fragment {

    ListView feedboxListView;
    ArrayList<Feedbox> feedboxesList;

    public EntriesTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View entriesView =  inflater.inflate(R.layout.fragment_home_entries, container, false);
        feedboxListView=entriesView.findViewById(R.id.feedboxListView);

        feedboxesList = new ArrayList<>();
        Feedbox feedbox;
        for (int i=0;i<5;i++)
        {
            feedbox = new Feedbox();
            feedbox.setDate("date"+i);
            feedbox.setTime("time"+i);
            feedbox.setData("Descr"+i);
            feedboxesList.add(feedbox);

        }

        FeedboxListAdapter feedboxListAdapter = new FeedboxListAdapter(feedboxListView.getContext(),feedboxesList);
        feedboxListView.setAdapter(feedboxListAdapter);
        feedboxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO open with new view pad activity;
            }
        });

        return entriesView;
    }

}
