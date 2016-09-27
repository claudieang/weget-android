package com.weget.fuyan.fyp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class UpdateProfileActivity extends AppCompatActivity {

    String username;
    TextView updateTitle;
    ImageButton updatePasswordBtn, updateContactBtn,uploadImageBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        //font
        //TextView myTextView=(TextView)findViewById(R.id.updateprofile_title);
        TextView updatepassword=(TextView)findViewById(R.id.textView1);
        TextView updatecontact=(TextView)findViewById(R.id.textView6);
        TextView updatepicture=(TextView)findViewById(R.id.textView4);
        //myTextView.setTypeface(typeFace);


        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = pref.edit();
        username = pref.getString("username", null);

        updatePasswordBtn = (ImageButton)findViewById(R.id.update_password_btn);
        //updateEmailBtn = (Button)findViewById(R.id.update_email_btn);
        updateContactBtn = (ImageButton)findViewById(R.id.update_contact_btn);
        uploadImageBtn = (ImageButton)findViewById(R.id.upload_image_btn);
        updateTitle = (TextView)findViewById(R.id.update_username);
        updateTitle.setText(username);

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
}
