package com.journalapp.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        Feedbox Feedbox;
        for (int i=0;i<5;i++)
        {
            Feedbox = new Feedbox();
            Feedbox.setDate("date"+i);
            Feedbox.setTime("time"+i);
            Feedbox.setData("Descr"+i);

            feedboxesList.add(Feedbox);

        }

        FeedboxListAdapter feedboxListAdapter = new FeedboxListAdapter(feedboxListView.getContext(),feedboxesList);
        feedboxListView.setAdapter(feedboxListAdapter);

        return entriesView;
    }

}
