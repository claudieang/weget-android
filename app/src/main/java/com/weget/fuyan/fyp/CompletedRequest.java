package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weget.fuyan.fyp.Util.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CompletedRequest extends AppCompatActivity {

    Request r;
    TextView productNameTV, requestorNameTV, addressTV, priceTV, requestorTitleTV, title, date, fulfillerName, details;
    String productName, requestorName, address, username, password, authString, requestorIdS, err;
    double price;
    int requestId, myId, requestorId, postal;
    Context mContext;
    ArrayList<Account> fulfillerAccountList = new ArrayList<>();
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_request);

        //apply font
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        URL = getString(R.string.webserviceurl);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;


        r = (Request)getIntent().getSerializableExtra("completed_request");
        requestorId = r.getRequestorId();
        requestorIdS = String.valueOf(requestorId);
        requestId = r.getId();
        productName = r.getProductName();
        address = r.getLocation();
        price = r.getPrice();
        postal = r.getPostal();


        title = (TextView)findViewById(R.id.title);
        date = (TextView)findViewById(R.id.date);

        productNameTV = (TextView)findViewById(R.id.product_name);
        fulfillerName = (TextView)findViewById(R.id.fulfiller_name);
        requestorNameTV = (TextView)findViewById(R.id.requestor_tv);
        addressTV = (TextView)findViewById(R.id.address);
        priceTV = (TextView)findViewById(R.id.price);
        details = (TextView)findViewById(R.id.info);


//        ((TextView)findViewById(R.id.product_name)).setTypeface(typeFace);
//        ((TextView)findViewById(R.id.product_description)).setTypeface(typeFaceLight);
//        ((TextView)findViewById(R.id.requestor_tv)).setTypeface(typeFace);
//        ((TextView)findViewById(R.id.requestor_name)).setTypeface(typeFaceLight);
//        ((TextView)findViewById(R.id.address)).setTypeface(typeFace);
//        ((TextView)findViewById(R.id.address_details)).setTypeface(typeFaceLight);
//        ((TextView)findViewById(R.id.price)).setTypeface(typeFace);
//        ((TextView)findViewById(R.id.price_detail)).setTypeface(typeFaceLight);


        if (myId != requestorId) {


            Log.d("MY ID: =========", ""+myId);
            Log.d("R ID:========", ""+requestorId);
            new getRequestor().execute(authString + "," + requestorIdS);


        }else{

            Log.d("MY ID: =========", ""+myId);
            Log.d("R ID:========", ""+requestorId);
            new getMyRequestFulfiller().execute(authString);
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

    private class getRequestor extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(CompletedRequest.this, R.style.MyTheme);


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

//
//                requestorNameTV.setText(requestorName );
//                productNameTV.setText(productName);
//                addressTV.setText(address + " " + postal);
//                priceTV.setText("$" + price + "0")
                String status = r.getStatus();
                status  = status.substring(0, 1).toUpperCase() + status.substring(1);
                title.setText(status + "d Request");
                date.setText(DateFormatter.formatDate(r.getStartTime()));
                productNameTV.setText(productName);
                //fulfillerName.setText();
                priceTV.setText("$" + price + "0");
                addressTV.setText(address + " " + postal);
                details.setText(r.getRequirement());


            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

            if(dialog.isShowing()){
                dialog.dismiss();
            }

        }
    }

    private class getMyRequestFulfiller extends AsyncTask<String, Void, Boolean> {

        int id, contactNo;
        String username, password, email, fulfiller, picture;
        Account account;
        ProgressDialog dialog = new ProgressDialog(CompletedRequest.this, R.style.MyTheme);

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
            String url = URL + "request/" + requestId +"/fulfillers/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                fulfillerAccountList.clear();

                try {
                    Gson gson = new Gson();
                    fulfillerAccountList = gson.fromJson(rst, new TypeToken<List<Account>>() {}.getType());

//                    JSONArray jsoArray = new JSONArray(rst);
//                    for(int i = 0; i < jsoArray.length(); i++) {
//                        JSONObject jso = jsoArray.getJSONObject(i);
//
//                        id = jso.getInt("id");
//                        username = jso.getString("username");
//                        password = jso.getString("password");
//                        contactNo = jso.getInt("contactNo");
//                        email = jso.getString("email");
//                        fulfiller = jso.getString("fulfiller");
//                        picture = jso.getString("picture");
//
//                        account = new Account(id, username, password, contactNo, email, fulfiller, picture);
//
//
//                        fulfillerAccountList.add(account);
//
//
//                    }
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

            if(result) {
                if(fulfillerAccountList.size() == 1){
                    Account a = fulfillerAccountList.get(0);


//                    requestorTitleTV.setText("Fulfilled by");
//                    requestorNameTV.setText(a.getUsername());
//                    productNameTV.setText(productName);
//                    addressTV.setText(address + " " + postal);
//                    priceTV.setText("$" + price + "0");
                    String status = r.getStatus();
                    status  = status.substring(0, 1).toUpperCase() + status.substring(1);
                    if(status.charAt(status.length() -1 ) != 'd'){
                        status += "d";
                    }
                    title.setText(status + " Request");
                    date.setText(DateFormatter.formatDateFull(r.getStartTime()));
                    productNameTV.setText(productName);
                    fulfillerName.setText(a.getUsername());
                    priceTV.setText("$" + price + "0");
                    addressTV.setText(address + " " + postal);
                    details.setText(r.getRequirement());

                }

            }else{
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
