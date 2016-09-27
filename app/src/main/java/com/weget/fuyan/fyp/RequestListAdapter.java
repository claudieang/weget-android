package com.weget.fuyan.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claudie on 9/8/16.
 */
public class RequestListAdapter extends ArrayAdapter{

    List list = new ArrayList();

    public RequestListAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class DataHandler{
        TextView requestTitle;
        TextView requestRequirement;
        //Button viewFulfillerBtn;
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
            row = inflater.inflate(R.layout.request_list_layout,parent,false);
            handler = new DataHandler();
            handler.requestTitle = (TextView)row.findViewById(R.id.request_title);
            handler.requestRequirement = (TextView)row.findViewById(R.id.request_requirement);
            //handler.viewFulfillerBtn = (Button)row.findViewById(R.id.view_fulfill_btn);
            row.setTag(handler);
        }else{
            handler = (DataHandler)row.getTag();
        }
        Request request;
        request = (Request) this.getItem(position);
        handler.requestTitle.setText(request.getProductName());
        handler.requestRequirement.setText(request.getRequirement());

        //to set see fulfillers button VISIBLE/GONE
        //setButtonVisibility();
        //get /request/id/fulfill/ . Returns array
        //ArrayList<String> fulfillList = new ArrayList<String>();
        //if /request/id/fulfill/ return empty array
//        if(fulfillList.isEmpty()) {
//            handler.viewFulfillerBtn.setVisibility(View.GONE);
//        } else{
//            handler.viewFulfillerBtn.setVisibility(View.VISIBLE);
//        }

        return row;
    }

    public void clear()
    {
        super.clear();
        list.clear();
    }
}
