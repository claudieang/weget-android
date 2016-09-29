package com.weget.fuyan.fyp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    Button b1, b2, b3;
    EditText t1, t2, t3, contact, email1;
    Context ctx = this;
    String user_name, user_pass, confirm_pass, email, contactNumS, err, token;
    Integer contactNum;
    private Context mContext;
    ImageView dpIV;
    byte[] byteArray;
    String encodedImage;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 666;

    final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private static final String TAG = "RegisterActivity";
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");

        //set font
        SpannableString s = new SpannableString("Register");
        s.setSpan(new TypefaceSpan(this, "Roboto-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(s);

       // b2 = (Button) findViewById(R.id.button5);
       // b3 = (Button)findViewById(R.id.button6);
        t1 = (EditText) findViewById(R.id.username);
        t2 = (EditText) findViewById(R.id.password);
        t3 = (EditText) findViewById(R.id.Cpassword);
        contact = (EditText) findViewById(R.id.contact_num);
        email1 = (EditText) findViewById(R.id.email);
        dpIV = (ImageView)findViewById(R.id.profile_pic_iv);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        //dpIV.setImageResource(R.drawable.ic_profile);

        t1.setTypeface(typeFace);
        t2.setTypeface(typeFace);
        t3.setTypeface(typeFace);
        contact.setTypeface(typeFace);
        email1.setTypeface(typeFace);




        Button done_Btn = (Button) findViewById(R.id.create_btn);
        ImageButton cancel_Btn = (ImageButton) findViewById(R.id.close_btn);

//        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Regular.ttf");
//        b2.setTypeface(typeFace);

        done_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_name = t1.getText().toString();
                user_pass = t2.getText().toString();
                confirm_pass = t3.getText().toString();
                contactNumS = contact.getText().toString();

                email = email1.getText().toString();
                DatabaseOperations DB = new DatabaseOperations(ctx);

                if(user_name != null && user_name.trim().length()> 0){ //check if username is filled in

                    if(user_pass != null && user_pass.trim().length() > 0){ //check if password is filled in


                        if(user_pass.trim().length() >= 8 && user_pass.indexOf(" ") == -1){ // check if password is at least 8 characters long


                            if(confirm_pass != null && confirm_pass.trim().length() > 0){// check empty confirmed password


                                if(confirm_pass.equals(user_pass)){ // check if confirmed password matches password


                                    if(contactNumS != null && contactNumS.trim().length() > 0) { // Check if contact number is empty


                                        contactNumS = contactNumS.trim().replaceAll("\\s", "");//Remove all white spaces in contact number

                                        if(contactNumS.matches("[0-9]+")){ // check if contact number only contains integer

                                            contactNum = Integer.parseInt(contactNumS);//convert input into integers

                                            if(email != null && email.trim().length() >0){//check empty email
                                                email = email.toLowerCase();

                                                if(EMAIL_ADDRESS_PATTERN.matcher(email).matches()){ // email pattern validation

                                                    //check TOS checkbox
                                                    if(checkBox.isChecked()) {
                                                        new storeValue().execute(user_name); //execute webservice register!
                                                    } else {
                                                        checkBox.setError("You need to agree to the Terms & Services to register!");
                                                    }

                                                }else{
                                                    email1.setError("Invalid email format!");
                                                }

                                            }else{
                                                email1.setError("Email is required!");
                                            }

                                        }else{
                                            contact.setError("Invalid contact number!");
                                        }
                                    }else{
                                        contact.setError("Contact number is required!");
                                    }
                                }else{
                                    t3.setError("Password mismatch!");
                                }
                            }else{
                                t3.setError("Confirm password is required!");
                            }

                        }else{
                            t2.setError("Invalid password!");
                        }

                    }else{
                        t2.setError("Password is required!");
                    }

                }else{
                    t1.setError("User name is required!");
                }



            }
        });


        cancel_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);

            }
        });
        dpIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {


                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(RegisterActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);



                }else {

                    ChooseImage();
                }

            }
        });



    }

    public void ChooseImage() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
                && !Environment.getExternalStorageState().equals(
                Environment.MEDIA_CHECKING)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        } else {
            Toast.makeText(RegisterActivity.this,
                    "No activity found to perform this task",
                    Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == RESULT_OK) {
            Bitmap originBitmap = null;
            Bitmap resized = null;
            Uri selectedImage = data.getData();
            Toast.makeText(RegisterActivity.this, selectedImage.toString(),
                    Toast.LENGTH_SHORT).show();
            //txtmsg.setText(selectedImage.toString());
            InputStream imageStream;
            try {

                imageStream = getContentResolver().openInputStream(
                        selectedImage);
                originBitmap = BitmapFactory.decodeStream(imageStream);
                resized = Bitmap.createScaledBitmap(originBitmap, 100, 100, true);

            } catch (FileNotFoundException e) {

                //txtmsg.setText(e.getMessage().toString());
            }
            if (originBitmap != null) {
                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), originBitmap);
                roundDrawable.setCircular(true);

                this.dpIV.setImageDrawable(roundDrawable);
                //this.dpIV.setImageBitmap(originBitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                if (resized.compress(Bitmap.CompressFormat.JPEG, 100, stream)){
                    byteArray = stream.toByteArray();

                    encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                    Toast.makeText(RegisterActivity.this, "Conversion Done",
                            Toast.LENGTH_SHORT).show();
                    Log.d("BASE46: ", encodedImage);
                }else if (resized.compress(Bitmap.CompressFormat.PNG, 100, stream)){
                    byteArray = stream.toByteArray();

                    encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                    Toast.makeText(RegisterActivity.this, "Conversion Done",
                            Toast.LENGTH_SHORT).show();
                    Log.d("BASE46: ", encodedImage);
                }


            }
        }
    }


    private class storeValue extends AsyncTask<String, Void, Boolean>{

        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this, R.style.MyTheme);

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

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/account/";
            JSONObject jsoin = null;

            token = FirebaseInstanceId.getInstance().getToken();

            String msg = getString(R.string.msg_token_fmt, token);
            Log.d(TAG, msg);


            try {
                if(encodedImage!= null && encodedImage.length() >0) {
                    jsoin = new JSONObject();
                    jsoin.put("username", user_name);
                    jsoin.put("password", user_pass);
                    jsoin.put("contactNo", contactNum);
                    jsoin.put("email", email);
                    jsoin.put("fulfiller", "false");
                    jsoin.put("picture", encodedImage);
                    jsoin.put("enabled", false);
                    jsoin.put("fcm",token);
                }else{

                    jsoin = new JSONObject();
                    jsoin.put("username", user_name);
                    jsoin.put("password", user_pass);
                    jsoin.put("contactNo", contactNum);
                    jsoin.put("email", email);
                    jsoin.put("fulfiller", "false");
                    jsoin.put("picture", "");
                    jsoin.put("enabled", false);
                    jsoin.put("fcm",token);

                }


            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostJson(mContext, url,jsoin.toString());
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
                Toast.makeText(getBaseContext(), "Registration Success!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }



        }
    }




}
