package com.wegot.fuyan.fyp.Recycler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.wegot.fuyan.fyp.CreateRequestActivity;
import com.wegot.fuyan.fyp.R;
import com.wegot.fuyan.fyp.Request;

/**
 * Created by Claudie on 9/8/16.
 */
public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder>{

    private List<Request> requestsList;


    public RequestListAdapter(List<Request> requestsList) {
        this.requestsList = requestsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, details;
        public Button fulfiller_btn;
        //public RelativeLayout fulfillers_btn;
        //public View.OnClickListener mListener;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            fulfiller_btn = (Button) view.findViewById(R.id.view_fulfill_btn);
            //fulfillers_btn = (RelativeLayout)view.findViewById(R.id.fulfillers_btn);
            //view.setOnClickListener(this);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_list_layout, parent, false);
        TextView b1 = (TextView) itemView.findViewById(R.id.request_title);
        TextView b2 = (TextView) itemView.findViewById(R.id.request_requirement);
        Typeface typeFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Light.ttf");
        b1.setTypeface(typeFace);
        b2.setTypeface(typeFaceLight);



        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)  {
        Request request = requestsList.get(position);
        holder.title.setText(request.getProductName());
        holder.details.setText(request.getRequirement());
        //final int idCheck = holder.fulfillers_btn.getId();
        //holder.fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);



        ArrayList<String> fulfillList = new ArrayList<String>();
        //if /request/id/fulfill/ return empty array
        if(fulfillList.isEmpty()) {
            holder.fulfiller_btn.setVisibility(View.GONE);
        } else{
            holder.fulfiller_btn.setVisibility(View.VISIBLE);
        }
    }

}
