package com.wegot.fuyan.fyp.Recycler;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wegot.fuyan.fyp.CreateRequestActivity;
import com.wegot.fuyan.fyp.MyRequestFulfillerActivity;
import com.wegot.fuyan.fyp.R;
import com.wegot.fuyan.fyp.Request;
import com.wegot.fuyan.fyp.RequestDetailsActivity;
import com.wegot.fuyan.fyp.RequesterViewDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claudie on 9/10/16.
 */
public class RequestActiveListAdapter extends RecyclerView.Adapter<RequestActiveListAdapter.MyActiveViewHolder>{

    private List<Request> requestsList;


    public RequestActiveListAdapter(List<Request> requestsList) {
        this.requestsList = requestsList;
    }

    public class MyActiveViewHolder extends RecyclerView.ViewHolder{
        public TextView title, details;
        //public Button fulfiller_btn;
        public RelativeLayout fulfillers_btn;
        //public View.OnClickListener mListener;


        public MyActiveViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            //fulfiller_btn = (Button) view.findViewById(R.id.view_fulfill_btn);
            fulfillers_btn = (RelativeLayout)view.findViewById(R.id.fulfillers_btn);
            //view.setOnClickListener(this);
            View viewById = view.findViewById(R.id.cv2);
            viewById.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Request request = requestsList.get(getAdapterPosition());
                    //Toast.makeText(view.getContext(),"RV clicked " + view.getId() + ", " + R.id.cv2, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), RequesterViewDetails.class);
                    intent.putExtra("selected_request",(Serializable) request);
                    view.getContext().startActivity(intent);

                }
            });

            fulfillers_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //Intent i = new Intent();

//                    Intent intent = new Intent(v.getContext(),CreateRequestActivity.class);
//                    v.getContext().startActivity(intent);
//                    Toast.makeText(v.getContext(),"Fulfiller button clicked", Toast.LENGTH_SHORT).show();

                    Request rq = requestsList.get(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), MyRequestFulfillerActivity.class);
                    intent.putExtra("selected_my_request",(Serializable) rq);
                    v.getContext().startActivity(intent);

                }
            });
        }


    }

    @Override
    public MyActiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_list_layout, parent, false);
        TextView b1 = (TextView) itemView.findViewById(R.id.request_title);
        TextView b2 = (TextView) itemView.findViewById(R.id.request_requirement);
        Typeface typeFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Bold.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Italic.ttf");
        b1.setTypeface(typeFace);
        b2.setTypeface(typeFaceLight);
        RelativeLayout fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);

        return new MyActiveViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    @Override
    public void onBindViewHolder(MyActiveViewHolder holder, int position)  {
        final Request request = requestsList.get(position);
        holder.title.setText(request.getProductName());
        holder.details.setText(request.getRequirement());
        //final int idCheck = holder.fulfillers_btn.getId();
        //holder.fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);



//        ArrayList<String> fulfillList = new ArrayList<String>();
//        //if /request/id/fulfill/ return empty array
//        if(fulfillList.isEmpty()) {
//            holder.fulfiller_btn.setVisibility(View.GONE);
//        } else{
//            holder.fulfiller_btn.setVisibility(View.VISIBLE);
//        }
    }

}

