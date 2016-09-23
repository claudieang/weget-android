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

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
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
import com.wegot.fuyan.fyp.UserChatActivity;
import com.wegot.fuyan.fyp.UtilHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Claudie on 9/8/16.
 */
public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder>{

    private List<Request> requestsList;
    Context mContext;
    int rId, myId, fr, fId, requstorId;
    String username, password, authString, err;
    ArrayList<Account> fulfillerAccountList = new ArrayList<>();


    public RequestListAdapter(List<Request> requestsList, int lol) {
        this.requestsList = requestsList;
        fr = lol;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, details;
        ImageView receivedIV,chatBtn;
        //public Button fulfiller_btn;
        //public RelativeLayout fulfillers_btn;
        //public View.OnClickListener mListener;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.request_title);
            details = (TextView) view.findViewById(R.id.request_requirement);
            receivedIV = (ImageView)view.findViewById(R.id.confirm_received_button);
            chatBtn = (ImageView)view.findViewById(R.id.chat_button);
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

            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Request request = requestsList.get(getAdapterPosition());
                    rId = request.getId();
                    requstorId = request.getRequestorId();

                    new getMyRequestFulfiller().execute(authString);

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
                Toast.makeText(mContext, "Delivered!", Toast.LENGTH_SHORT).show();
                mContext.startActivity(i);
                //((Activity)mContext).finish();

            }else{

                Toast.makeText(mContext, err, Toast.LENGTH_SHORT).show();
            }

        }


    }

    private class getMyRequestFulfiller extends AsyncTask<String, Void, Boolean> {

        int id, contactNo;
        String username, password, email, fulfiller, picture;
        Account account;

        @Override
        protected void onPreExecute() {



        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            Log.d("lol1", "rId issss : " + rId);
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/" + rId +"/fulfillers/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                fulfillerAccountList.clear();

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for(int i = 0; i < jsoArray.length(); i++) {
                        JSONObject jso = jsoArray.getJSONObject(i);

                        id = jso.getInt("id");
                        username = jso.getString("username");
                        password = jso.getString("password");
                        contactNo = jso.getInt("contactNo");
                        email = jso.getString("email");
                        fulfiller = jso.getString("fulfiller");
                        picture = jso.getString("picture");

                        account = new Account(id, username, password, contactNo, email, fulfiller, picture);


                        fulfillerAccountList.add(account);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                success = true;
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("lol1", "Result issss : " + result);
            if(result) {
                Log.d("lol1", "fulfillerAccountList.size() issss : " + fulfillerAccountList.size());
                if(fulfillerAccountList.size() == 1){
                    Account a = fulfillerAccountList.get(0);
                    fId = a.getId();


                }

            }



            List<String> userIds = new ArrayList<>();

            userIds.add(myId+"");
            userIds.add(fId+"");
            GroupChannelListQuery mQuery = GroupChannel.createMyGroupChannelListQuery();
            if(mQuery == null){
                GroupChannel.createChannelWithUserIds(userIds, true, new GroupChannel.GroupChannelCreateHandler() {
                    @Override
                    public void onResult(GroupChannel groupChannel, SendBirdException e) {
                        if(e != null) {
                            Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(mContext, UserChatActivity.class);
                        intent.putExtra("channel_url", groupChannel.getUrl());
                        mContext.startActivity(intent);
                    }
                });
            } else{

                Log.d("geo1","isit we dunnid create a group?");

                mQuery.setIncludeEmpty(true);
                mQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
                    @Override
                    public void onResult(List<GroupChannel> list, SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean haveMyId = false;
                        boolean imFulfilling = false;
                        boolean imRequesting = false;
                        int membercount = 0;

                        if((myId+"").equals(requstorId+"")){
                            imRequesting = true;

                        } else {
                            imFulfilling = true;
                        }

                        for (GroupChannel gc: list){
                            List<User> uList = gc.getMembers();
                            for (User u: uList){
                                Log.d("geo1","NOWNOWNOW THE ID IS : " + u.getUserId());
                                Log.d("geo1","ACTUAL Id now is : " + myId);
                                if (u.getUserId().equals(myId+"")){
                                    membercount++;
                                    haveMyId = true;
                                }

                                if (u.getUserId().equals(fId+"")){
                                    membercount++;
                                }

                            }


                            if (membercount == gc.getMemberCount() && haveMyId){

                                Log.d("geo1","isit it goes in here nao?");
                                Intent intent = new Intent(mContext, UserChatActivity.class);
                                intent.putExtra("channel_url", gc.getUrl());
                                mContext.startActivity(intent);

                            }
                                //if im not fulfilling
                            else if (haveMyId && !imFulfilling && imRequesting){
                                Log.d("geo1","isit it goes into else if?");
                                List<String> uIds = new ArrayList<>();
                                uIds.add(myId+"");
                                uIds.add(fId+"");
                                Log.d("geo1","isit it myId is : " + myId);
                                Log.d("geo1","isit it fId is : " + fId);
                                GroupChannel.createChannelWithUserIds(uIds, true, new GroupChannel.GroupChannelCreateHandler() {
                                    @Override
                                    public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                        if(e != null) {
                                            Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Intent intent = new Intent(mContext, UserChatActivity.class);
                                        intent.putExtra("channel_url", groupChannel.getUrl());
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                                //if im fulfillling
                            else if(haveMyId && imFulfilling && !imRequesting){
                                Log.d("geo1","isit it goes into im fulfilling?");
                                List<String> uIds = new ArrayList<>();
                                uIds.add(myId+"");
                                uIds.add(requstorId+"");
                                Log.d("geo1","isit it myId is : " + myId);
                                Log.d("geo1","isit it fId is : " + fId);
                                GroupChannel.createChannelWithUserIds(uIds, true, new GroupChannel.GroupChannelCreateHandler() {
                                    @Override
                                    public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                        if(e != null) {
                                            Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Intent intent = new Intent(mContext, UserChatActivity.class);
                                        intent.putExtra("channel_url", groupChannel.getUrl());
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                            membercount = 0;
                        }
                    }
                });
            }
        }
    }


}




