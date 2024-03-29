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
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Dispute extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String username, password, authString, message, origin, err, reason;
    int myId, transactionId, usertype;
    EditText disputeFormET;
    Button submitBtn;
    Context mContext;
    String URL;
    Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute);

        //apply font
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

        URL = getString(R.string.webserviceurl);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt ("id", 0);
        authString  = username + ":" + password;

        transactionId = getIntent().getIntExtra("transaction_id", -1);
        origin = getIntent().getStringExtra("origin");
        usertype = getIntent().getIntExtra("user_type",-1);

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

        //((TextView)findViewById(R.id.dipute)).setTypeface(typeFace);
        disputeFormET.setTypeface(typeFace);
        submitBtn.setTypeface(typeFace);





        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //reason dropdown menu
        dropdown = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[0];

        //zero means fulfiller, 1 means requestor
        Log.d("usertype","usertype is : " + usertype);
        if(usertype==0){
            items = new String[]{"Requestor refuses confirmation", "Requestor is difficult to handle", "Requestor refuses to accept item", "Requestor was unresponsive"};
        } else {
            items = new String[]{"Fulfiller did not turn up", "Fulfiller came very late", "Fulfiller was unresponsive", "Service not up to standard"};
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items){


            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);
                ((TextView) v).setTextColor(
                        getResources().getColorStateList(R.color.black)
                );

                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                v.setBackgroundResource(R.drawable.barbase);

                ((TextView) v).setTextColor(
                        getResources().getColorStateList(R.color.black)
                );

                ((TextView) v).setGravity(Gravity.CENTER);

                return v;
            }



        };
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        dropdown.setSelection(position);
        String selState = dropdown.getSelectedItem().toString();
        Log.d("string <<< ", selState);
        reason = selState;


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        reason = "Fulfiller did not turn up";
    }

    private class doDispute extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(Dispute.this, R.style.MyTheme);

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
            String url = URL + "transaction/" + transactionId+"/dispute/";
            JSONObject jsoin = null;
            try{
                jsoin = new JSONObject();
                jsoin.put("accountId", myId);
                jsoin.put("message", message);
                jsoin.put("type",origin);
                jsoin.put("reason",reason);


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
                Intent i = new Intent(Dispute.this, MainActivity.class);

                if(origin.equals("fulfiller")){
                    i.putExtra("after_dispute_fulfill_tab", 3);
                    i.putExtra("disputed_fulfill_swipe", 2);
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(getApplicationContext(), "Complaint Logged!", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    //finish();
                }else{
                    i.putExtra("after_dispute_request_tab", 1);
                    i.putExtra("disputed_request_swipe", 2);
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(getApplicationContext(), "Complaint Logged!", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    //finish();
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
