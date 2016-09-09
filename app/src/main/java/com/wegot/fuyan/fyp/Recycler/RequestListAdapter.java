package com.wegot.fuyan.fyp.Recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.wegot.fuyan.fyp.R;
import com.wegot.fuyan.fyp.Request;

/**
 * Created by Claudie on 9/8/16.
 */
public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder> {

    private List<Request> requestsList;

    public RequestListAdapter(List<Request> requestsList) {
        this.requestsList = requestsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, details;
        public Button fulfiller_btn;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            fulfiller_btn = (Button) view.findViewById(R.id.view_fulfill_btn);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Request request = requestsList.get(position);
        holder.title.setText(request.getProductName());
        holder.details.setText(request.getRequirement());

        ArrayList<String> fulfillList = new ArrayList<String>();
        //if /request/id/fulfill/ return empty array
        if(fulfillList.isEmpty()) {
            holder.fulfiller_btn.setVisibility(View.GONE);
        } else{
            holder.fulfiller_btn.setVisibility(View.VISIBLE);
        }
    }

}
