package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class update_bank_details extends AppCompatActivity {

    EditText accountHolderNameET, accountBankNameET, accountNumberET;
    Button updateBankBtn;
    String accountHolderName, accountBankName, accountNumber, username, password, authString, bankUserName, bankName,
            err, bankAccNumber;
    int  myId, bankId, bankUserId;
    Context mContext;
    Boolean bank = true;
    final String URL = getString(R.string.webserviceurl);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bank_details);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Update Bank Details");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        accountHolderNameET = (EditText)findViewById(R.id.payee_name_detail);
        accountBankNameET = (EditText)findViewById(R.id.payee_bank_detail);
        accountNumberET = (EditText)findViewById(R.id.account_number_detail);
        updateBankBtn = (Button)findViewById(R.id.update_button);

        ((TextView)findViewById(R.id.payee_name)).setTypeface(typeFace);
        accountHolderNameET.setTypeface(typeFaceLight);
        ((TextView)findViewById(R.id.bank_name)).setTypeface(typeFace);
        accountBankNameET.setTypeface(typeFaceLight);
        ((TextView)findViewById(R.id.account_number)).setTypeface(typeFace);
        accountNumberET.setTypeface(typeFaceLight);

        updateBankBtn.setTypeface(typeFace);


        new getBank().execute(authString);

        updateBankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountHolderName = accountHolderNameET.getText().toString();
                accountBankName = accountBankNameET.getText().toString();
                accountNumber = accountNumberET.getText().toString();

                if(accountHolderName!=null && accountHolderName.trim().length()!= 0){
                    if(accountBankName!=null && accountBankName.trim().length()!=0){
                        if(accountNumber != null && accountNumber.trim().length()!=0){
                            //accountNum = Integer.parseInt(accountNumber);
                            new updateBank().execute(authString);

                        }else{
                            accountNumberET.setError("Please enter account number!");
                        }
                    }else{
                        accountBankNameET.setError("Please enter bank name!");
                    }
                }else{
                    accountHolderNameET.setError("Please enter your name!");
                }
            }
        });


    }

    private class getBank extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(update_bank_details.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {

            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "account/"+ myId+"/bank/";
            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;
                try {
                    JSONObject jso = new JSONObject(rst);
                    bankId = jso.getInt("id");
                    bankUserId = jso.getInt("userId");
                    bankUserName = jso.getString("accountHolder");
                    bankAccNumber = jso.getString("accountNumber");
                    bankName = jso.getString("bankName");
                }catch (JSONException e){
                    e.printStackTrace();
                    success = false;
                    bank = false;
                }
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result){
                bank = true;
                accountHolderNameET.setText(bankUserName);
                accountBankNameET.setText(bankName);
                accountNumberET.setText(String.valueOf(bankAccNumber));

            }else {
                if(!bank){

                    Intent i = new Intent (update_bank_details.this, bank_details.class);
                    i.putExtra("empty_bank", 2);
                    startActivity(i);
                    finish();

                }

            }

        }
    }

    private class updateBank extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(update_bank_details.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {

            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "account/" + myId + "/bank/";

            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("id", bankId);
                jsoin.put("userId",myId);
                jsoin.put("accountHolder",accountHolderName);
                jsoin.put("accountNumber",accountNumber);
                jsoin.put("bankName", accountBankName);




            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPutBasicAuthentication(mContext, url, jsoin.toString()+ basicAuth);
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
            if(result) {
                Toast.makeText(getBaseContext(), "Bank Update!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(update_bank_details.this, MainActivity.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
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
