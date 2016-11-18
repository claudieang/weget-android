package com.weget.fuyan.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    String profileUsername, profileEmail, profilePicture;
    int profileContactNumber;
    TextView profileUsernameTV, profileEmailTV, profileContactNumberTV, requestorRtValue, fulfillerRtValue,
    requestSumTV, fulfillSumTV;
    ImageView profileImage;
    ImageView updateProfile;
    String URL, err, password, authString;
    int myId, requestorRtInt, fulfillerRtInt;
    double requestorRating,requestorRatingNum, fulfillerRating, fulfillerRatingNum, overallRt, requestorRt, fulfillerRt;
    Context mContext;
    int requestTotal, fulfillTotal;

    RatingBar ratingBar1, ratingBar2;
    ProgressBar requestorRatingBar, fulfillerRatingBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //font
        //updateProfile = (ImageView)findViewById(R.id.updateprofile_btn);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setElevation(0);
        profileImage = (ImageView)findViewById(R.id.profile_picture);
        TextView change_pw = (TextView)findViewById(R.id.change_password);
        change_pw.setPaintFlags(change_pw.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        ratingBar1 = (RatingBar)findViewById(R.id.ratingBar1);
        ratingBar2 = (RatingBar)findViewById(R.id.ratingBar2);
        requestorRtValue = (TextView)findViewById(R.id.requestor_rating_value);
        fulfillerRtValue = (TextView)findViewById(R.id.fulfiller_rating_value);
        requestSumTV = (TextView)findViewById(R.id.request_num);
        fulfillSumTV = (TextView)findViewById(R.id.fulfill_num);


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
        profileContactNumberTV.setPaintFlags(change_pw.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


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

        profileContactNumberTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (ProfileActivity.this, UpdateContactActivity.class);
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
            profileImage.setColorFilter(Color.argb(255, 255, 255, 255));;
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
            String url = URL + "account/" + myId+"/profileVO/";

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
                    requestTotal = jso.getInt("requestMade");
                    fulfillTotal = jso.getInt("fulfillMade");

                    //overallRt = (requestorRating + fulfillerRating) / (requestorRatingNum + fulfillerRatingNum);
                    requestorRt = requestorRating / requestorRatingNum;
                    //requestorRtInt = (int) requestorRt;
                    fulfillerRt = fulfillerRating / fulfillerRatingNum;
                    //fulfillerRtInt = (int)fulfillerRt;
                    if(Double.isNaN(requestorRt)){
                        requestorRt = 0;
                    }
                    if(Double.isNaN(fulfillerRt)){
                        fulfillerRt = 0;
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


            if(result){



                ratingBar1.setRating(Float.parseFloat(Double.toString(requestorRt)));
                ratingBar2.setRating(Float.parseFloat(Double.toString(fulfillerRt)));
                String pad1 = Double.toString(requestorRt)+"00";
                String pad2 = Double.toString(fulfillerRt)+"00";
                requestorRtValue.setText(pad1.substring(0,(pad1.indexOf('.')+2)));
                fulfillerRtValue.setText(pad2.substring(0,(pad1.indexOf('.')+2)));
                requestSumTV.setText(String.valueOf(requestTotal));
                fulfillSumTV.setText(String.valueOf(fulfillTotal));





            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }


}
