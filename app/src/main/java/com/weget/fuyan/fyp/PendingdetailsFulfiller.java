package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PendingdetailsFulfiller extends AppCompatActivity {

    Request myRequest;
    TextView productNameTV, requestorTV, addressTV, priceTV, productName1, newPriceTV;
    String productName, requestorName, address, err, postal, username, password, authString, requestorIdS,requirement, priceS;
    int myId, requestorId, myRequestId, transactionId;
    double price, newprice;
    Button deliveredBtn, disputeBtn;
    Context mContext;
    ProgressDialog dialog;
    String URL;
    Account a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendingdetails_fulfiller);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        URL = getString(R.string.webserviceurl);

        dialog = new ProgressDialog(PendingdetailsFulfiller.this, R.style.MyTheme);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        myRequest =(Request) getIntent().getSerializableExtra("selected_my_pending_fulfill");
        myRequestId = myRequest.getId();
        requestorId = myRequest.getRequestorId();
        requestorIdS = String.valueOf(requestorId);
        productName = myRequest.getProductName();
        address = myRequest.getLocation();
        price = myRequest.getPrice();
        priceS = String.format("%.2f", price);
        postal = myRequest.getPostal();
        requirement = myRequest.getRequirement();

        productNameTV = (TextView)findViewById(R.id.product_description);
        productName1 = (TextView)findViewById(R.id.product_name);
        requestorTV = (TextView)findViewById(R.id.requestor_name);
        addressTV = (TextView)findViewById(R.id.address_details);
        priceTV = (TextView)findViewById(R.id.price_detail);
        newPriceTV = (TextView)findViewById(R.id.price_detail3);
        deliveredBtn = (Button)findViewById(R.id.delivered_button);
        disputeBtn = (Button)findViewById(R.id.dipute_button);

        productName1.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.product_name)).setTypeface(typeFace);
        productNameTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.requestor_tv)).setTypeface(typeFace);
        requestorTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.address)).setTypeface(typeFace);
        addressTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.price)).setTypeface(typeFace);
        priceTV.setTypeface(typeFaceLight);

        deliveredBtn.setTypeface(typeFace);
        disputeBtn.setTypeface(typeFace);


        new getRequestor().execute(authString + "," + requestorIdS);

        deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PendingdetailsFulfiller.this)
                        .setTitle("Alert!")
                        .setMessage("Confirm delivered order?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                new doDeliver().execute(authString);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        disputeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new getTransaction().execute(authString);
            }
        });

        requestorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (PendingdetailsFulfiller.this, RequestFulfillerDetailsActivity.class);
                i.putExtra("indicator", 1);
                i.putExtra("selected_request_tofulfull", (Serializable) myRequest);
                i.putExtra("selected_fulfiller", (Serializable)a);
                startActivity(i);
            }
        });

    }

    private class getRequestor extends AsyncTask<String, Void, Boolean> {



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
                    int aId = jso.getInt("id");
                    requestorName = jso.getString("username");
                    String aPw = jso.getString("password");
                    int aContact = jso.getInt("contactNo");
                    String aEmail = jso.getString("email");
                    String aFulfiller = jso.getString("fulfiller");
                    String aPicture = jso.getString("picture");

                    a = new Account (aId, requestorName, aPw, aContact, aEmail, aFulfiller, aPicture);


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

                productName1.setText(productName);
                requestorTV.setText(requestorName );
                productNameTV.setText(requirement);
                addressTV.setText(address + " " + postal);
                priceTV.setText(priceS);

                newprice = (price-(price*0.029+0.3))*0.9;
                newPriceTV.setText("$ " +String.format("%.2f",newprice));


            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }


        }
    }

    private class doDeliver extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(PendingdetailsFulfiller.this, R.style.MyTheme);

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
            String url = URL + "transaction/" + myRequestId+"/delivered/";

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
                Intent i = new Intent(PendingdetailsFulfiller.this, Rating_requestor.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("selected_request", (Serializable) myRequest);
                i.putExtra("user_to_rate", (Serializable)a);
                Toast.makeText(getApplicationContext(), "Delivered!", Toast.LENGTH_SHORT).show();
                startActivity(i);
                finish();

            }else{

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }


    }

    private class getTransaction extends AsyncTask<String, Void, Boolean> {

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
            String url = URL + "request/"+myRequestId+"/transaction/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                try {
                    JSONObject jso = new JSONObject(rst);
                    transactionId = jso.getInt("transactionId");



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

                Intent i = new Intent (PendingdetailsFulfiller.this, dispute.class);

                i.putExtra("transaction_id", transactionId);
                i.putExtra("user_type",0); //zero means fulfiller, 1 means requestor
                i.putExtra("origin", "fulfiller");
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                //finish();

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
