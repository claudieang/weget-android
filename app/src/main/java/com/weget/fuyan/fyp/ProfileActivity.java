package com.weget.fuyan.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    String profileUsername, profileEmail, profilePicture;
    int profileContactNumber;
    TextView profileUsernameTV, profileEmailTV, profileContactNumberTV, requestorRtValue, fulfillerRtValue;
    ImageView profileImage;
    ImageView updateProfile;
    String URL, err, password, authString;
    int myId, requestorRtInt, fulfillerRtInt;
    double requestorRating,requestorRatingNum, fulfillerRating, fulfillerRatingNum, overallRt, requestorRt, fulfillerRt;
    Context mContext;

    RatingBar ratingBar;
    ProgressBar requestorRatingBar, fulfillerRatingBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //font
        //updateProfile = (ImageView)findViewById(R.id.updateprofile_btn);
        getSupportActionBar().setTitle("Profile");

        profileImage = (ImageView)findViewById(R.id.profile_picture);
        TextView change_pw = (TextView)findViewById(R.id.change_password);
        change_pw.setPaintFlags(change_pw.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar2);
        requestorRatingBar = (ProgressBar)findViewById(R.id.requestor_rating_bar);
        fulfillerRatingBar = (ProgressBar)findViewById(R.id.fulfiller_rating_bar);
        requestorRtValue = (TextView)findViewById(R.id.requestor_rating_value);
        fulfillerRtValue = (TextView)findViewById(R.id.fulfiller_rating_value);


        URL = getString(R.string.webserviceurl);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        profileUsername = pref.getString("username", null);
        password = pref.getString("password", null);
        profileEmail = pref.getString ("email",null);
        myId = pref.getInt("id",-1);
        profileContactNumber = pref.getInt("contactnumber",0);
        profilePicture = pref.getString("picture", null);
        authString  = profileUsername + ":" + password;

        if(profilePicture.equals("")){

            profileImage.setImageResource(R.drawable.ic_account_circle_black_48dp);
        }else{


            //this.dpIV.setImageDrawable(roundDrawable);
            byte[] decodeString = Base64.decode(profilePicture, Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodebitmap);
            roundDrawable.setCircular(true);
            profileImage.setImageDrawable(roundDrawable);
        }

        profileUsernameTV = (TextView)findViewById(R.id.profile_username);
        profileEmailTV = (TextView)findViewById(R.id.profile_email);
        profileContactNumberTV = (TextView)findViewById(R.id.profile_contactNumber);

        String displayUserName = profileUsername;
        String displayEmail = profileEmail;
        String displayContactNumber = ""+profileContactNumber;

        profileUsernameTV.setText(displayUserName);
        profileEmailTV.setText(displayEmail);
        profileContactNumberTV.setText(displayContactNumber);

        new getMyRating().execute(authString);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (ProfileActivity.this, UploadImageActivity.class);
                startActivity(i);
            }
        });

        change_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (ProfileActivity.this, UpdatePasswordActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //font
        //updateProfile = (ImageView)findViewById(R.id.updateprofile_btn);
        getSupportActionBar().setTitle("Profile");

        profileImage = (ImageView)findViewById(R.id.profile_picture);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        profilePicture = pref.getString("picture", null);

        if(profilePicture.equals("")){

            profileImage.setImageResource(R.drawable.ic_account_circle_black_48dp);
        }else{


            //this.dpIV.setImageDrawable(roundDrawable);
            byte[] decodeString = Base64.decode(profilePicture, Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decodebitmap);
            roundDrawable.setCircular(true);
            profileImage.setImageDrawable(roundDrawable);
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

    private class getMyRating extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {



        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "rating/" + myId+"/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {


                try {

                    JSONObject jso = new JSONObject(rst);

                    requestorRating = jso.getDouble("requestTotal");
                    requestorRatingNum = jso.getDouble("requestNo");
                    fulfillerRating = jso.getDouble("fulfillTotal");
                    fulfillerRatingNum = jso.getDouble("fulfillNo");

                    overallRt = (requestorRating + fulfillerRating) / (requestorRatingNum + fulfillerRatingNum);
                    requestorRt = ((requestorRating / requestorRatingNum) / 5) * 100;
                    requestorRtInt = (int) requestorRt;
                    fulfillerRt = ((fulfillerRating / fulfillerRatingNum) / 5) * 100;
                    fulfillerRtInt = (int)fulfillerRt;


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


                ratingBar.setRating(Float.parseFloat(Double.toString(overallRt)));


                requestorRatingBar.setMax(100);
                requestorRatingBar.setProgress(0);
                requestorRatingBar.setProgress(requestorRtInt);
                fulfillerRatingBar.setMax(100);
                fulfillerRatingBar.setProgress(0);
                fulfillerRatingBar.setProgress(fulfillerRtInt);
                requestorRtValue.setText(String.valueOf(requestorRtInt));
                fulfillerRtValue.setText(String.valueOf(fulfillerRtInt));









            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }


}
