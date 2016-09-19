package com.wegot.fuyan.fyp;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadImageActivity extends AppCompatActivity {

    public static final int requestcode = 1;
    ImageView img;
    Button btnupload, btnchooseimage;
    //EditText edtname;
    byte[] byteArray;

    String encodedImage;
    TextView txtmsg;

    ProgressBar pg;

    ResultSet rs;
    //Connection con;
    private Context mContext;
    String username;
    String password,email,err, contactNoS, idString;
    int contactNo, id;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 666;
    SharedPreferences.Editor editor = null;
/*

    @SuppressLint("NewApi")
    private Connection ConnectionHelper(String user, String password,
                                        String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + ";"
                    + "databaseName=" + database + ";user=" + user
                    + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return connection;
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
        id = pref.getInt("id",0);
        idString = String.valueOf(id);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        email = pref.getString("email",null);
        contactNo = pref.getInt("contactnumber",0);
        contactNoS = String.valueOf(contactNo);

        img = (ImageView) findViewById(R.id.imageview);
        btnupload = (Button) findViewById(R.id.btnupload);
        btnchooseimage = (Button) findViewById(R.id.btnchooseimage);
        //edtname = (EditText) findViewById(R.id.edtname);
        txtmsg = (TextView) findViewById(R.id.txtmsg);

        pg = (ProgressBar) findViewById(R.id.progressBar1);
        pg.setVisibility(View.GONE);
/*
        un = "sa";
        password = "123";
        db = "MyDB";
        ip = "192.168.0.100";
        con = ConnectionHelper(un, password, db, ip);
*/
        btnchooseimage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(UploadImageActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {


                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(UploadImageActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);



                }else {

                    ChooseImage();
                }
            }
        });

        btnupload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(encodedImage != null && encodedImage.length() > 0) {
                    Log.d("BASE64: ", encodedImage);
                    new updateValue().execute(idString);
                }else{
                    Toast.makeText(getBaseContext(), "Please select image!", Toast.LENGTH_LONG).show();
                }

                /*

                byte[] decodeString = Base64.decode("BASE64 STRING OVER HERE", Base64.DEFAULT);
                Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                        decodeString, 0, decodeString.length);
                img.setImageBitmap(decodebitmap);

                */

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RC_PERMISSION_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    ChooseImage();


                } else {

                    Toast.makeText(getBaseContext(), "Permission Denied! LEL!", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

/*
    public void UploadtoDB() {
        String msg = "unknown";
        try {

            con = ConnectionHelper(un, password, db, ip);
            String commands = "Insert into ImgTbl2 (ImgName,Img) values ('"
                    + edtname.getText().toString() + "','" + encodedImage
                    + "')";
            // encodedImage which is the Base64 String
            PreparedStatement preStmt = con.prepareStatement(commands);
            preStmt.executeUpdate();
            msg = "Inserted Successfully";
        } catch (SQLException ex) {
            msg = ex.getMessage().toString();
            Log.d("hitesh", msg);

        } catch (IOError ex) {
            // TODO: handle exception
            msg = ex.getMessage().toString();
            Log.d("hitesh", msg);
        } catch (AndroidRuntimeException ex) {
            msg = ex.getMessage().toString();
            Log.d("hitesh", msg);

        } catch (NullPointerException ex) {
            msg = ex.getMessage().toString();
            Log.d("hitesh", msg);
        }

        catch (Exception ex) {
            msg = ex.getMessage().toString();
            Log.d("hitesh", msg);
        }

        txtmsg.setText(msg);

    }

*/

    //Select Image from Gallery
    public void ChooseImage() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
                && !Environment.getExternalStorageState().equals(
                Environment.MEDIA_CHECKING)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        } else {
            Toast.makeText(UploadImageActivity.this,
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
            Toast.makeText(UploadImageActivity.this, selectedImage.toString(),
                    Toast.LENGTH_SHORT).show();
            txtmsg.setText(selectedImage.toString());
            InputStream imageStream;
            try {

                imageStream = getContentResolver().openInputStream(
                        selectedImage);
                originBitmap = BitmapFactory.decodeStream(imageStream);
                resized = Bitmap.createScaledBitmap(originBitmap, 100, 100, true);

            } catch (FileNotFoundException e) {

                txtmsg.setText(e.getMessage().toString());
            }
            if (originBitmap != null) {
                this.img.setImageBitmap(originBitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (resized.compress(Bitmap.CompressFormat.JPEG, 100, stream)){
                    byteArray = stream.toByteArray();

                    encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                    Toast.makeText(UploadImageActivity.this, "Conversion Done",
                            Toast.LENGTH_SHORT).show();
                    Log.d("BASE46: ", encodedImage);
                }else if (resized.compress(Bitmap.CompressFormat.PNG, 100, stream)){
                    byteArray = stream.toByteArray();

                    encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                    Toast.makeText(UploadImageActivity.this, "Conversion Done",
                            Toast.LENGTH_SHORT).show();
                    Log.d("BASE46: ", encodedImage);
                }


            }
        }
    }



    private class updateValue extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(UploadImageActivity.this, R.style.MyTheme);

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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/account/" +params[0] +"/";
            JSONObject jsoin = null;

            try {
                jsoin = new JSONObject();
                jsoin.put("id",id);
                jsoin.put("username", username);
                jsoin.put("password", password);
                jsoin.put("contactNo", contactNo);
                jsoin.put("email", email);
                jsoin.put("fulfiller", "false");
                jsoin.put("picture", encodedImage);
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
                Toast.makeText(getBaseContext(), "Image Uploaded Success!", Toast.LENGTH_LONG).show();
                editor.putString("picture",encodedImage);
                editor.commit();
                Intent i = new Intent (UploadImageActivity.this,ProfileActivity.class);
                startActivity(i);
                finish();

            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }

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