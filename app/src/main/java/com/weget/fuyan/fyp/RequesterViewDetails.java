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
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.weget.fuyan.fyp.Util.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class RequesterViewDetails extends AppCompatActivity {

    TextView productNameTV, requestorTV, addressTV, expiryTimeTV, priceTV;
    String productName, requestorName, location, expiryTime, priceS, requestorIdS, requirement,
    startTime, endTime, status, username, password, authString, err, postal;
    double price;
    Button editRequestBtn, deleteRequestBtn;
    int requestId, requestorId, duration,myId;
    Context mContext;
    Request request;
    private TextView productDescriptionTV;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_view_details);
        //apply font
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");


        URL = getString(R.string.webserviceurl);

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
        priceS = String.format("%.2f", price);

        productNameTV = (TextView)findViewById(R.id.product_name);
        productDescriptionTV = (TextView)findViewById(R.id.product_description);
        //requestorTV = (TextView)findViewById(R.id.requestor_name);
        addressTV = (TextView)findViewById(R.id.address_details);
        expiryTimeTV = (TextView)findViewById(R.id.time_detail);
        priceTV = (TextView)findViewById(R.id.price_detail);
        editRequestBtn = (Button)findViewById(R.id.edit_button);
        deleteRequestBtn = (Button)findViewById(R.id.delete_button);

        //apply typeface
        productNameTV.setTypeface(typeFaceLight);
        productDescriptionTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.requestor_tv)).setTypeface(typeFace);
        //requestorTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.address)).setTypeface(typeFace);
        addressTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.expirytime)).setTypeface(typeFace);
        expiryTimeTV.setTypeface(typeFaceLight);
        //((TextView)findViewById(R.id.price)).setTypeface(typeFace);
        priceTV.setTypeface(typeFaceLight);
        editRequestBtn.setTypeface(typeFace);
        deleteRequestBtn.setTypeface(typeFace);

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

        deleteRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Alert!")
                        .setMessage("Confirm cancel order??")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                new cancelRequest().execute(authString);


                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

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
                //itemPic.setImageResource(R.drawable.ordericon);
                //requestorTV.setText(requestorName );
                productNameTV.setText(productName);
                productDescriptionTV.setText(request.getRequirement());
                addressTV.setText(location + " " + postal);
                expiryTimeTV.setText(DateFormatter.formatDate(endTime));
                priceTV.setText(priceS);


            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }


        }
    }

    private class cancelRequest extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(RequesterViewDetails.this, R.style.MyTheme);

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


            //authString = "admin:password";
            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "request/" + requestId + "/";

            String rst = UtilHttp.doHttpDeleteBasicAuthenticaion(mContext, url, basicAuth);
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
            if(result) {
                Toast.makeText(getBaseContext(), "Request cancelled!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(RequesterViewDetails.this, MainActivity.class);
                i.putExtra("updated_request_tab", 1);
                i.putExtra("udpated_request_swipe", 0);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }
}
