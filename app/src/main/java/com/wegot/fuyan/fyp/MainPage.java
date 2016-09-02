package com.wegot.fuyan.fyp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainPage extends AppCompatActivity {
    ImageButton addRequest,homepage,requestbt,fulfillbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //change font
        TextView myTextView=(TextView)findViewById(R.id.textview2);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");
        myTextView.setTypeface(typeFace);

        addRequest = (ImageButton)findViewById(R.id.addrequest);
        homepage = (ImageButton)findViewById(R.id.homepage);
        requestbt = (ImageButton)findViewById(R.id.request);
        fulfillbt = (ImageButton)findViewById(R.id.fulfill);


        addRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (MainPage.this, CreateRequestActivity.class);
                startActivity(i);

            }
        });

        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (MainPage.this, HomeActivity.class);
                startActivity(i);

            }
        });

        requestbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (MainPage.this, MyRequestActivity.class);
                startActivity(i);

            }
        });

        fulfillbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (MainPage.this, MyFulfillActivity.class);
                startActivity(i);

            }
        });
    }

}
