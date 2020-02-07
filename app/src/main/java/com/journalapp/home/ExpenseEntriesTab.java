package com.journalapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.journalapp.R;

public class ExpenseEntriesTab extends Fragment {

        public ExpenseEntriesTab (){
    // Required empty public constructor

}


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_home_expense_entries, container, false);
        }
}
