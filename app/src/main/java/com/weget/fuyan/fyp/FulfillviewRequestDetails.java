package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.weget.fuyan.fyp.Util.DateFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FulfillviewRequestDetails extends AppCompatActivity {

    TextView productNameTV, requestorTV, addressTV, expiryTimeTV, priceTV, newPrice;
    String productName, requestorName, location, expiryTime, priceS, requestorIdS, requirement,
            startTime, endTime, status, username, password, authString, err, postal;
    double price,newprice;
    Button acceptRequestBtn, chatBtn;
    int requestId, requestorId, duration,myId;
    boolean fulfilled = false;
    boolean bank = true;
    Context mContext;
    Request request;
    Account a;

    ArrayList<Integer> fulfillerIdList = new ArrayList<>();
    private TextView productDescriptionTV;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulfillview_request_details);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //toolbar.setTitle("Request Completed");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString("");
        s.setSpan(new TypefaceSpan(this, "Roboto-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Update the action bar title with the TypefaceSpan instance
        //ActionBar actionBar = getActionBar();
        getSupportActionBar().setTitle(s);
        URL = getString(R.string.webserviceurl);

        request = (Request) getIntent().getSerializableExtra("selected_request");

        requestId = request.getId();
        requestorId = request.getRequestorId();
        requestorIdS = String.valueOf(requestorId);
        productName = request.getProductName();
        requirement = request.getRequirement();
        location = request.getLocation();
        postal = request.getPostal();
        startTime = request.getStartTime();
        endTime = request.getEndTime();
        duration = request.getDuration();
        price = request.getPrice();
        priceS = String.format("%.2f", price);
        status = request.getStatus();

        newPrice = (TextView)findViewById(R.id.price_detail3);
        productNameTV = (TextView)findViewById(R.id.product_name);
        productDescriptionTV = (TextView)findViewById(R.id.product_description);
        requestorTV = (TextView)findViewById(R.id.requestor_tv);
        addressTV = (TextView)findViewById(R.id.address_details);
        expiryTimeTV = (TextView)findViewById(R.id.time_detail);
        priceTV = (TextView)findViewById(R.id.price_detail);
        acceptRequestBtn = (Button)findViewById(R.id.accept_button);
        chatBtn = (Button)findViewById(R.id.chat_button);

        //apply typeface
        productNameTV.setTypeface(typeFaceLight);
        productDescriptionTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.requestor_tv)).setTypeface(typeFace);
        requestorTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.address)).setTypeface(typeFace);
        addressTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.expirytime)).setTypeface(typeFace);
        expiryTimeTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.price)).setTypeface(typeFace);
        priceTV.setTypeface(typeFaceLight);
        acceptRequestBtn.setTypeface(typeFace);
        chatBtn.setTypeface(typeFace);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        new getRequestor().execute(authString + "," + requestorIdS);
        acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getBank().execute(authString);
            }
        });
        requestorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (FulfillviewRequestDetails.this, RequestFulfillerDetailsActivity.class);
                i.putExtra("indicator", 1);
                i.putExtra("selected_request_tofulfull", (Serializable) request);
                i.putExtra("selected_fulfiller", (Serializable)a);
                startActivity(i);
            }
        });


        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                List<String> userIds = new ArrayList<>();
                userIds.add(requestorId +"");
                userIds.add(myId+"");



                GroupChannelListQuery mQuery = GroupChannel.createMyGroupChannelListQuery();

                if(mQuery == null){
                    GroupChannel.createChannelWithUserIds(userIds, true, new GroupChannel.GroupChannelCreateHandler() {
                        @Override
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if(e != null) {
                                Toast.makeText(FulfillviewRequestDetails.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(FulfillviewRequestDetails.this, UserChatActivity.class);
                            intent.putExtra("channel_url", groupChannel.getUrl());
                            startActivity(intent);
                            finish();
                        }
                    });
                } else{

                    mQuery.setIncludeEmpty(true);
                    mQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
                        @Override
                        public void onResult(List<GroupChannel> list, SendBirdException e) {
                            if (e != null) {
                                Toast.makeText(FulfillviewRequestDetails.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
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


                            if(imAFulfiller) {

                                for (GroupChannel gc : list) {
                                    if (!activityStarted) {

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

                                            Intent intent = new Intent(FulfillviewRequestDetails.this, UserChatActivity.class);
                                            intent.putExtra("channel_url", gc.getUrl());
                                            activityStarted = true;
                                            startActivity(intent);
                                            finish();
                                        }
                                        membercount = 0;
                                    }
                                }

                                if (!activityStarted){

                                    List<String> uIds = new ArrayList<>();
                                    uIds.add(requestorId + "");
                                    uIds.add(myId + "");

                                    GroupChannel.createChannelWithUserIds(uIds, true, new GroupChannel.GroupChannelCreateHandler() {
                                        @Override
                                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                            if (e != null) {
                                                Toast.makeText(FulfillviewRequestDetails.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Intent intent = new Intent(FulfillviewRequestDetails.this, UserChatActivity.class);
                                            intent.putExtra("channel_url", groupChannel.getUrl());

                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    activityStarted = true;

                                }
                            }



                        }
                    });
                }


                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private class getRequestor extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(FulfillviewRequestDetails.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            if(!isFinishing()) {
                dialog.show();
            }

            acceptRequestBtn.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String auth = params[0].substring(0, params[0].indexOf(','));
            String rId = params[0].substring(params[0].indexOf(',') + 1);

            Log.d("auth: ", auth);
            Log.d("rID: ", rId);
            final String basicAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "account/"+ rId + "/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                try {

                    Gson gson = new Gson();
                    a = gson.fromJson(rst,Account.class);
                    requestorName = a.getUsername();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                success = true;
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result){
                //itemPic.setImageResource(R.drawable.ordericon);
                requestorTV.setText(requestorName );
                productNameTV.setText(productName);
                addressTV.setText(location + " " + postal);
                expiryTimeTV.setText(DateFormatter.formatDate(endTime));
                priceTV.setText(priceS);
                newprice = price-(price*0.029+0.3);
                newPrice.setText("$ " + String.format("%.2f",newprice));

                productDescriptionTV.setText(requirement);

                new getMyRequestFulfiller().execute(authString);


            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
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
            String url = URL + "request/" + requestId +"/fulfillers/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for(int i = 0; i < jsoArray.length(); i++) {
                        JSONObject jso = jsoArray.getJSONObject(i);

                        id = jso.getInt("id");



                        fulfillerIdList.add(id);


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
            if(result) {

                for (int i : fulfillerIdList){
                    if(i == myId){
                        fulfilled = true;

                    }
                }

                Log.d("test -----------", ""+fulfilled);
                if(!fulfilled) {
                    acceptRequestBtn.setVisibility(View.VISIBLE);
                }

            }else{

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class createFulfill extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(FulfillviewRequestDetails.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            if(!isFinishing()) {
                dialog.show();
            }


        }

        @Override
        protected Boolean doInBackground(String... params) {
            String authString  = params[0];
            //authString = "admin:password";
            final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            //Log.d ("Basic Authentitaion", basicAuth);

            boolean success = false;
            String url = URL + "fulfill/";
            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("requestId", requestId);
                jsoin.put("fulfillerId", myId);
                jsoin.put("status", "active");


            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, jsoin.toString()+ basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
            } else {
                success = true;

            }
            return success;

        }
        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();

            if(result){
                Toast.makeText(getBaseContext(), "Request Accepted!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(FulfillviewRequestDetails.this, MainActivity.class);
                i.putExtra("accepted_fulfill_tab", 3);
                i.putExtra("accepted_fulfill_swipe", 0);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }

    private class getBank extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(FulfillviewRequestDetails.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {

            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            if(!isFinishing()) {
                dialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "account/"+ myId+"/bank/";
            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;
                try {
                    JSONObject jso = new JSONObject(rst);
                    int bankId = jso.getInt("id");
                    int bankUserId = jso.getInt("userId");
                    String bankUserName = jso.getString("accountHolder");
                    String bankAccNumber = jso.getString("accountNumber");
                    String bankName = jso.getString("bankName");
                }catch (JSONException e){
                    e.printStackTrace();
                    success = false;
                    bank = false;
                }
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result){
                bank = true;
                new AlertDialog.Builder(FulfillviewRequestDetails.this)
                        .setTitle("Alert!")
                        .setMessage("Do you really want to fulfill this request?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                new createFulfill().execute(authString);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }else {
                if(!bank){
                    Intent i = new Intent (FulfillviewRequestDetails.this, BankDetails.class);
                    i.putExtra("selected_request", (Serializable) request);
                    i.putExtra("empty_bank", 1);
                    startActivity(i);
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
}
