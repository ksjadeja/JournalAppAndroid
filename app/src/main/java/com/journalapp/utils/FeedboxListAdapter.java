package com.journalapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.journalapp.R;
import com.journalapp.models.Feedbox;

import java.util.ArrayList;

public class FeedboxListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Feedbox> feedboxArrayList;
    public FeedboxListAdapter(Context context, ArrayList<Feedbox> feedboxArrayList) {
        this.context=context;
        this.feedboxArrayList =feedboxArrayList;
    }

    @Override
    public int getCount() {
        return feedboxArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return feedboxArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.feedbox_layout,null);


        TextView dateText = view.findViewById(R.id.dateField);
        TextView timeText = view.findViewById(R.id.timeField);
        TextView dataText = view.findViewById(R.id.dataField);

        dateText.setText(feedboxArrayList.get(i).getDate());
        timeText.setText(feedboxArrayList.get(i).getTime());
        dataText.setText(feedboxArrayList.get(i).getData());

        return view;
    }
}
