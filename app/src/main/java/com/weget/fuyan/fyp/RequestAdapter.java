package com.weget.fuyan.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FU YAN on 7/11/2016.
 */
public class RequestAdapter extends ArrayAdapter {
    List list = new ArrayList();

    public RequestAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class DataHandler{
        ImageView requestImage;
        TextView requestTitle;
        TextView requestRequirement;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = convertView;
        DataHandler handler;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_layout,parent,false);
            handler = new DataHandler();
            handler.requestImage = (ImageView)row.findViewById(R.id.request_image);
            handler.requestTitle = (TextView)row.findViewById(R.id.request_title);
            handler.requestRequirement = (TextView)row.findViewById(R.id.request_requirement);
            row.setTag(handler);
        }else{
            handler = (DataHandler)row.getTag();
        }
        Request request;
        request = (Request) this.getItem(position);
        handler.requestImage.setImageResource(request.getImageResource());
        handler.requestTitle.setText(request.getProductName());
        handler.requestRequirement.setText(request.getRequirement());

        return row;
    }

    public void clear()
    {
        super.clear();
        list.clear();
    }
}
