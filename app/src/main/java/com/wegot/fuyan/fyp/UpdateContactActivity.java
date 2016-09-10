package com.wegot.fuyan.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.EditText;
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
    EditText updateContact;
    SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);

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

        updateContactBtn = (Button)findViewById(R.id.confirm_update_contact_btn);
        updateContact = (EditText)findViewById(R.id.update_contact_txt);
        updateTitle = (TextView)findViewById(R.id.update_username);
        updateTitle.setText("User Name: " + username);
        updateContact.setHint(contactNoS);


        updateContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedContact = updateContact.getText().toString();

                if(updatedContact != null && updatedContact.trim().length() > 0){
                    updatedContact = updatedContact.trim().replaceAll("\\s", "");
                    if(updatedContact.matches("[0-9]+")){
                        if(!updatedContact.equals(contactNoS)){
                            updatedContactNo = Integer.parseInt(updatedContact);
                            new updateValue().execute(idString);

                        }else{
                            updateContact.setError("Same as old contact!");
                        }

                    }else{
                        updateContact.setError("Invalid contact number!");
                    }

                }else{
                    updateContact.setError("Contact number is required!");
                }


            }
        });
    }

    //HTTP Connection
    private class updateValue extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String authString  = username + ":" + password;
            //authString = "admin:password";
            final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            Log.d ("Basic Authentitaion", basicAuth);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/account/" +params[0] +"/";
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
            if(result){
                Toast.makeText(getBaseContext(), "Contact Update Success!", Toast.LENGTH_LONG).show();
                editor.putInt("contactnumber",updatedContactNo);
                editor.commit();
                Intent i = new Intent(UpdateContactActivity.this, ProfileActivity.class);
                startActivity(i);
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }

    //Menu Bar

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
