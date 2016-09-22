package com.wegot.fuyan.fyp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
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

        //font
        TextView myTextView=(TextView)findViewById(R.id.profile_title);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-BoldItalic.ttf");
        myTextView.setTypeface(typeFace);


        updateProfile = (Button)findViewById(R.id.updateprofile_btn);
        Typeface typeFace2=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Regular.ttf");
        updateProfile.setTypeface(typeFace2);


        profileImage = (ImageView)findViewById(R.id.profile_picture);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        profileUsername = pref.getString("username", null);
        profileEmail = pref.getString ("email",null);
        profileContactNumber = pref.getInt("contactnumber",0);
        profilePicture = pref.getString("picture", null);

        if(profilePicture.equals("")){
            profileImage.setImageResource(R.drawable.ic_profile);
        }else{
            byte[] decodeString = Base64.decode(profilePicture, Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            profileImage.setImageBitmap(decodebitmap);
        }

        profileUsernameTV = (TextView)findViewById(R.id.profile_username);
        profileEmailTV = (TextView)findViewById(R.id.profile_email);
        profileContactNumberTV = (TextView)findViewById(R.id.profile_contactNumber);

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
