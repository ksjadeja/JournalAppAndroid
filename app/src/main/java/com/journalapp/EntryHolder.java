//package com.journalapp;
//
//import android.view.View;
//import android.widget.TextView;
//
//
//import com.google.android.material.card.MaterialCardView;
//
//public class EntryHolder extends View {
//    MaterialCardView cv;
//    TextView dateField;
//    TextView timeField;
//    TextView dataField;
//
//
//    public EntryHolder(final View itemView) {
//        super(itemView);
//        cv =  itemView.findViewById(R.id.card_view);
//        dateField = itemView.findViewById(R.id.dateField);
//        timeField = itemView.findViewById(R.id.timeField);
//        dataField = itemView.findViewById(R.id.dataField);
//
//    }
//
//    public MaterialCardView getCv() {
//        return cv;
//    }
//
//    public void setCv(MaterialCardView cv) {
//        this.cv = cv;
//    }
//
//    public TextView getDateField() {
//        return dateField;
//    }
//
//    public void setDateField(TextView dateField) {
//        this.dateField = dateField;
//    }
//
//    public TextView getTimeField() {
//        return timeField;
//    }
//
//    public void setTimeField(TextView timeField) {
//        this.timeField = timeField;
//    }
//
//    public TextView getDataField() {
//        return dataField;
//    }
//
//    public void setDataField(TextView dataField) {
//        this.dataField = dataField;
//    }
//
//    public void setDate(String date){
//        dateField.setText(date);
//    }
//
//    public void setTime(String time){
//        timeField.setText(time);
//    }
//
//    public void setData(String data){
//        dataField.setText(data);
//    }
//
//}
