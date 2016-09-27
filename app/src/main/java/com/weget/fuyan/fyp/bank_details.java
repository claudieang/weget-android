package com.weget.fuyan.fyp;

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

import java.io.Serializable;

public class bank_details extends AppCompatActivity {

    EditText accountHolderNameET, accountBankNameET, accountNumberET;
    Button submitBtn;
    String username,password, authString, accountHolderName, accountBankName, accountNumber, err;
    int myId, emptyBank;
    Context mContext;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Bank Details");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //apply font
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

        emptyBank = getIntent().getIntExtra("empty_bank",0);
        request = (Request) getIntent().getSerializableExtra("selected_request");

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        accountHolderNameET = (EditText)findViewById(R.id.payee_name_detail);
        accountBankNameET = (EditText)findViewById(R.id.payee_bank_detail);
        accountNumberET = (EditText)findViewById(R.id.account_number_detail);
        submitBtn = (Button)findViewById(R.id.submit_button);

        //((TextView)findViewById(R.id.title1)).setTypeface(typeFaceBold);
        //((TextView)findViewById(R.id.title2)).setTypeface(typeFaceBold);
        ((TextView)findViewById(R.id.payee_name)).setTypeface(typeFace);
        accountHolderNameET.setTypeface(typeFace);
        ((TextView)findViewById(R.id.bank_name)).setTypeface(typeFace);
        accountBankNameET.setTypeface(typeFace);
        ((TextView)findViewById(R.id.account_number)).setTypeface(typeFace);
        accountNumberET.setTypeface(typeFace);
        submitBtn.setTypeface(typeFace);



        accountHolderNameET.setText(username);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountHolderName = accountHolderNameET.getText().toString();
                accountBankName = accountBankNameET.getText().toString();
                accountNumber = accountNumberET.getText().toString();

                if(accountHolderName!=null && accountHolderName.trim().length()!= 0){
                    if(accountBankName!=null && accountBankName.trim().length()!=0){
                        if(accountNumber != null && accountNumber.trim().length()!=0){
                            //accountNum = Integer.parseInt(accountNumber);
                            new setBank().execute(authString);

                        }else{
                            accountNumberET.setError("Please enter account number!");
                        }
                    }else{
                        accountBankNameET.setError("Please enter bank name!");
                    }
                }else{
                    accountHolderNameET.setError("Please enter your name!");
                }

                //new getBank().execute(authString);
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

    private class setBank extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(bank_details.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();


        }

        @Override
        protected Boolean doInBackground(String... params) {
            String authString  = params[0];
            //authString = "admin:password";
            final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            //Log.d ("Basic Authentitaion", basicAuth);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/account/" + myId + "/bank/";
            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("userId", myId);
                jsoin.put("accountHolder", accountHolderName);
                jsoin.put("accountNumber", accountNumber);
                jsoin.put("bankName", accountBankName);


            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, jsoin.toString()+ basicAuth);
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
                if(emptyBank == 1){
                    Toast.makeText(getBaseContext(), "Bank Created!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(bank_details.this,FulfillviewRequestDetails.class);
                    i.putExtra("selected_request", (Serializable) request);
                    startActivity(i);
                    finish();

                }else if(emptyBank == 2){
                    Toast.makeText(getBaseContext(), "Bank Created!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(bank_details.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }
}