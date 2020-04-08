package com.journalapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.journalapp.R;
import com.journalapp.models.AccountBoxDao;

import java.util.ArrayList;

public class AccEntriesListAdapter extends BaseAdapter {

    Context context;
    ArrayList<AccountBoxDao> accEntryboxArrayList;

    @Override
    public int getCount() {
        return accEntryboxArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return accEntryboxArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        layoutInflater.inflate(R.layout.fragment_home_acc_entries,null);



        return null;
    }
}
