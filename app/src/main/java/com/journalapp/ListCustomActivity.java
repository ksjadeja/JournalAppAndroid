package com.journalapp;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListCustomActivity extends AppCompatActivity {

    ListView listView;
    String text[] = {"Apple","Google","Tesla","Java","C","Python","PHP","Spring","LinkedIn"};
    int image[]={R.drawable.ic_apple,R.drawable.ic_google,R.drawable.ic_tesla,R.drawable.ic_java,R.drawable.ic_c,R.drawable.ic_python,R.drawable.ic_php,R.drawable.spring_boot2,R.drawable.linkedin};
    ArrayList<MyBean> beanArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_custom);

        listView = findViewById(R.id.list_view_custom);
        beanArrayList = new ArrayList<>();
        for (int i=0;i<text.length;i++)
        {
            MyBean myBean = new MyBean();
            myBean.setText(text[i]);
            myBean.setImage(image[i]);

            beanArrayList.add(myBean);

        }

        MyAdapter myAdapter = new MyAdapter(this,beanArrayList);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.raw_list_view,R.id.tv_list);
//        arrayAdapter.addAll(str);

        listView.setAdapter(myAdapter);
    }
}
