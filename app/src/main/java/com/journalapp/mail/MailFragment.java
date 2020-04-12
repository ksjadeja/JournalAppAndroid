package com.journalapp.mail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.journalapp.R;

public class MailFragment extends Fragment {

    Button btnMail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mail,container,false);
        btnMail = root.findViewById(R.id.btn_send_mail);

        btnMail.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                    Log.i("SendMail", "inside onClick");
                    GMailSender sender = new GMailSender("ksjadeja2812.2017@gmail.com", "28122017@kj");
                    sender.sendMail("Return my money(Just testing hardcoded)",
                            "Hello Mr. Kiran, we would like to inform you on behalf of Mr. Kuldeepsinh that you have to return him an amount of " +
                                    "Rs. 20 which you borrowed from him for eating Vadapav. Please return the money as soon as possible" +
                                    ".\n Regards,\n Team JournalApp.",
                            "ksjadeja2812.2017@gmail.com",
                            "kiran.italiya19@gmail.com");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
                    }
                }).start();
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
