package com.journalapp.mail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.journalapp.EntriesMap;
import com.journalapp.R;
import com.journalapp.models.Feedbox;
import com.journalapp.models.FeedboxDao;
import com.journalapp.models.MailBean;
import com.journalapp.utils.MailListAdapter;

import java.util.ArrayList;

import static com.journalapp.EntriesMap.EntriesIndex;

public class MailFragment extends Fragment {

    CollectionReference mailRef = FirebaseFirestore.getInstance().collection("mailing_list");
    ListenerRegistration liveMailEntries;
    ListView listView;
    final String USER = FirebaseAuth.getInstance().getCurrentUser().getUid();           //"Kiran1901";
    ArrayList<MailBean> mailBeanArrayList;
//    Button btnMail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mail, container, false);
//        btnMail = root.findViewById(R.id.btn_send_mail);
        listView = root.findViewById(R.id.mail_list);
        mailBeanArrayList = new ArrayList<>();
        final MailListAdapter mailListAdapter = new MailListAdapter(getContext(),mailBeanArrayList);
        listView.setAdapter(mailListAdapter);

        liveMailEntries = mailRef.document(USER).collection("entries").orderBy("personName", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ERROR:", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    String key=null;
                    MailBean mailBean;

                    switch (dc.getType()) {
                        case ADDED:
                            key = dc.getDocument().getId();
                            mailBean = dc.getDocument().toObject(MailBean.class);
                            mailBean.setKey(key);
                            mailBeanArrayList.add(mailBean);
                            mailListAdapter.notifyDataSetChanged();
                            break;

                        case MODIFIED:
                            key = dc.getDocument().getId();
                            mailBean = dc.getDocument().toObject(MailBean.class);
                            int index;
                            for(MailBean x: mailBeanArrayList)
                            {
                                if(x.getKey().equals(key)){
                                    mailBean.setKey(key);
                                    mailBeanArrayList.set(mailBeanArrayList.indexOf(x),mailBean);
                                    break;
                                }

                            }
                            mailListAdapter.notifyDataSetChanged();
                            break;

                        case REMOVED:
                            for(MailBean mb:mailBeanArrayList){            //TODO optimize it futher
                                if(mb.getKey().equals(dc.getDocument().getId())){
                                    mailBeanArrayList.remove(mb);
                                    mailListAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            break;
                    }
                }
            }
        });

        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Mail");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
