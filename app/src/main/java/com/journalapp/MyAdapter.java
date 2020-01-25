package com.journalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class MyAdapter extends BaseAdapter {

    Context context;
    ArrayList<MyBean> beanArrayList;
    public MyAdapter(Context context, ArrayList<MyBean> beanArrayList) {
            this.context=context;
            this.beanArrayList=beanArrayList;
    }

    @Override
    public int getCount() {
        return beanArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return beanArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.raw_custom_list,null);

        ImageView imageView = (ImageView)view.findViewById(R.id.circle_image_view);
        TextView textView = (TextView)view.findViewById(R.id.tv_data);
//        getItem(i);
        imageView.setImageResource(beanArrayList.get(i).getImage());
        textView.setText(beanArrayList.get(i).getText());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Selection is "+beanArrayList.get(i).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
