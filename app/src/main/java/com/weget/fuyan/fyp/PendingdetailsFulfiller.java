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

public class PendingdetailsFulfiller extends AppCompatActivity {

    Request myRequest;
    TextView productNameTV, requestorTV, addressTV, priceTV;
    String productName, requestorName, address, err, username, password, authString, requestorIdS;
    int myId, requestorId, myRequestId, postal, transactionId;
    double price;
    Button deliveredBtn, disputeBtn;
    Context mContext;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendingdetails_fulfiller);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Pending Request");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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
        postal = myRequest.getPostal();

        productNameTV = (TextView)findViewById(R.id.product_description);
        requestorTV = (TextView)findViewById(R.id.requestor_name);
        addressTV = (TextView)findViewById(R.id.address_details);
        priceTV = (TextView)findViewById(R.id.price_detail);
        deliveredBtn = (Button)findViewById(R.id.delivered_button);
        disputeBtn = (Button)findViewById(R.id.dipute_button);

        ((TextView)findViewById(R.id.product_name)).setTypeface(typeFace);
        productNameTV.setTypeface(typeFaceLight);
        ((TextView)findViewById(R.id.requestor_tv)).setTypeface(typeFace);
        requestorTV.setTypeface(typeFaceLight);
        ((TextView)findViewById(R.id.address)).setTypeface(typeFace);
        addressTV.setTypeface(typeFaceLight);
        ((TextView)findViewById(R.id.price)).setTypeface(typeFace);
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
            dialog.dismiss();
            if(result){


                requestorTV.setText(requestorName );
                productNameTV.setText(productName);
                addressTV.setText(address + " " + postal);
                priceTV.setText("$" + price + "0");


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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/transaction/" + myRequestId+"/delivered/";

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
                Intent i = new Intent(PendingdetailsFulfiller.this, MainActivity.class);
                i.putExtra("after_delivered_tab", 3);
                i.putExtra("complete_fulfill_swipe",2);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Toast.makeText(getApplicationContext(), "Delivered!", Toast.LENGTH_SHORT).show();
                startActivity(i);

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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/"+myRequestId+"/transaction/";

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
                i.putExtra("origin", "fulfiller");
                startActivity(i);

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