package com.wegot.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class dispute extends AppCompatActivity {

    String username, password, authString, message, origin, err;
    int myId, transactionId;
    EditText disputeFormET;
    Button submitBtn;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute);

        //apply font
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        transactionId = getIntent().getIntExtra("transaction_id", -1);
        origin = getIntent().getStringExtra("origin");

        disputeFormET = (EditText)findViewById(R.id.dipute_form);
        submitBtn = (Button)findViewById(R.id.submit);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = disputeFormET.getText().toString();

                if(message != null && message.trim().length()!= 0){
                    new doDispute().execute(authString);
                }else{
                    disputeFormET.setError("Please describe the issue!");
                }

            }
        });

        ((TextView)findViewById(R.id.dipute)).setTypeface(typeFace);
        disputeFormET.setTypeface(typeFace);
        submitBtn.setTypeface(typeFace);





        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Dispute Form");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class doDispute extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(dispute.this, R.style.MyTheme);

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

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/transaction/" + transactionId+"/dispute/";
            JSONObject jsoin = null;
            try{
                jsoin = new JSONObject();
                jsoin.put("accountId", myId);
                jsoin.put("message", message);


            }catch(JSONException e){
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, jsoin.toString() + basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result) {
                Intent i = new Intent(dispute.this, MainActivity.class);

                if(origin.equals("fulfiller")){
                    i.putExtra("after_dispute_fulfill_tab", 3);
                    i.putExtra("disputed_fulfill_swipe", 2);
                    Toast.makeText(getApplicationContext(), "Complaint Logged!", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    finish();
                }else{
                    i.putExtra("after_dispute_request_tab", 1);
                    i.putExtra("disputed_request_swipe", 2);
                    Toast.makeText(getApplicationContext(), "Complaint Logged!", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    finish();
                }



            }else{

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

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
}
