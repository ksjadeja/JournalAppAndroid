package com.journalapp.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.journalapp.R;
import com.journalapp.models.MailBean;

import java.util.ArrayList;

public class MailListAdapter extends BaseAdapter {

    Context context;
    ArrayList<MailBean> mailList;
    public MailListAdapter(Context context, ArrayList<MailBean> mailList) {
        this.context=context;
        this.mailList=mailList;
    }

    @Override
    public int getCount() {
        return mailList.size();
    }

    @Override
    public Object getItem(int i) {
        return mailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.mail_entry_layout,null);
        TextView personName = view.findViewById(R.id.tv_person_name);
        EditText email = view.findViewById(R.id.edt_mail_id);

//        getItem(i);
        personName.setText(mailList.get(i).getPersonName());
        Log.i("MailAA","name "+personName.getText());
        Log.i("MailAA","name "+mailList.get(i).getEmailEntered());
        email.setText(mailList.get(i).getEmail());
        if(mailList.get(i).getEmailEntered())
        {
            email.setFocusable(false);
            email.setClickable(false);
//            email.setFocusableInTouchMode(false);
        }
        else{
//            email.setClickable(true);
//            email.setFocusable(true);
        }
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "Selection is "+beanArrayList.get(i).getText(), Toast.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }
}
