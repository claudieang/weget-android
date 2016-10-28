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
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class PendingdetailsRequester extends AppCompatActivity {

    Request myRequest;
    TextView productNameTV, requestorTV, addressTV, priceTV, productName1;
    String productName, requestorName, address, err, username, password, authString, requestorIdS, productDescription;
    int myId, requestorId, myRequestId, postal, transactionId;
    double price;
    Button receivedBtn, disputeBtn;
    Context mContext;
    ProgressDialog dialog;
    ArrayList<Account> fulfillerAccountList = new ArrayList<>();
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendingdetails_requester);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        URL = getString(R.string.webserviceurl);
        dialog = new ProgressDialog(PendingdetailsRequester.this, R.style.MyTheme);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        myRequest =(Request) getIntent().getSerializableExtra("selected_my_pending_request");
        myRequestId = myRequest.getId();
        requestorId = myRequest.getRequestorId();
        requestorIdS = String.valueOf(requestorId);
        productName = myRequest.getProductName();
        address = myRequest.getLocation();
        price = myRequest.getPrice();
        postal = myRequest.getPostal();
        productDescription = myRequest.getRequirement();

        productName1 = (TextView)findViewById(R.id.product_name);
        productNameTV = (TextView)findViewById(R.id.product_description);
        requestorTV = (TextView)findViewById(R.id.requestor_name);
        addressTV = (TextView)findViewById(R.id.address_details);
        priceTV = (TextView)findViewById(R.id.price_detail);
        receivedBtn = (Button)findViewById(R.id.receve_button);
        disputeBtn = (Button)findViewById(R.id.dipute_button);

        productName1.setTypeface(typeFaceLight);
        productNameTV.setTypeface(typeFaceLight);
        requestorTV.setTypeface(typeFaceLight);
        addressTV.setTypeface(typeFaceLight);
        priceTV.setTypeface(typeFaceLight);

        receivedBtn.setTypeface(typeFace);
        disputeBtn.setTypeface(typeFace);

        new getMyRequestFulfiller().execute(authString);

        receivedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PendingdetailsRequester.this)
                        .setTitle("Alert!")
                        .setMessage("Confirm received order?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                new doReceive().execute(authString);
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

    private class getMyRequestFulfiller extends AsyncTask<String, Void, Boolean> {

        int id, contactNo;
        String username, password, email, fulfiller, picture;
        Account account;
        ProgressDialog dialog = new ProgressDialog(PendingdetailsRequester.this, R.style.MyTheme);

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
            String url = URL + "request/" + myRequestId +"/fulfillers/";

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
            dialog.dismiss();

            if(result) {
                if(fulfillerAccountList.size() == 1){
                    Account a = fulfillerAccountList.get(0);
                    requestorTV.setText(a.getUsername());
                    productName1.setText(productName);
                    productNameTV.setText(productDescription);
                    addressTV.setText(address + " " + postal);
                    priceTV.setText("" + price + "0");

                }

            }else{
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }



    private class doReceive extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog = new ProgressDialog(PendingdetailsRequester.this, R.style.MyTheme);

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
            String url = URL + "transaction/" + myRequestId+"/received/";

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
            if(result) {
                dialog.dismiss();
                Account a = fulfillerAccountList.get(0);
                Intent i = new Intent(PendingdetailsRequester.this, Rating_requestor.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("selected_request", (Serializable) myRequest);
                i.putExtra("user_to_rate", (Serializable)a);
                Toast.makeText(getApplicationContext(), "Received!", Toast.LENGTH_SHORT).show();
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

                Intent i = new Intent (PendingdetailsRequester.this, dispute.class);
                i.putExtra("transaction_id", transactionId);
                i.putExtra("origin", "requestor");
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
