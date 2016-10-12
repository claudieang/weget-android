package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class UpdatePasswordActivity extends AppCompatActivity {

    String username;
    String password,email,err, updatedOldPassword, updatedNewPassword,
            updatedConfirmPassword, contactNoS, idString, picture;
    int contactNo, id;
    private Context mContext;
    TextView updateTitle;
    Button updatePasswordBtn;
    EditText updateOldPassword,updateNewPassword, updateConfirmPassword;
    SharedPreferences.Editor editor = null;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");

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


        updateOldPassword = (EditText)findViewById(R.id.update_old_password);
        updateNewPassword = (EditText)findViewById(R.id.update_new_password);
        updateConfirmPassword = (EditText)findViewById(R.id.update_confirm_password);
        updatePasswordBtn = (Button)findViewById(R.id.create_btn);
        updatePasswordBtn.setText("UPDATE");

        //updateTitle = (TextView)findViewById(R.id.update_username);
        //updateTitle.setText("User Name: " + username);
        updateOldPassword.setHint("Old password");
        updateNewPassword.setHint("New password");
        updateConfirmPassword.setHint("Confirm password");

//        ((TextView)findViewById(R.id.title1)).setTypeface(typeFaceBold);
//        ((TextView)findViewById(R.id.title2)).setTypeface(typeFaceBold);
        //updateTitle.setTypeface(typeFace);
        //((TextView)findViewById(R.id.textView1)).setTypeface(typeFace);
        updateOldPassword.setTypeface(typeFace);
        updateNewPassword.setTypeface(typeFace);
        //((TextView)findViewById(R.id.textView3)).setTypeface(typeFace);
        //updateConfirmPassword.setTypeface(typeFace);


        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedOldPassword = updateOldPassword.getText().toString();
                updatedNewPassword = updateNewPassword.getText().toString();
                updatedConfirmPassword = updateConfirmPassword.getText().toString();


                if(updatedOldPassword != null && updatedOldPassword.trim().length() >0){
                    if(updatedOldPassword.equals(password)){

                        if(updatedNewPassword != null && updatedNewPassword.trim().length() > 0){

                            if(updatedNewPassword.trim().length() >= 8 && updatedNewPassword.indexOf(" ") == -1){
                                if(!updatedNewPassword.equals(password)){
                                    if(updatedConfirmPassword != null && updatedConfirmPassword.trim().length()>0){
                                        if(updatedConfirmPassword.equals(updatedNewPassword)){
                                            new updateValue().execute(idString);

                                        }else{
                                            updateConfirmPassword.setError("Password mismatch!");
                                        }
                                    }else{
                                        updateConfirmPassword.setError("Confirm password is required!");
                                    }
                                }else{
                                    updateNewPassword.setError("Same as old password!");
                                }

                            }else{
                                updateNewPassword.setError("Invalid password!");
                            }

                        }else{
                            updateNewPassword.setError("New password is required!");
                        }

                    }else{
                        updateOldPassword.setError("Invalid password!");
                    }

                }else{
                    updateOldPassword.setError("Old password is required!");
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

        ProgressDialog dialog = new ProgressDialog(UpdatePasswordActivity.this, R.style.MyTheme);

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
            Log.d ("Basic Authentitaion", basicAuth);

            boolean success = false;
            String url = URL + "account/" +params[0] +"/";
            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("id", id);
                jsoin.put("username",username);
                jsoin.put("password", updatedNewPassword);
                jsoin.put("contactNo", contactNo);
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
                Toast.makeText(getBaseContext(), "Password Update Success!", Toast.LENGTH_LONG).show();
                editor.putString("password",updatedNewPassword);
                editor.commit();
                //Intent i = new Intent(UpdatePasswordActivity.this, ProfileActivity.class);
                //startActivity(i);

                finish();
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }
}
