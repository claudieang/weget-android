package com.wegot.fuyan.fyp.Recycler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.wegot.fuyan.fyp.Account;
import com.wegot.fuyan.fyp.CompletedRequest;
import com.wegot.fuyan.fyp.CreateRequestActivity;
import com.wegot.fuyan.fyp.MainActivity;
import com.wegot.fuyan.fyp.MyfulfillDetails;
import com.wegot.fuyan.fyp.PaymentActivity;
import com.wegot.fuyan.fyp.PendingdetailsFulfiller;
import com.wegot.fuyan.fyp.PendingdetailsRequester;
import com.wegot.fuyan.fyp.R;
import com.wegot.fuyan.fyp.Request;
import com.wegot.fuyan.fyp.RequesterViewDetails;
import com.wegot.fuyan.fyp.UtilHttp;

/**
 * Created by Claudie on 9/8/16.
 */
public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder>{

    private List<Request> requestsList;
    Context mContext;
    int rId, myId, fr;
    String username, password, authString, err;


    public RequestListAdapter(List<Request> requestsList, int lol) {
        this.requestsList = requestsList;
        fr = lol;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, details;
        ImageView receivedIV;
        //public Button fulfiller_btn;
        //public RelativeLayout fulfillers_btn;
        //public View.OnClickListener mListener;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            receivedIV = (ImageView)view.findViewById(R.id.confirm_received_button);
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

            receivedIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fr == 0) {
                        new AlertDialog.Builder(v.getContext())
                                .setTitle("Alert!")
                                .setMessage("Confirm received order?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        Request request = requestsList.get(getAdapterPosition());
                                        rId = request.getId();
                                        new doReceive().execute(authString);


                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }else if(fr==1){

                        new AlertDialog.Builder(v.getContext())
                                .setTitle("Alert!")
                                .setMessage("Confirm delivered order??")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        Request request = requestsList.get(getAdapterPosition());
                                        rId = request.getId();
                                        new doDeliver().execute(authString);


                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();

                    }
                }
            });

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_list_layout, parent, false);
        TextView b1 = (TextView) itemView.findViewById(R.id.request_title);
        TextView b2 = (TextView) itemView.findViewById(R.id.request_requirement);
        Typeface typeFace=Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Black.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-LightItalic.ttf");
        b1.setTypeface(typeFace);
        b2.setTypeface(typeFaceLight);
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
        //final int idCheck = holder.fulfillers_btn.getId();
        //holder.fulfillers_btn = (RelativeLayout)itemView.findViewById(R.id.fulfillers_btn);

    }

    private class doReceive extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog = new ProgressDialog(mContext, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            if(!((Activity)mContext).isFinishing()) {
                dialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/transaction/" + rId+"/received/";

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result) {
                Intent i = new Intent(mContext, MainActivity.class);
                i.putExtra("after_received_tab", 1);
                i.putExtra("complete_request_swipe", 2);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Toast.makeText(mContext, "Received!", Toast.LENGTH_SHORT).show();
                mContext.startActivity(i);
                //((Activity)mContext).finish();

            }else{

                Toast.makeText(mContext, err, Toast.LENGTH_SHORT).show();
            }

        }


    }

    private class doDeliver extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(mContext, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            if(!((Activity)mContext).isFinishing()) {
                dialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/transaction/" + rId+"/delivered/";

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result) {
                Intent i = new Intent(mContext, MainActivity.class);
                i.putExtra("after_delivered_tab", 3);
                i.putExtra("complete_fulfill_swipe",2);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Toast.makeText(mContext, "Delivered!", Toast.LENGTH_SHORT).show();
                mContext.startActivity(i);
                //((Activity)mContext).finish();

            }else{

                Toast.makeText(mContext, err, Toast.LENGTH_SHORT).show();
            }

        }


    }


}




