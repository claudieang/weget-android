package com.weget.fuyan.fyp.Recycler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weget.fuyan.fyp.CompletedRequest;
import com.weget.fuyan.fyp.MyfulfillDetails;
import com.weget.fuyan.fyp.PendingdetailsFulfiller;
import com.weget.fuyan.fyp.PendingdetailsRequester;
import com.weget.fuyan.fyp.R;
import com.weget.fuyan.fyp.Request;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Claudie on 9/19/16.
 */
public class FulfillActiveListAdapter  extends RecyclerView.Adapter<FulfillActiveListAdapter.MyViewHolder>{

    private List<Request> requestsList;
    Context mContext;
    int rId, myId, fr;
    String username, password, authString, err;


    public FulfillActiveListAdapter(List<Request> requestsList, int lol) {
        this.requestsList = requestsList;
        fr = lol;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, details, price;
        //ImageView receivedIV;
        //public Button fulfiller_btn;
        //public RelativeLayout fulfillers_btn;
        //public View.OnClickListener mListener;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            //receivedIV = (ImageView)view.findViewById(R.id.confirm_received_button);
            price= (TextView)view.findViewById(R.id.request_price);
            //fulfiller_btn = (Button) view.findViewById(R.id.view_fulfill_btn);
            //fulfillers_btn = (RelativeLayout)view.findViewById(R.id.fulfillers_btn);
            //view.setOnClickListener(this);

            SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0);
            username = pref.getString("username", null);
            password = pref.getString("password", null);
            myId = pref.getInt ("id", 0);
            authString  = username + ":" + password;


            View viewById = view.findViewById(R.id.cv);
            viewById.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(fr == 0) {

                        //Log.d("lol============", ""+requestsList.size());
                        Request rq = requestsList.get(getAdapterPosition());
                        Intent intent = new Intent(mContext, PendingdetailsRequester.class);
                        intent.putExtra("selected_my_pending_request", (Serializable) rq);
                        mContext.startActivity(intent);
                    }else if(fr==1){

                        Request rq = requestsList.get(getAdapterPosition());
                        Intent intent = new Intent(mContext, PendingdetailsFulfiller.class);
                        intent.putExtra("selected_my_pending_fulfill", (Serializable) rq);
                        mContext.startActivity(intent);

                    }else if(fr==2){
                        Request rq = requestsList.get(getAdapterPosition());
                        Intent intent = new Intent(mContext, MyfulfillDetails.class);
                        intent.putExtra("selected_fulfill_request", (Serializable) rq);
                        mContext.startActivity(intent);

                    }else{

                        Request rq = requestsList.get(getAdapterPosition());
                        Intent intent = new Intent(mContext, CompletedRequest.class);
                        intent.putExtra("completed_request",(Serializable) rq);
                        mContext.startActivity(intent);
                    }
                }
            });


        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_fulfill_layout, parent, false);
        TextView b1 = (TextView) itemView.findViewById(R.id.request_title);
        TextView b2 = (TextView) itemView.findViewById(R.id.request_requirement);
        TextView b3 = (TextView) itemView.findViewById(R.id.request_price);
        Typeface typeFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Black.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-LightItalic.ttf");
        b1.setTypeface(typeFace);
        b2.setTypeface(typeFaceLight);
        b3.setTypeface(typeFace);
        mContext = parent.getContext();



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
        holder.price.setText("$"+ request.getPrice() + "0");
        //final int idCheck = holder.fulfillers_btn.getId();
        //holder.fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);

    }



}