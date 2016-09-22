package com.wegot.fuyan.fyp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    String profileUsername, profileEmail, profilePicture;
    int profileContactNumber;
    TextView profileUsernameTV, profileEmailTV, profileContactNumberTV;
    ImageView profileImage;
    ImageView updateProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //font
        //updateProfile = (ImageView)findViewById(R.id.updateprofile_btn);


        profileImage = (ImageView)findViewById(R.id.profile_picture);
        TextView change_pw = (TextView)findViewById(R.id.change_password);
        change_pw.setPaintFlags(change_pw.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


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

        String displayUserName = profileUsername;
        String displayEmail = profileEmail;
        String displayContactNumber = ""+profileContactNumber;

        profileUsernameTV.setText(displayUserName);
        profileEmailTV.setText(displayEmail);
        profileContactNumberTV.setText(displayContactNumber);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (ProfileActivity.this, UpdateProfileActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
