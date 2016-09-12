package com.wegot.fuyan.fyp.Recycler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.wegot.fuyan.fyp.Account;
import com.wegot.fuyan.fyp.R;
import com.wegot.fuyan.fyp.Request;
import com.wegot.fuyan.fyp.RequestFulfillerDetailsActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claudie on 9/11/16.
 */
public class RequestFulfillersListAdapter extends RecyclerView.Adapter<RequestFulfillersListAdapter.FulfillerListViewHolder>{
    List<Account> list;
    List<Request> requestList;
    int fulfillId;


//    public RequestFulfillersListAdapter(List<Account> list, List<Request> requestList, int fulfillId) {
//        this.list = list;
//        this.requestList = requestList;
//        this.fulfillId = fulfillId;
//    }

    public RequestFulfillersListAdapter(List<Account> list){
        this.list= list;
    }

    public class FulfillerListViewHolder extends RecyclerView.ViewHolder{
        ImageView accountImage;
        TextView accountName;
        RatingBar rating;


        public FulfillerListViewHolder(View view) {
            super(view);

            accountImage = (ImageView)view.findViewById(R.id.accountImage);
            accountName = (TextView)view.findViewById(R.id.fulfiller_name);
            rating= (RatingBar)view.findViewById(R.id.ratingBar);
            //view.setOnClickListener(this);

//            View viewById = view.findViewById(R.id.cv2);
//            viewById.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Account account = list.get(getAdapterPosition());
//                    Request request = requestList.get(getAdapterPosition());
//
//                    Intent intent = new Intent(view.getContext(), RequestFulfillerDetailsActivity.class);
//                    intent.putExtra("selected_fulfill_id", fulfillId);
//                    intent.putExtra("selected_fulfiller", (Serializable) account);
//                    intent.putExtra("selected_request_tofulfull", (Serializable) request);
//                    view.getContext().startActivity(intent);
//
//                }
//            });
        }


    }

    @Override
    public FulfillerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fulfiller_row_layout, parent, false);
        TextView b1 = (TextView) itemView.findViewById(R.id.fulfiller_name);
        Typeface typeFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Regular.ttf");
        b1.setTypeface(typeFace);
//        RelativeLayout fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);



        return new FulfillerListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FulfillerListViewHolder holder, int position) {
        Account account = list.get(position);


        byte[] decodeString = Base64.decode(account.getPicture(), Base64.NO_WRAP);
        Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                decodeString, 0, decodeString.length);
        holder.accountImage.setImageBitmap(decodebitmap);
        //holder.accountImage.setBitmap(account.getPicture());
        holder.accountName.setText(account.getUsername());
        holder.rating.setRating((float) 3.5);

        //Log.d("BVH","Name: " + holder.accountName.toString());
    }

    @Override
    public int getItemCount() {
        //Log.d("ITEM COUNT: ", "" + list.size());
        return list.size();
    }
}
