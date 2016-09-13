package com.wegot.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class RequesterViewDetails extends AppCompatActivity {

    TextView productNameTV, requestorTV, addressTV, expiryTimeTV, priceTV;
    String productName, requestorName, location, expiryTime, priceS, requestorIdS, requirement,
    startTime, endTime, status, username, password, authString, err;
    double price;
    Button editRequestBtn, deleteRequestBtn;
    int requestId, requestorId, postal,duration,myId;
    Context mContext;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_view_details);

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
        status = request.getStatus();

        productNameTV = (TextView)findViewById(R.id.product_description);
        requestorTV = (TextView)findViewById(R.id.requestor_name);
        addressTV = (TextView)findViewById(R.id.address_details);
        expiryTimeTV = (TextView)findViewById(R.id.time_detail);
        priceTV = (TextView)findViewById(R.id.price_detail);
        editRequestBtn = (Button)findViewById(R.id.edit_button);
        deleteRequestBtn = (Button)findViewById(R.id.delete_button);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        new getRequestor().execute(authString + "," + requestorIdS);

        editRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (RequesterViewDetails.this, UpdateRequestActivity.class);
                i.putExtra("selected_request", (Serializable) request);
                startActivity(i);

            }
        });


    }

    private class getRequestor extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(RequesterViewDetails.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();


        }

        @Override
        protected Boolean doInBackground(String... params) {

            String auth = params[0].substring(0, params[0].indexOf(','));
            String rId = params[0].substring(params[0].indexOf(',') + 1);

            Log.d("auth: ", auth);
            Log.d("rID: ", rId);
            final String basicAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/account/"+ rId + "/";

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
            if(result){



                //itemPic.setImageResource(R.drawable.ordericon);
                requestorTV.setText(requestorName );
                productNameTV.setText(productName);
                addressTV.setText(location);
                expiryTimeTV.setText(endTime);
                priceTV.setText("" + price);


            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

            if(dialog.isShowing()){
                dialog.dismiss();
            }

        }
    }
}