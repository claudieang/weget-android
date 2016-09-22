package com.wegot.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MyfulfillDetails extends AppCompatActivity {

    Request r;
    TextView productNameTV, requestorNameTV, addressTV, priceTV;
    String productName, requestorName, address, username, password, authString, requestorIdS, err;
    double price;
    int requestId, myId, requestorId;
    Context mContext;

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
