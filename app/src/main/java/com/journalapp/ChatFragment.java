package com.journalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    ListView listView;
    String text[] = {"Apple","Google","Tesla","Java","C","Python","PHP","Spring","LinkedIn"};
    int image[]={R.drawable.ic_apple,R.drawable.ic_google,R.drawable.ic_tesla,R.drawable.ic_java,R.drawable.ic_c,R.drawable.ic_python,R.drawable.ic_php,R.drawable.spring_boot2,R.drawable.linkedin};
    ArrayList<MyBean> beanArrayList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat,container,false);
        listView = rootView.findViewById(R.id.list_view_custom2);
        beanArrayList = new ArrayList<>();
        for (int i=0;i<text.length;i++)
        {
            MyBean myBean = new MyBean();
            myBean.setText(text[i]);
            myBean.setImage(image[i]);

            beanArrayList.add(myBean);

        }

        MyAdapter myAdapter = new MyAdapter(rootView.getContext(),beanArrayList);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.raw_list_view,R.id.tv_list);
//        arrayAdapter.addAll(str);

        listView.setAdapter(myAdapter);

        return rootView;
    }
}
