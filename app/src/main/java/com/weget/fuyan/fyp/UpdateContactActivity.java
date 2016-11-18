package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateContactActivity extends AppCompatActivity {

    String username;
    String password,email,err, updatedContact, contactNoS, idString, picture;
    int contactNo,updatedContactNo, id;
    private Context mContext;
    TextView updateTitle;
    Button updateContactBtn;
    //EditText updateContact;
    SharedPreferences.Editor editor = null;
    String URL;
    private EditText contactNoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        URL = getString(R.string.webserviceurl);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        email = pref.getString("email",null);
        contactNo = pref.getInt("contactnumber",0);
        contactNoS = String.valueOf(contactNo);
        id = pref.getInt("id",0);
        idString = String.valueOf(id);
        picture = pref.getString("picture", null);

        contactNoBtn = (EditText) findViewById(R.id.update_contact);
        Button updateBtn = (Button)findViewById(R.id.create_btn);
        updateBtn.setText("UPDATE");

        contactNoBtn.setText(contactNo + "");

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedContact = contactNoBtn.getText().toString();

                if(updatedContact != null && updatedContact.trim().length() > 0){
                    updatedContact = updatedContact.trim().replaceAll("\\s", "");
                    if(updatedContact.matches("[0-9]+")){
                        if(!updatedContact.equals(contactNoS)){
                            boolean error = false;
                            try {
                                updatedContactNo = Integer.parseInt(updatedContact);
                            }catch(Exception e){
                                error = true;
                                contactNoBtn.setError("Invalid contact!");
                            }
                            if(!error) {
                                new updateValue().execute(idString);
                            }

                        }else{
                            contactNoBtn.setError("Same as old contact!");
                        }

                    }else{
                        contactNoBtn.setError("Invalid contact number!");
                    }

                }else{
                    contactNoBtn.setError("Contact number is required!");
                }


            }
        });

        ImageButton close_btn = (ImageButton)findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //HTTP Connection
    private class updateValue extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(UpdateContactActivity.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();


        }

        @Override
        protected Boolean doInBackground(String... params) {
            String authString  = username + ":" + password;
            //authString = "admin:password";
            final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            Log.d ("Basic Authentication", basicAuth);

            boolean success = false;
            String url = URL + "account/" +params[0] +"/";
            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("id", id);
                jsoin.put("username", username);
                jsoin.put("password", password);
                jsoin.put("contactNo", updatedContactNo);
                jsoin.put("email", email);
                jsoin.put("fulfiller", "false");
                jsoin.put("picture", picture);
                jsoin.put("enabled", true);


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
                Toast.makeText(getBaseContext(), "Contact Update Success!", Toast.LENGTH_LONG).show();
                editor.putInt("contactnumber",updatedContactNo);
                editor.commit();
                Intent i = new Intent(UpdateContactActivity.this, ProfileActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }



}
