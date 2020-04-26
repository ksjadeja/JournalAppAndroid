package com.journalapp.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.R;
import com.journalapp.models.MailBean;

import java.util.ArrayList;

public class MailListAdapter extends BaseAdapter {

    Context context;
    String USER = "Kiran1901";
    ArrayList<MailBean> mailList;
    CollectionReference mailRef = FirebaseFirestore.getInstance().collection("mailing_list");
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
        final EditText email = view.findViewById(R.id.edt_mail_id);
        final ImageButton editButton = view.findViewById(R.id.editEntryButton);
        final ImageButton saveButton = view.findViewById(R.id.saveEntryButton);
        final MaterialCardView saveCard = view.findViewById(R.id.saveCard);
        final MaterialCardView editCard = view.findViewById(R.id.editCard);
//        getItem(i);
        personName.setText(mailList.get(i).getPersonName());
        Log.i("MailAA","name "+personName.getText());
        Log.i("MailAA","name "+mailList.get(i).getEmailEntered());
        email.setText(mailList.get(i).getEmail());
        if(mailList.get(i).getEmailEntered())
        {
            email.setClickable(false);
            email.setFocusable(false);
//            email.setEnabled(false);
            saveButton.setVisibility(View.GONE);
            saveCard.setVisibility(View.GONE);
            editCard.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
        }else{
            saveCard.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            editCard.setVisibility(View.GONE);
        }
        if(saveButton.getVisibility()==View.VISIBLE)
        {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Mail Save ","mail "+email.getText().toString());
                    String mail = email.getText().toString();
                    mailList.get(i).setEmail(mail);
                    mailList.get(i).setEmailEntered(true);
                    notifyDataSetChanged();
                    mailRef.document(USER).collection("entries").document(mailList.get(i).getKey()).set(mailList.get(i));
                }
            });
        }
        if(editButton.getVisibility()==View.VISIBLE)
        {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editButton.setVisibility(View.GONE);
                    editCard.setVisibility(View.GONE);
                    saveCard.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    mailList.get(i).setEmail(email.getText().toString());
                    mailList.get(i).setEmailEntered(false);

//                    email.setEnabled(true);
                    email.setFocusableInTouchMode(true);
                    email.setClickable(true);
                    Log.i("Mail Edit","mail "+email.isInEditMode());
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i("Mail Save ","mail "+email.getText().toString());
                            String mail = email.getText().toString();
                            mailList.get(i).setEmail(mail);
                            mailList.get(i).setEmailEntered(true);
//                            notifyDataSetChanged();
                            mailRef.document(USER).collection("entries").document(mailList.get(i).getKey()).set(mailList.get(i));
                            email.setFocusable(false);
                            email.setClickable(false);
                        }
                    });

                }
            });
        }
        return view;
    }
}
