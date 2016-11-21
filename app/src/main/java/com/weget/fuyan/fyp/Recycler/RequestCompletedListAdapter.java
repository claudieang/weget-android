package com.weget.fuyan.fyp.Recycler;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weget.fuyan.fyp.R;
import com.weget.fuyan.fyp.Request;
import com.weget.fuyan.fyp.Util.DateFormatter;

import java.util.List;

/**
 * Created by Claudie on 9/18/16.
 */
public class RequestCompletedListAdapter  extends RecyclerView.Adapter<RequestCompletedListAdapter.MyViewHolder>{

    private List<Request> requestsList;
    private String origin;


    public RequestCompletedListAdapter(List<Request> requestsList, String origin) {
        this.requestsList = requestsList;
        this.origin = origin;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, details, date, price;
        //public Button fulfiller_btn;
        //public RelativeLayout fulfillers_btn;
        //public View.OnClickListener mListener;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            date = (TextView) view.findViewById(R.id.request_date);
            price = (TextView)view.findViewById(R.id.price);
            //fulfiller_btn = (Button) view.findViewById(R.id.view_fulfill_btn);
            //fulfillers_btn = (RelativeLayout)view.findViewById(R.id.fulfillers_btn);
            //view.setOnClickListener(this);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_requests, parent, false);
        TextView b1 = (TextView) itemView.findViewById(R.id.request_title);
        TextView b2 = (TextView) itemView.findViewById(R.id.request_requirement);
        TextView b3 = (TextView) itemView.findViewById(R.id.request_date);
        Typeface typeFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Bold.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-LightItalic.ttf");
        Typeface typeFaceRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        b1.setTypeface(typeFace);
        b2.setTypeface(typeFaceLight);
        b3.setTypeface(typeFaceRegular);




        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)  {

        if(origin.equals("fulfill")){
            Request request = requestsList.get(position);
            holder.title.setText(request.getProductName());
            holder.details.setText(request.getRequirement());
            holder.date.setText(DateFormatter.formatDateShort(request.getEndTime()));
            holder.price.setText("$ " + String.format("%.2f",(request.getPrice()-(request.getPrice()*0.029 + 0.3))));
        }else {
            Request request = requestsList.get(position);
            holder.title.setText(request.getProductName());
            holder.details.setText(request.getRequirement());
            holder.date.setText(DateFormatter.formatDateShort(request.getEndTime()));
            holder.price.setText("$ " + String.format("%.2f", request.getPrice()));
        }
        //final int idCheck = holder.fulfillers_btn.getId();
        //holder.fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);

    }

}
