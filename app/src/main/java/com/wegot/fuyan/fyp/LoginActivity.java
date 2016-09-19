package com.wegot.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ImageView logo_iv = (ImageView)findViewById(R.id.applogo_imageview);
        //logo_iv.setImageResource(R.drawable.weget_logo);


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

        forgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);

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
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/login/";
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

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

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
}
