package com.weget.fuyan.fyp.Recycler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weget.fuyan.fyp.MergedRequest;
import com.weget.fuyan.fyp.MyRequestFulfillerActivity;
import com.weget.fuyan.fyp.R;
import com.weget.fuyan.fyp.Request;
import com.weget.fuyan.fyp.RequesterViewDetails;
import com.weget.fuyan.fyp.Util.DateFormatter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Claudie on 9/10/16.
 */
public class RequestActiveListAdapter extends RecyclerView.Adapter<RequestActiveListAdapter.MyActiveViewHolder>{

    private List<MergedRequest> mergedList;
    private List<Request> requestsList;
    private List<Integer> counterList;
    Context mContext;

//    public RequestActiveListAdapter(List <Request> requestsList, List <Integer> counterList) {
//        this.requestsList = requestsList;
//        this.counterList = counterList;
//    }

    public RequestActiveListAdapter(List<MergedRequest> mergedList){
        this.mergedList = mergedList;
    }
    public class MyActiveViewHolder extends RecyclerView.ViewHolder{
        public TextView title, details, fulfillerNum, price;
        //public Button fulfiller_btn;
        public RelativeLayout fulfillers_btn;
        //public View.OnClickListener mListener;


        public MyActiveViewHolder(View view) {
            super(view);

            price = (TextView) view.findViewById(R.id.price);
            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            //fulfiller_btn = (Button) view.findViewById(R.id.view_fulfill_btn);
            fulfillers_btn = (RelativeLayout)view.findViewById(R.id.fulfillers_btn);
            fulfillerNum = (TextView) view.findViewById(R.id.fulfiller_number);
            //view.setOnClickListener(this);
            View viewById = view.findViewById(R.id.cv2);
            viewById.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MergedRequest request = mergedList.get(getAdapterPosition());
                    //Request request = requestsList.get(getAdapterPosition());
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

                    //Request rq = requestsList.get(getAdapterPosition());
                    MergedRequest rq = mergedList.get(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), MyRequestFulfillerActivity.class);
                    intent.putExtra("selected_my_request",(Serializable) rq);
                    v.getContext().startActivity(intent);
                    //((Activity)mContext).finish();
                    //a.finish();

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
        TextView b3 = (TextView) itemView.findViewById(R.id.price);
        Typeface typeFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Bold.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Italic.ttf");
        b1.setTypeface(typeFace);
        b3.setTypeface(typeFace);
        b2.setTypeface(typeFaceLight);
        RelativeLayout fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);
        mContext = parent.getContext();

        return new MyActiveViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mergedList.size();
        //return requestsList.size();
    }

    @Override
    public void onBindViewHolder(MyActiveViewHolder holder, int position)  {
        //final Request request = requestsList.get(position);
        MergedRequest request = mergedList.get(position);
        String priceS = String.format("%.2f", request.getPrice());
        holder.title.setText(request.getProductName());
        String expiryStr = "Expires on: " + DateFormatter.formatDate(request.getEndTime());
        holder.details.setText(expiryStr);
        //holder.fulfillerNum.setText(String.valueOf(counterList.get(position)));
        holder.fulfillerNum.setText(request.getFulfillerCount() + "");
        //Log.d("numberss", counterList.get(position) + "");
        holder.price.setText("$" + priceS);
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

