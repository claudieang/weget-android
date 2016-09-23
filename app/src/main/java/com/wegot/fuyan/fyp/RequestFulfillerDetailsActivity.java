package com.wegot.fuyan.fyp;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequestFulfillerDetailsActivity extends AppCompatActivity {

    Request request;
    Account account;
    TextView fulfillerNameTV, fulfillerEmailTV, fulfillerContactTV;
    ImageView fulfillerPicIV;
    Button acceptFulfillerBtn, chatBtn;

    String fulfillerName, fulfillerEmail, fulfillerPic, fulfillerContactS, productName, requirement,
    location, startTime, endTime, username, password, authString, err, fulfillStatus, requestString;
    int fulfillerContact, requestorId, fulfillerId, postal, duration, fulfillId, requestId, myId;
    double price;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_fulfiller_details);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //toolbar.setTitle("Fulfiller Details");
        //setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fulfiller Details");


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id",0);
        authString  = username + ":" + password;

        fulfillerNameTV = (TextView)findViewById(R.id.request_fulfiller_name);
        fulfillerEmailTV = (TextView)findViewById(R.id.request_fulfiller_email);
        fulfillerContactTV = (TextView)findViewById(R.id.request_fulfiller_contact);
        fulfillerPicIV = (ImageView)findViewById(R.id.request_fulfiller_image);
        acceptFulfillerBtn = (Button)findViewById(R.id.select_fulfiller_btn);
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
        fulfillerEmailTV.setText(fulfillerEmail);
        fulfillerContactTV.setText("" + fulfillerContactS);

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
                    }
                });
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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/active/";

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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/fulfill/"+fulfillId+"/";

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

}
