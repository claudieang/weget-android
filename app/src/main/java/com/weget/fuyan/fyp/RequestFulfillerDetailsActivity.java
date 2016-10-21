package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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

public class RequestFulfillerDetailsActivity extends AppCompatActivity {

    Request request;
    Account account;
    TextView fulfillerNameTV, fulfillerEmailTV, fulfillerContactTV, requestorRtValue, fulfillerRtValue;
    ImageView fulfillerPicIV;
    Button acceptFulfillerBtn, chatBtn;

    String fulfillerName, fulfillerEmail, fulfillerPic, fulfillerContactS, productName, requirement,
    location, startTime, endTime, username, password, authString, err, fulfillStatus, requestString;
    int fulfillerContact, requestorId, fulfillerId, postal, duration, fulfillId, requestId, myId;
    double price, requestorRating, requestorRatingNum, fulfillerRating, fulfillerRatingNum, requestorRt, fulfillerRt;
    Context mContext;
    RatingBar ratingBar1, ratingBar2;

    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_fulfiller_details);


        //rating part

        ratingBar1 = (RatingBar)findViewById(R.id.ratingBar1);
        ratingBar2 = (RatingBar)findViewById(R.id.ratingBar2);
        requestorRtValue = (TextView)findViewById(R.id.requestor_rating_value);
        fulfillerRtValue = (TextView)findViewById(R.id.fulfiller_rating_value);



        //Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //toolbar.setTitle("Fulfiller Details");
        //setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fulfiller Details");
        getSupportActionBar().setElevation(0);
        URL = getString(R.string.webserviceurl);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id",0);
        authString  = username + ":" + password;

        fulfillerNameTV = (TextView)findViewById(R.id.profile_username);
        fulfillerPicIV = (ImageView)findViewById(R.id.profile_picture);
        acceptFulfillerBtn = (Button)findViewById(R.id.select_fulfiller_btn);
        fulfillerContactTV = (TextView)findViewById(R.id.profile_contactNumber);
        Typeface typeFace2=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        acceptFulfillerBtn.setTypeface(typeFace2);

        chatBtn = (Button)findViewById(R.id.chat_button);
        chatBtn.setTypeface(typeFace2);

        request = (Request) getIntent().getSerializableExtra("selected_request_tofulfull");
        account = (Account) getIntent().getSerializableExtra("selected_fulfiller");
        fulfillId = getIntent().getIntExtra("selected_fulfill_id", 0);
        fulfillerName = account.getUsername();
        fulfillerEmail = account.getEmail();
        fulfillerContact = account.getContactNo();
        fulfillerContactS = String.valueOf(fulfillerContact);
        fulfillerPic = account.getPicture();
        fulfillerId = account.getId();

        requestId = request.getId();
        requestorId = request.getRequestorId();
        productName = request.getProductName();
        requirement = request.getRequirement();
        location = request.getLocation();
        postal = request.getPostal();
        startTime = request.getStartTime();
        endTime = request.getEndTime();
        duration = request.getDuration();
        price = request.getPrice();

        if(fulfillerPic.equals("")){

            fulfillerPicIV.setImageResource(R.drawable.ic_account_circle_black_48dp);
        }else{


            //this.dpIV.setImageDrawable(roundDrawable);
            byte[] decodeString = Base64.decode(fulfillerPic, Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodebitmap);
            roundDrawable.setCircular(true);
            fulfillerPicIV.setImageDrawable(roundDrawable);
        }

        fulfillerNameTV.setText(fulfillerName);
        fulfillerContactTV.setText("" + fulfillerContactS);

        new getRating().execute(authString);

        acceptFulfillerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RequestFulfillerDetailsActivity.this)
                        .setTitle("Alert!")
                        .setMessage("Do you really want to select this Fulfiller?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                new getRequestsv().execute(authString);

                                //new acceptFulfill().execute(authString);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                List<String> userIds = new ArrayList<>();
                userIds.add(fulfillerId +"");
                userIds.add(myId+"");

                GroupChannelListQuery mQuery = GroupChannel.createMyGroupChannelListQuery();
                if(mQuery == null){
                    GroupChannel.createChannelWithUserIds(userIds, true, new GroupChannel.GroupChannelCreateHandler() {
                        @Override
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if(e != null) {
                                Toast.makeText(RequestFulfillerDetailsActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(RequestFulfillerDetailsActivity.this, UserChatActivity.class);
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
                                Toast.makeText(RequestFulfillerDetailsActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                            if (u.getUserId().equals(fulfillerId + "")) {
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
                                            Intent intent = new Intent(RequestFulfillerDetailsActivity.this, UserChatActivity.class);
                                            intent.putExtra("channel_url", gc.getUrl());
                                            activityStarted = true;
                                            startActivity(intent);
                                            finish();
                                        }
                                        membercount = 0;
                                    }

                                }
                                if (!activityStarted){
                                    Log.d("victorious", "Activity still not started yet! Running checks before creating channel...");
                                    if (haveMyId) {
                                        Log.d("victorious", "if im a requestor and channel doesnt exists");
                                        Log.d("victorious", "so the requestor is : " + myId);
                                        Log.d("victorious", "so the fulfiller is : " + fulfillerId);

                                        List<String> uIds = new ArrayList<>();
                                        uIds.add(fulfillerId + "");
                                        uIds.add(myId + "");

                                        GroupChannel.createChannelWithUserIds(uIds, true, new GroupChannel.GroupChannelCreateHandler() {
                                            @Override
                                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                                if (e != null) {
                                                    Toast.makeText(RequestFulfillerDetailsActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                Intent intent = new Intent(RequestFulfillerDetailsActivity.this, UserChatActivity.class);
                                                intent.putExtra("channel_url", groupChannel.getUrl());

                                                startActivity(intent);
                                                finish();
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
                                            Intent intent = new Intent(RequestFulfillerDetailsActivity.this, UserChatActivity.class);
                                            intent.putExtra("channel_url", gc.getUrl());
                                            activityStarted = true;
                                            startActivity(intent);
                                            finish();
                                        }
                                        membercount = 0;
                                    }
                                }
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
                                                Toast.makeText(RequestFulfillerDetailsActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Intent intent = new Intent(RequestFulfillerDetailsActivity.this, UserChatActivity.class);
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


    private class getRequestsv extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(RequestFulfillerDetailsActivity.this, R.style.MyTheme);

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
            String url = URL + "request/active/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
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
            if(result){

                new getFulfill().execute(authString);

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getFulfill extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "fulfill/"+fulfillId+"/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;

                JSONObject jso = null;
                try{
                    jso = new JSONObject(rst);
                    fulfillStatus = jso.getString("status");

                }catch(JSONException e){
                    e.printStackTrace();
                    err = e.getMessage();
                }
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result){

                if(fulfillStatus.equals("active")) {

                    Intent i = new Intent (RequestFulfillerDetailsActivity.this, PaymentActivity.class);
                    requestString = String.valueOf(requestId);
                    i.putExtra("fulfill_Id", fulfillId);
                    i.putExtra("fulfill_price", price);
                    i.putExtra("product_name", productName);
                    i.putExtra("product_desc", requirement);
                    i.putExtra("request_string", requestString);
                    startActivity(i);
                    finish();

                }else{
                    Intent i = new Intent (RequestFulfillerDetailsActivity.this, MyRequestActivity.class);
                    Toast.makeText(getApplicationContext(), "Request Expired!", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    finish();

                }

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getRating extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "rating/" + fulfillerId+"/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {


                try {

                    JSONObject jso = new JSONObject(rst);

                    requestorRating = jso.getDouble("requestTotal");
                    requestorRatingNum = jso.getDouble("requestNo");
                    fulfillerRating = jso.getDouble("fulfillTotal");
                    fulfillerRatingNum = jso.getDouble("fulfillNo");
                    requestorRt = requestorRating / requestorRatingNum;
                    fulfillerRt = fulfillerRating / fulfillerRatingNum;



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                success = true;
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {


            if(result){



                ratingBar1.setRating(Float.parseFloat(Double.toString(requestorRt)));
                ratingBar2.setRating(Float.parseFloat(Double.toString(fulfillerRt)));
                String pad1 = Double.toString(requestorRt)+"00";
                String pad2 = Double.toString(fulfillerRt)+"00";
                requestorRtValue.setText(pad1.substring(0,(pad1.indexOf('.')+2)));
                fulfillerRtValue.setText(pad2.substring(0,(pad1.indexOf('.')+2)));





            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
