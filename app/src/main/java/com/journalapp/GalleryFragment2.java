package com.journalapp;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

//import java.util.Calendar;

public class GalleryFragment2 extends Fragment {
//    int day,month,year,hour,minute;
    DatePicker datePicker;
    TextView textView;
//    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_gallery,container,false);

       datePicker = rootView.findViewById(R.id.datePicker);
       textView = rootView.findViewById(R.id.text_gallery);
    datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
    @Override
    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
        String date="Date is "+i2+"-"+i1+1+"-"+i;
        Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
        textView.setText(date);
    }
});


//        minute= calendar.get(Calendar.MINUTE);
//        hour= calendar.get(Calendar.HOUR_OF_DAY);
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Gallery");
    }
}
