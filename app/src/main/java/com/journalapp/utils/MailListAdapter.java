package com.journalapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journalapp.R;
import com.journalapp.models.MailBean;

import java.util.ArrayList;
import java.util.Objects;

public class MailListAdapter extends BaseAdapter {

    private Context context;
    private String USER = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();           //"Kiran1901";
    private ArrayList<MailBean> mailList;
    private CollectionReference mailRef = FirebaseFirestore.getInstance().collection("mailing_list");
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

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        view = layoutInflater.inflate(R.layout.layout_mail_entry,null,false);
        TextView personName = view.findViewById(R.id.tv_person_name);
        final EditText email = view.findViewById(R.id.edt_mail_id);
        final ImageButton editButton = view.findViewById(R.id.editEntryButton);
        final ImageButton saveButton = view.findViewById(R.id.saveEntryButton);
        final MaterialCardView saveCard = view.findViewById(R.id.saveCard);
        final MaterialCardView editCard = view.findViewById(R.id.editCard);
        personName.setText(mailList.get(i).getPersonName());
        email.setText(mailList.get(i).getEmail());

        editButton.setOnClickListener(view1 -> {
            editButton.setVisibility(View.GONE);
            editCard.setVisibility(View.GONE);
            saveCard.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);

            email.setEnabled(true);
            email.setInputType(InputType.TYPE_CLASS_TEXT);

            mailList.get(i).setEmail(email.getText().toString());
            mailList.get(i).setEmailEntered(false);

        });
        saveButton.setOnClickListener(view12 -> {
            editButton.setVisibility(View.VISIBLE);
            editCard.setVisibility(View.VISIBLE);
            saveCard.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);

            String mail = email.getText().toString();
            mailList.get(i).setEmail(mail);
            mailList.get(i).setEmailEntered(true);

            email.setEnabled(false);
            email.setInputType(InputType.TYPE_NULL);

            mailRef.document(USER).collection("entries").document(mailList.get(i).getKey()).set(mailList.get(i));
        });
        if(mailList.get(i).getEmailEntered())
        {
            email.setEnabled(false);
            email.setInputType(InputType.TYPE_NULL);

            saveButton.setVisibility(View.GONE);
            saveCard.setVisibility(View.GONE);
            editCard.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
        }else{
            email.setEnabled(true);
            email.setInputType(InputType.TYPE_CLASS_TEXT);

            saveCard.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
            editCard.setVisibility(View.GONE);
        }
        return view;
    }
}