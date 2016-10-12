package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyfulfillDetails extends AppCompatActivity {

    Request r;
    TextView productNameTV, requestorNameTV, addressTV, priceTV;
    Button chatBtn;
    String productName, requestorName, address, username, password, authString, requestorIdS, err;
    double price;
    int requestId, myId, requestorId;
    Context mContext;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfulfill_details);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Fulfill Details");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        URL = getString(R.string.webserviceurl);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;


        r = (Request)getIntent().getSerializableExtra("selected_fulfill_request");
        requestorId = r.getRequestorId();
        requestorIdS = String.valueOf(requestorId);
        requestId = r.getId();
        productName = r.getProductName();
        address = r.getLocation();
        price = r.getPrice();

        productNameTV = (TextView)findViewById(R.id.product_description);
        requestorNameTV = (TextView)findViewById(R.id.requestor_name);
        addressTV = (TextView)findViewById(R.id.address_details);
        priceTV = (TextView)findViewById(R.id.price_detail);

        ((TextView)findViewById(R.id.product_name)).setTypeface(typeFace);
        productNameTV.setTypeface(typeFaceLight);
        ((TextView)findViewById(R.id.requestor_tv)).setTypeface(typeFace);
        requestorNameTV.setTypeface(typeFaceLight);
        ((TextView)findViewById(R.id.address)).setTypeface(typeFace);
        addressTV.setTypeface(typeFaceLight);
        ((TextView)findViewById(R.id.price)).setTypeface(typeFace);
        priceTV.setTypeface(typeFaceLight);
        ((Button)findViewById(R.id.chat_button)).setTypeface(typeFace);

        chatBtn = (Button)findViewById(R.id.chat_button);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Hihi", "checking requestor id : " + requestorId);
                Log.d("Hihi", "checking myId : " + myId);

                List<String> userIds = new ArrayList<>();
                userIds.add(requestorId +"");
                userIds.add(myId+"");
                GroupChannelListQuery mQuery = GroupChannel.createMyGroupChannelListQuery();
                Log.d("victorious","mquerysize is : " + mQuery.hasNext());
                if(mQuery == null){
                    GroupChannel.createChannelWithUserIds(userIds, true, new GroupChannel.GroupChannelCreateHandler() {
                        @Override
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if(e != null) {
                                Toast.makeText(MyfulfillDetails.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(MyfulfillDetails.this, UserChatActivity.class);
                            intent.putExtra("channel_url", groupChannel.getUrl());
                            startActivity(intent);
                            finish();
                        }
                    });
                } else{

                    Log.d("geo1","isit we dunnid create a group?");

                    mQuery.setIncludeEmpty(true);
                    mQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
                        @Override
                        public void onResult(List<GroupChannel> list, SendBirdException e) {
                            if (e != null) {
                                Toast.makeText(MyfulfillDetails.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Log.d("victorious", "Yes Im a fulfiller!");
                                Log.d("victorious", "YOYO This person has " + list.size() +" number of channles");
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
                                            Log.d("victorious", "if im a requestor and channel already exists");
                                            Intent intent = new Intent(MyfulfillDetails.this, UserChatActivity.class);
                                            intent.putExtra("channel_url", gc.getUrl());
                                            activityStarted = true;
                                            startActivity(intent);
                                            finish();
                                        }
                                        membercount = 0;
                                    }
                                }
                                Log.d("victorious","activity has started? : " + activityStarted);
                                Log.d("victorious","HAVE MY ID? : " + haveMyId);
                                if (!activityStarted){
                                    Log.d("victorious", "Activity still not started yet! Running checks before creating channel...");
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
                                                Toast.makeText(MyfulfillDetails.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Intent intent = new Intent(MyfulfillDetails.this, UserChatActivity.class);
                                            Log.d("victorious", "channel_url is : " + groupChannel.getUrl());
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



        new getRequestor().execute(authString + "," + requestorIdS);

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

        ProgressDialog dialog = new ProgressDialog(MyfulfillDetails.this, R.style.MyTheme);


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
                    JSONObject jso = new JSONObject(rst);
                    requestorName = jso.getString("username");

                } catch (JSONException e) {
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


                requestorNameTV.setText(requestorName );
                productNameTV.setText(productName);
                addressTV.setText(address);
                priceTV.setText("" + price);


            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }



        }
    }
}
