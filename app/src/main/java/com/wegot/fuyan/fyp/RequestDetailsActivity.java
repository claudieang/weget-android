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
import android.service.voice.VoiceInteractionSession;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class RequestDetailsActivity extends AppCompatActivity {

    ArrayList<Integer> fulfillerIdList = new ArrayList<>();
    int requestorId, postal, duration, myId, requestId;
    String productName, requirement, location, startTime, endTime, status, err, requestorIdS, requestorName, authString,
    username, password, picture;
    double price;
    Context mContext;
    Button acceptRequestBtn;
    boolean fulfilled = false;
    ImageView profilePic, itemPic;

    TextView requestorTV, productNameTV, requirementTV, locationTV, postalTV, startTimeTV, endTimeTV, durationTV,
    priceTV, statusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        Request request = (Request) getIntent().getSerializableExtra("selected_request");

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

        //itemPic = (ImageView)findViewById(R.id.itemPic);
        profilePic = (ImageView)findViewById(R.id.profilePic);
        requestorTV = (TextView)findViewById(R.id.requestor_tv);
        productNameTV = (TextView)findViewById(R.id.product_tv);
        requirementTV = (TextView)findViewById(R.id.requirement_tv);
        locationTV = (TextView)findViewById(R.id.location_tv);
        //postalTV = (TextView)findViewById(R.id.postal_tv);
        //startTimeTV = (TextView)findViewById(R.id.start_time_tv);
        endTimeTV = (TextView)findViewById(R.id.end_time_tv);
        //durationTV = (TextView)findViewById(R.id.duration_tv);
        priceTV = (TextView)findViewById(R.id.price_tv);
        statusTV = (TextView)findViewById(R.id.status_tv);
        acceptRequestBtn = (Button)findViewById(R.id.accept_request_btn);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        new getRequestor().execute(authString + "," + requestorIdS);


        acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RequestDetailsActivity.this)
                        .setTitle("Alert!")
                        .setMessage("Do you really want to fulfill this request?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                new createFulfill().execute(authString);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });


        //requestorTV.setText("Requestor: " + requestorId);

        //font
        TextView myTextView=(TextView)findViewById(R.id.request_details_tital);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");
        Typeface typeFace2=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Regular.ttf");
        myTextView.setTypeface(typeFace);

        requestorTV.setTypeface(typeFace2);
        productNameTV.setTypeface(typeFace2);
        requirementTV.setTypeface(typeFace2);
        locationTV.setTypeface(typeFace2);
//        postalTV.setTypeface(typeFace2);
//        startTimeTV.setTypeface(typeFace2);
        endTimeTV.setTypeface(typeFace2);
//        durationTV.setTypeface(typeFace2);
        priceTV.setTypeface(typeFace2);
        statusTV.setTypeface(typeFace2);
        acceptRequestBtn.setTypeface(typeFace2);


    }

    private class getMyRequestFulfiller extends AsyncTask<String, Void, Boolean> {

        int id, contactNo;
        String username, password, email, fulfiller, picture;
        Account account;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/" + requestId +"/fulfillers/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for(int i = 0; i < jsoArray.length(); i++) {
                        JSONObject jso = jsoArray.getJSONObject(i);

                        id = jso.getInt("id");



                        fulfillerIdList.add(id);


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
            if(result) {

                for (int i : fulfillerIdList){
                    if(i == myId){
                        fulfilled = true;
                    }
                }

                Log.d("test -----------", ""+fulfilled);
                if(!fulfilled) {
                    acceptRequestBtn.setVisibility(View.VISIBLE);
                }

            }else{

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class createFulfill extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(RequestDetailsActivity.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();


        }

        @Override
        protected Boolean doInBackground(String... params) {
            String authString  = params[0];
            //authString = "admin:password";
            final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            //Log.d ("Basic Authentitaion", basicAuth);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/fulfill/";
            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("requestId", requestId);
                jsoin.put("fulfillerId", myId);
                jsoin.put("status", "active");


            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, jsoin.toString()+ basicAuth);
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
            if(result){
                Toast.makeText(getBaseContext(), "Request Accepted!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(RequestDetailsActivity.this, MainActivity.class);
                startActivity(i);
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }

    private class getRequestor extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
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
                    picture = jso.getString("picture");




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

                if(picture.equals("")){
                    profilePic.setImageResource(R.drawable.ic_profile);
                }else{
                    byte[] decodeString = Base64.decode(picture, Base64.NO_WRAP);
                    Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                            decodeString, 0, decodeString.length);
                    profilePic.setImageBitmap(decodebitmap);
                }
                //itemPic.setImageResource(R.drawable.ordericon);
                requestorTV.setText(requestorName );
                productNameTV.setText(productName);
                requirementTV.setText(requirement);
                locationTV.setText(location);
                endTimeTV.setText(endTime);
                priceTV.setText("" + price);
                statusTV.setText(status);

                if(myId != requestorId){
                    new getMyRequestFulfiller().execute(authString);

                }

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bottombar, menu);
        return true;
    }


    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.home_item:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                Intent homeIntent = new Intent (this, MainActivity.class);
                startActivity(homeIntent);
                Toast.makeText(this, "Redirecting to Home Page", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.search_item:
                Toast.makeText(this, "Search is selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.profile_item:
                //Toast.makeText(HomeActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                Toast.makeText(this, "Redirecting to Profile Page.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.my_request_item:
                Intent myRequestIntent = new Intent (this, MyRequestActivity.class);
                startActivity(myRequestIntent);
                Toast.makeText(this, "Redirecting to My Request Page.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.my_fulfill_item:
                Intent myFulfillIntent = new Intent (this, MyFulfillActivity.class);
                startActivity(myFulfillIntent);
                Toast.makeText(this, "Redirecting to My Fulfill Page.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logout_item:

                Intent logoutIntent = new Intent (this, LoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
                finish();


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
