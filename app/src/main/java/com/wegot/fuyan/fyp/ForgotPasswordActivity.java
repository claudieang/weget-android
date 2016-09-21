package com.wegot.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText forgotEmailET;
    Button retrieveAccountBtn;
    String email, err;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

        forgotEmailET = (EditText)findViewById(R.id.forget_email_et);
        retrieveAccountBtn = (Button)findViewById(R.id.retrieve_account_btn);

        forgotEmailET.setTypeface(typeFace);
        retrieveAccountBtn.setTypeface(typeFace);

        retrieveAccountBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                email = forgotEmailET.getText().toString();

                //Call ws
                new resetPassword().execute(email);



            }
        });



    }

    private class resetPassword extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/reset/";
            JSONObject jsoin = null;


            try {

                jsoin = new JSONObject();
                jsoin.put("email",params[0] );

            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostJson(mContext, url, jsoin.toString());
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

            if(result){
                Intent i = new Intent (ForgotPasswordActivity.this,LoginActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Email Sent!", Toast.LENGTH_SHORT).show();


            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
