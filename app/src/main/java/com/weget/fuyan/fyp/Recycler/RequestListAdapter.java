package com.weget.fuyan.fyp.Recycler;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.weget.fuyan.fyp.Account;
import com.weget.fuyan.fyp.CompletedRequest;
import com.weget.fuyan.fyp.MainActivity;
import com.weget.fuyan.fyp.MyfulfillDetails;
import com.weget.fuyan.fyp.PendingdetailsFulfiller;
import com.weget.fuyan.fyp.PendingdetailsRequester;
import com.weget.fuyan.fyp.R;
import com.weget.fuyan.fyp.Request;
import com.weget.fuyan.fyp.UserChatActivity;
import com.weget.fuyan.fyp.UtilHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Claudie on 9/8/16.
 */
public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder>{

    private List<Request> requestsList;
    Context mContext;
    int rId, myId, fr, fId, requestorId;
    String username, password, authString, err;
    ArrayList<Account> fulfillerAccountList = new ArrayList<>();
    final String URL = mContext.getString(R.string.webserviceurl);



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
                    requestorId = request.getRequestorId();

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
            String url = URL + "transaction/" + rId+"/received/";

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
            String url = URL + "transaction/" + rId+"/delivered/";

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
            String url = URL + "request/" + rId +"/fulfillers/";

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
        protected void onPostExecute(final Boolean result) {
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

                        boolean activityStarted = false;
                        boolean imARequestor = false;
                        boolean imAFulfiller = false;
                        boolean haveMyId = false;
                        int membercount = 0;

                        if((requestorId+"").equals(myId+"")){
                            imARequestor = true;
                        } else {
                            imAFulfiller = true;
                        }

                        if(imARequestor) {
                            Log.d("victorious", "Yes Im a requestor!");
                            for (GroupChannel gc : list) {
                                if (!activityStarted) {
                                    Log.d("victorious", "Activity not started yet! Running checks before opening channel...");
                                    Log.d("victorious", "This person has " + list.size() +" number of channles");
                                    List<User> uList = gc.getMembers();
                                    for (User u : uList) {
                                        if (u.getUserId().equals(fId + "")) {
                                            membercount++;
                                        }

                                        if (u.getUserId().equals(myId + "")) {
                                            membercount++;
                                            haveMyId = true;


                                        }

                                    }

                                    //if im a requestor and channel already exists
                                    if (membercount == gc.getMemberCount() && haveMyId) {
                                        Log.d("victorious", "if im a requestor and channel already exists");
                                        Intent intent = new Intent(mContext, UserChatActivity.class);
                                        intent.putExtra("channel_url", gc.getUrl());
                                        activityStarted = true;
                                        mContext.startActivity(intent);
                                        //finish();
                                    }
                                    membercount = 0;
                                }

                            }
                            if (!activityStarted){
                                Log.d("victorious", "Activity still not started yet! Running checks before creating channel...");
                                if (haveMyId) {
                                    Log.d("victorious", "if im a requestor and channel doesnt exists");
                                    Log.d("victorious", "so the requestor is : " + myId);
                                    Log.d("victorious", "so the fulfiller is : " + fId);

                                    List<String> uIds = new ArrayList<>();
                                    uIds.add(fId + "");
                                    uIds.add(myId + "");

                                    GroupChannel.createChannelWithUserIds(uIds, true, new GroupChannel.GroupChannelCreateHandler() {
                                        @Override
                                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                            if (e != null) {
                                                Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Intent intent = new Intent(mContext, UserChatActivity.class);
                                            intent.putExtra("channel_url", groupChannel.getUrl());

                                            mContext.startActivity(intent);

                                        }
                                    });
                                    activityStarted = true;
                                }
                            }

                        }

                        if(imAFulfiller) {
                            Log.d("victorious", "Yes Im a fulfiller!");
                            for (GroupChannel gc : list) {
                                if (!activityStarted) {
                                    Log.d("victorious", "Activity not started yet! Running checks before opening channel...");
                                    Log.d("victorious", "This person has " + list.size() +" number of channles");
                                    List<User> uList = gc.getMembers();
                                    for (User u : uList) {
                                        if (u.getUserId().equals(requestorId + "")) {
                                            membercount++;
                                        }

                                        if (u.getUserId().equals(myId + "")) {
                                            membercount++;
                                            haveMyId = true;


                                        }

                                    }

                                    //if im a requestor and channel already exists
                                    if (membercount == gc.getMemberCount() && haveMyId) {
                                        Log.d("victorious", "if im a fulfiller and channel already exists");
                                        Intent intent = new Intent(mContext, UserChatActivity.class);
                                        intent.putExtra("channel_url", gc.getUrl());
                                        activityStarted = true;
                                        mContext.startActivity(intent);
                                    }
                                    membercount = 0;
                                }
                            }
                            if (!activityStarted){
                                Log.d("victorious", "Activity still not started yet! Running checks before creating channel...");
                                if (haveMyId) {
                                    Log.d("victorious", "if im a fulfiller and channel doesnt exists");
                                    Log.d("victorious", "so the requestor is : " + requestorId);
                                    Log.d("victorious", "so the fulfiller is : " + myId);

                                    List<String> uIds = new ArrayList<>();
                                    uIds.add(requestorId + "");
                                    uIds.add(myId + "");

                                    GroupChannel.createChannelWithUserIds(uIds, true, new GroupChannel.GroupChannelCreateHandler() {
                                        @Override
                                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                            if (e != null) {
                                                Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Intent intent = new Intent(mContext, UserChatActivity.class);
                                            intent.putExtra("channel_url", groupChannel.getUrl());

                                            mContext.startActivity(intent);
                                            //finish();
                                        }
                                    });
                                    activityStarted = true;
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}




