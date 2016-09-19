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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class RequestFulfillerDetailsActivity extends AppCompatActivity {

    Request request;
    Account account;
    TextView fulfillerNameTV, fulfillerEmailTV, fulfillerContactTV;
    ImageView fulfillerPicIV;
    Button acceptFulfillerBtn;

    String fulfillerName, fulfillerEmail, fulfillerPic, fulfillerContactS, productName, requirement,
    location, startTime, endTime, username, password, authString, err, fulfillStatus, requestString;
    int fulfillerContact, requestorId, fulfillerId, postal, duration, fulfillId, requestId;
    double price;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_fulfiller_details);

        //font
        TextView myTextView=(TextView)findViewById(R.id.request_fulfiller_title);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");
        myTextView.setTypeface(typeFace);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        authString  = username + ":" + password;

        fulfillerNameTV = (TextView)findViewById(R.id.request_fulfiller_name);
        fulfillerEmailTV = (TextView)findViewById(R.id.request_fulfiller_email);
        fulfillerContactTV = (TextView)findViewById(R.id.request_fulfiller_contact);
        fulfillerPicIV = (ImageView)findViewById(R.id.request_fulfiller_image);
        acceptFulfillerBtn = (Button)findViewById(R.id.select_fulfiller_btn);
        Typeface typeFace2=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Regular.ttf");
        acceptFulfillerBtn.setTypeface(typeFace2);


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
            fulfillerPicIV.setImageResource(R.drawable.ic_profile);
        }else{
            byte[] decodeString = Base64.decode(fulfillerPic, Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            fulfillerPicIV.setImageBitmap(decodebitmap);
        }

        fulfillerNameTV.setText("Name: " +fulfillerName);
        fulfillerEmailTV.setText("Email: "+fulfillerEmail);
        fulfillerContactTV.setText("Contact: " + fulfillerContactS);

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

    }


    private class getRequestsv extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(RequestFulfillerDetailsActivity.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();


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
