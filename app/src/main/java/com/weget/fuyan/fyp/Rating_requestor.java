package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Rating_requestor extends AppCompatActivity {
    Request request;
    Account account;
    TextView userTitle, userName, productName, productPrice, rateYourTitle;
    ImageView userPic;
    RatingBar ratingBar;
    String username, password, authString, profilePicture, baseURL, ratingType, err;
    int myId, requestId , requestorId, acctId;
    Button submitBtn, skipBtn;
    Float ratingNum;
    String ratingString;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_requestor);

        baseURL = getString(R.string.webserviceurl);
        userTitle = (TextView)findViewById(R.id.request_fulfiller_name);
        userName = (TextView)findViewById(R.id.username);
        userPic = (ImageView)findViewById(R.id.request_fulfiller_image);
        productName = (TextView)findViewById(R.id.product_name);
        productPrice = (TextView)findViewById(R.id.price);
        rateYourTitle = (TextView)findViewById(R.id.past_txn);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        submitBtn = (Button)findViewById(R.id.submit);
        skipBtn = (Button)findViewById(R.id.skip_button);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;


        request = (Request) getIntent().getSerializableExtra("selected_request");
        account = (Account) getIntent().getSerializableExtra("user_to_rate");
        acctId = account.getId();
        requestId = request.getId();
        requestorId = request.getRequestorId();
        profilePicture = account.getPicture();

        if(requestorId == myId){
            ratingType = "fulfill";
            userTitle.setText("Fulfiller");
            userName.setText(account.getUsername());
            if(profilePicture.equals("")){

                userPic.setImageResource(R.drawable.ic_account_circle_black_48dp);
            }else{


                //this.dpIV.setImageDrawable(roundDrawable);
                byte[] decodeString = Base64.decode(profilePicture, Base64.NO_WRAP);
                Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                        decodeString, 0, decodeString.length);
                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodebitmap);
                roundDrawable.setCircular(true);
                userPic.setImageDrawable(roundDrawable);
            }
            productName.setText(request.getProductName());
            productPrice.setText(String.valueOf(request.getPrice()));
            rateYourTitle.setText("Rate your fulfiller:");

            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ratingNum = ratingBar.getRating();
                    ratingString = Float.toString(ratingNum);
                    Log.d("Float 1:==", ""+ratingNum);
                    Log.d("String 1:==", "" + ratingString);
                    new createRating().execute(authString);

                }
            });

            skipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(Rating_requestor.this, MainActivity.class);
                    startActivity(i);
                    finish();

                }
            });


        }else{

            ratingType = "request";
            userTitle.setText("Requestor");
            userName.setText(account.getUsername());
            if(profilePicture.equals("")){

                userPic.setImageResource(R.drawable.ic_account_circle_black_48dp);
            }else{


                //this.dpIV.setImageDrawable(roundDrawable);
                byte[] decodeString = Base64.decode(profilePicture, Base64.NO_WRAP);
                Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                        decodeString, 0, decodeString.length);
                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodebitmap);
                roundDrawable.setCircular(true);
                userPic.setImageDrawable(roundDrawable);
            }
            productName.setText(request.getProductName());
            productPrice.setText(String.valueOf(request.getPrice()));
            rateYourTitle.setText("Rate your requestor:");

            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ratingNum = ratingBar.getRating();
                    ratingString = Float.toString(ratingNum);
                    Log.d("Float 2:==", ""+ratingNum);
                    Log.d("String 2:==", "" + ratingString);
                    new createRating().execute(authString);

                }
            });
            skipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(Rating_requestor.this, MainActivity.class);
                    startActivity(i);
                    finish();

                }
            });

        }

    }

    private class createRating extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(Rating_requestor.this, R.style.MyTheme);

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
            String url = baseURL + "rating/" + acctId + "/";
            JSONObject jsoin = null;


            try {

                jsoin = new JSONObject();
                jsoin.put("type", ratingType);
                jsoin.put("value",ratingString);


            } catch (JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, jsoin.toString() + basicAuth);
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
            if (result) {
                Toast.makeText(getBaseContext(), "User Rated!", Toast.LENGTH_LONG).show();
                //Intent i = new Intent(CreateRequestActivity.this, HomeActivity.class);
                Intent i = new Intent(Rating_requestor.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }
}
