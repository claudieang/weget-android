package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    Button b1, b2;
    EditText ed1, ed2;
    TextView forgotPw;
    Context ctx = this;
    String username, password, dbUsername, dbPassword, dbEmail, dbProfilePic, err,token;
    int dbID, dbContactNumber;
    private Context mContext;
    private static final String TAG = "LoginActivity";
    final Context context = this;
    String URL = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ImageView logo_iv = (ImageView)findViewById(R.id.applogo_imageview);
        //logo_iv.setImageResource(R.drawable.weget_logo);

        URL = getString(R.string.webserviceurl);
        b1 = (Button)findViewById(R.id.login_button);
        ed1 = (EditText)findViewById(R.id.login_text);
        ed2 = (EditText)findViewById(R.id.password_text);
        b2 = (Button)findViewById(R.id.register_button);
        forgotPw = (TextView)findViewById(R.id.forgetPassword);

        b1.setTransformationMethod(null);
        b2.setTransformationMethod(null);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        b1.setTypeface(typeFace);
        b2.setTypeface(typeFace);


//        forgotPw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent (LoginActivity.this, ForgotPasswordActivity.class);
//                startActivity(i);
//
//            }
//        });
        //check if logged in boolean = true
        //if true, then log in
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        username = pref.getString("username", null);

        if(username != null){
            //password = pref.getString("password", "");
            //instantiate variables used by SendBird
            dbUsername = username;
            dbID = pref.getInt("id", 0);
            dbProfilePic = pref.getString("picture", null);
            initSendBird();

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);

            finish();
            //new getValues().execute(username);
        }


        //mButton = (Button) findViewById(R.id.openUserInputDialog);
        forgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Log.d("Error: ", "in click");
                //LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
                LayoutInflater inflater = getLayoutInflater();

                View mView = inflater.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
                alertDialogBuilderUserInput.setView(mView);

                final EditText email = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // get user input here

                                new resetPassword().execute(email.getText().toString());
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                alertDialogAndroid.getButton(alertDialogAndroid.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                alertDialogAndroid.getButton(alertDialogAndroid.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = ed1.getText().toString().trim();
                password = ed2.getText().toString().trim();

                if (username.length() > 0 && password.length() > 0) {

                    new getValues().execute(username);


                } else {
                    Toast.makeText(getApplicationContext(), "Please Fill Up The Fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (LoginActivity.this, RegisterActivity.class);
                startActivity(i);

            }
        });



    }

    private class getValues extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(LoginActivity.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setMessage("Logging in");
            dialog.setIndeterminate(true);

            dialog.setCancelable(false);

            if(!isFinishing()) {
                dialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean success = false;
            String url = URL + "login/";
            JSONObject jsoin = null;

            token = FirebaseInstanceId.getInstance().getToken();

            String msg = getString(R.string.msg_token_fmt, token);
            Log.d(TAG, msg);

            try {

                jsoin = new JSONObject();
                jsoin.put("username",params[0] );
                jsoin.put("password",password );
                jsoin.put("fcm", token);


            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostJson(mContext, url, jsoin.toString());
            if (rst == null) {
                err = UtilHttp.err;
            } else {
                try {
                    JSONObject jso = new JSONObject(rst);
                    dbID = jso.getInt("id");
                    dbUsername = jso.getString("username");
                    //dbPassword = jso.getString("password");
                    dbContactNumber = jso.getInt("contactNo");
                    dbEmail = jso.getString("email");
                    dbProfilePic = jso.getString("picture");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                success = true;

            }
            return success;

        }
        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result){

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("id", dbID) ;
                editor.putString("username",dbUsername);
                editor.putString("password", password);
                editor.putInt ("contactnumber",dbContactNumber);
                editor.putString("email", dbEmail);
                editor.putString("picture", dbProfilePic);
                editor.commit();

                initSendBird();

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

                finish();

            }else {
               Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
           }

        }
    }

    public static String md5(String s)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private class resetPassword extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean success = false;
            String url = URL + "reset/";
            JSONObject jsoin = null;

            //Log.d("name: ", params[0]);
            try {

                jsoin = new JSONObject();
                jsoin.put("email",params[0] );

            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostJson(LoginActivity.this , url, jsoin.toString());
            if (rst == null) {
                err = UtilHttp.err;
            } else {
                Log.d("fuck", "ok");
                success = true;

            }
            Log.d("fuck", "fail");
            return success;

        }
        @Override
        protected void onPostExecute(Boolean result) {

            if(result){
                Intent i = new Intent (LoginActivity.this,LoginActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Email Sent!", Toast.LENGTH_SHORT).show();


            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void initSendBird(){
        Log.d("username","username is now : " + dbUsername);
        SendBird.init("0ABD752F-9D9A-46DE-95D5-37A00A1B3958", getApplication().getApplicationContext());
        SendBird.connect(dbID+"", new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
            if (e != null) {
                Toast.makeText(getApplicationContext(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            SendBird.updateCurrentUserInfo(dbUsername, dbProfilePic, new SendBird.UserInfoUpdateHandler() {
                @Override
                public void onUpdated(SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getApplicationContext(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

            //sendbird notification
            if (FirebaseInstanceId.getInstance().getToken() == null) return;
            SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
            new SendBird.RegisterPushTokenWithStatusHandler() {
                @Override
                public void onRegistered(SendBird.PushTokenRegistrationStatus status, SendBirdException e) {
                    if (e != null) {
                        // Error.
                        return;
                    }
                }
            });
            }
        });
    }
}
