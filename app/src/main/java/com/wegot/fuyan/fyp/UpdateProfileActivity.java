package com.wegot.fuyan.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    String username;
    TextView updateTitle;
    ImageButton updatePasswordBtn, updateContactBtn,uploadImageBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        //font
        TextView myTextView=(TextView)findViewById(R.id.updateprofile_title);
        TextView updatepassword=(TextView)findViewById(R.id.textView1);
        TextView updatecontact=(TextView)findViewById(R.id.textView6);
        TextView updatepicture=(TextView)findViewById(R.id.textView4);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");
        Typeface typeFace2=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Italic.ttf");
        Typeface typeFace3=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Regular.ttf");
        myTextView.setTypeface(typeFace);
        updatepassword.setTypeface(typeFace3);
        updatecontact.setTypeface(typeFace3);
        updatepicture.setTypeface(typeFace3);




        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = pref.edit();
        username = pref.getString("username", null);

        updatePasswordBtn = (ImageButton)findViewById(R.id.update_password_btn);
        //updateEmailBtn = (Button)findViewById(R.id.update_email_btn);
        updateContactBtn = (ImageButton)findViewById(R.id.update_contact_btn);
        uploadImageBtn = (ImageButton)findViewById(R.id.upload_image_btn);
        updateTitle = (TextView)findViewById(R.id.update_username);
        updateTitle.setTypeface(typeFace2);
        updateTitle.setText("User Name: " + username);

        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (UpdateProfileActivity.this, UpdatePasswordActivity.class);
                startActivity(i);
            }
        });

        /*
        updateEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(i);
            }
        });
        */
        updateContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (UpdateProfileActivity.this, UpdateContactActivity.class);
                startActivity(i);
            }
        });

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (UpdateProfileActivity.this, UploadImageActivity.class);
                startActivity(i);
            }
        });


    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bottombar, menu);
        return true;
    }


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
