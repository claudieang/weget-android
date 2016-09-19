package com.wegot.fuyan.fyp.Recycler;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wegot.fuyan.fyp.R;
import com.wegot.fuyan.fyp.Request;
import com.wegot.fuyan.fyp.RequesterViewDetails;
import com.wegot.fuyan.fyp.Util.DateFormatter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Claudie on 9/19/16.
 */
public class RequestAllListAdapter extends RecyclerView.Adapter<RequestAllListAdapter.MyActiveViewHolder>{

    private List<Request> requestsList;


    public RequestAllListAdapter(List<Request> requestsList) {
        this.requestsList = requestsList;
    }

    public class MyActiveViewHolder extends RecyclerView.ViewHolder{
        public TextView title, details, price;
        //public Button fulfiller_btn;
        //public View.OnClickListener mListener;


        public MyActiveViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            price = (TextView) view.findViewById(R.id.request_price);
            //fulfiller_btn = (Button) view.findViewById(R.id.view_fulfill_btn);
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
        }


    }

    @Override
    public MyActiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_all_request_layout, parent, false);
        TextView b1 = (TextView) itemView.findViewById(R.id.request_title);
        TextView b2 = (TextView) itemView.findViewById(R.id.request_requirement);
        TextView b3 = (TextView) itemView.findViewById(R.id.request_price);
        Typeface typeFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Bold.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Regular.ttf");
        b1.setTypeface(typeFace);
        b3.setTypeface(typeFace);
        b2.setTypeface(typeFaceLight);
       // RelativeLayout fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);

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
        holder.details.setText("Expires on: " + DateFormatter.formatDate(request.getEndTime()));
        holder.price.setText("$" + request.getPrice() + "0");
    }

}