package com.journalapp.contact_us;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.journalapp.R;


public class ContactUsFragment extends Fragment {

    TextView support_email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        support_email = view.findViewById(R.id.support_email);
        support_email.setMovementMethod(LinkMovementMethod.getInstance());
        support_email.setText(Html.fromHtml(getString(R.string.app_support_email)));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Contact Us");
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Contact Us");
    }
}
