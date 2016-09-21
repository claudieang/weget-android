package com.wegot.fuyan.fyp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    String profileUsername, profileEmail, profilePicture;
    int profileContactNumber;
    TextView profileUsernameTV, profileEmailTV, profileContactNumberTV;
    ImageView profileImage;
    Button updateProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

        //font
        TextView myTextView=(TextView)findViewById(R.id.profile_title);
        myTextView.setTypeface(typeFaceBold);


        updateProfile = (Button)findViewById(R.id.updateprofile_btn);
        profileImage = (ImageView)findViewById(R.id.profile_picture);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        profileUsername = pref.getString("username", null);
        profileEmail = pref.getString ("email",null);
        profileContactNumber = pref.getInt("contactnumber",0);
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

        profileUsernameTV = (TextView)findViewById(R.id.profile_username);
        profileEmailTV = (TextView)findViewById(R.id.profile_email);
        profileContactNumberTV = (TextView)findViewById(R.id.profile_contactNumber);

        profileUsernameTV.setTypeface(typeFace);
        profileEmailTV.setTypeface(typeFace);
        profileContactNumberTV.setTypeface(typeFace);
        updateProfile.setTypeface(typeFace);

        String displayUserName = "User Name: " + profileUsername;
        String displayEmail = "Email: " + profileEmail;
        String displayContactNumber = "Contact Number: " + profileContactNumber;


        profileUsernameTV.setText(displayUserName);
        profileEmailTV.setText(displayEmail);
        profileContactNumberTV.setText(displayContactNumber);

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (ProfileActivity.this, UpdateProfileActivity.class);
                startActivity(i);
                finish();

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

}
