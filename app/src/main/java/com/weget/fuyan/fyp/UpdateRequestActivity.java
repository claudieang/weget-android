package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.weget.fuyan.fyp.Util.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateRequestActivity extends AppCompatActivity  implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener  {

    String URL;

    List<Address> fullAddress;
    double latitude, longtitude;

    String productName, requestRequirement, addressLine, requestDurationS, postalCodeS,priceS,
            startTime, endTime, err, username, password, authString, requestStatus, postalCode;
    int requestDuration, requestorId, requestId;
    double price;
    EditText etProductName, etRequestRequirement, etPostalCode, etAddressLine, etPrice;
    //Button getAddressBtn, updateRequestBtn;
    Button updateRequestBtn;
    Button getAddressBtn;
    Geocoder geocoder;
    Context mContext;
    Request rq;
    CheckBox cb;
    Spinner dropdown;
    String day, month, year, hours, mins;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker_name";
    private TextView etRequestDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_request);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        rq = (Request)getIntent().getSerializableExtra("selected_request");

        URL = getString(R.string.webserviceurl);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        requestorId = pref.getInt("id",0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        requestId = rq.getId();

        ImageButton cancel_Btn = (ImageButton)findViewById(R.id.close_btn);

        Button updateRequestBtn = (Button)findViewById(R.id.create_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        toolbarTitle.setText("Update Request");
        toolbarTitle.setTypeface(typeFace);
        updateRequestBtn.setText("UPDATE");

        etProductName = (EditText)findViewById(R.id.product_name_txt);
        etRequestRequirement = (EditText)findViewById(R.id.product_requirement_txt);
        etPostalCode = (EditText)findViewById(R.id.postal_code_txt);
        etAddressLine = (EditText)findViewById(R.id.address_line_txt);

        etPrice = (EditText)findViewById(R.id.request_price_txt);
        getAddressBtn = (Button)findViewById(R.id.get_address_btn);
        etRequestDuration = (TextView)findViewById(R.id.request_duration_txt);
        //updateRequestBtn = (Button)findViewById(R.id.update_request_btn);
        etPrice.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        geocoder = new Geocoder(this);

        etProductName.setText(rq.getProductName());
        etRequestRequirement.setText(rq.getRequirement());
        etPostalCode.setText(String.valueOf(rq.getPostal()));
        etAddressLine.setText(rq.getLocation());

        endTime = rq.getEndTime();

        //etRequestDuration.setText("test");
        etRequestDuration.setText(DateFormatter.formatDate(rq.getEndTime()));
        etRequestDuration.setTextSize(12);

        etPrice.setText(String.valueOf(rq.getPrice()));

        //Check whether location is enabled
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateRequestActivity.this);
            dialog.setMessage("GPS Network has not been enabled.");
            dialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            AlertDialog alt = dialog.create();
            alt.show();
        }

        etRequestDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(UpdateRequestActivity.this)
                        .setDateRange(new MonthAdapter.CalendarDay(), new MonthAdapter.CalendarDay(2020, 1, 1));
                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });

        getAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postalCodeS = etPostalCode.getText().toString();

                if(postalCodeS != null && postalCodeS.trim().length() >0) {
                    postalCode = postalCodeS;
                    String zip = postalCodeS;
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(zip, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            // Use the address as needed
                            latitude = address.getLatitude();
                            longtitude = address.getLongitude();

                            String message = String.format("Latitude: %f, Longitude: %f",
                                    latitude, longtitude);

                            Log.d("Result: ", message);
                            fullAddress = geocoder.getFromLocation(latitude, longtitude, 1);

                            if (fullAddress != null && !fullAddress.isEmpty()) {

                                String completeAddress = fullAddress.get(0).getAddressLine(0);
                                String city = fullAddress.get(0).getLocality();
                                String state = fullAddress.get(0).getAdminArea();
                                String country = fullAddress.get(0).getCountryName();
                                String postalCode = fullAddress.get(0).getPostalCode();
                                String knownName = fullAddress.get(0).getFeatureName();


                                String addressLineResult = completeAddress + ", " + country;
                                etAddressLine.setText(addressLineResult);
                            } else {

                                etPostalCode.setError("Invalid Postal!");
                                //Toast.makeText(CreateRequestActivity.this, "Unable to process Lat & Long", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            // Display appropriate message when Geocoder services are not available
                            etPostalCode.setError("Invalid Postal!");
                            //Toast.makeText(CreateRequestActivity.this, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        updateRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productName = etProductName.getText().toString();
                requestRequirement = etRequestRequirement.getText().toString();
                postalCodeS = etPostalCode.getText().toString();
                addressLine = etAddressLine.getText().toString();
                //endTime = etRequestDuration.getText().toString();
                priceS = etPrice.getText().toString();
                //requestDuration = Integer.parseInt(requestDurationS);

                if(productName != null && productName.trim().length()>0){

                    if(requestRequirement != null && requestRequirement.trim().length() >0){

                        if(postalCodeS != null && postalCodeS.trim().length() >0){
                            postalCode = postalCodeS;

                            if(addressLine != null && addressLine.trim().length()>0){
                                    requestDuration = 0;
                                    if(etRequestDuration.getText().toString().isEmpty()){
                                        etRequestDuration.setError("EMPTY");

                                    }
                                    if(priceS!=null && priceS.trim().length()>0){
                                        price = Double.parseDouble(priceS);
                                        startTime = rq.getStartTime();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                        if(endTime.length() != 0) {
                                            Date end = null;
                                            try {
                                                end = sdf.parse(endTime);

                                            } catch (ParseException pe) {
                                                etRequestDuration.setError("Invalid End Date");
                                            }

                                            if (end.before(new Date())) {
                                                etRequestDuration.setError("Invalid End Date");
                                            } else {
                                                authString = username + ":" + password;
                                                new updateRequest().execute(authString);
                                            }
                                        } else {
                                            etRequestDuration.setError("Invalid End Date");
                                        }

                                    }else{
                                        etPrice.setError("Price required!");
                                    }

                            }else{
                                etAddressLine.setError("Address required!");
                            }

                        }else{
                            etPostalCode.setError("Postal code required!");
                        }

                    }else{
                        etRequestRequirement.setError("Specify requirements!");
                    }

                }else{
                    etProductName.setError ("Product name required!");
                }


            }
        });

        cancel_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(UpdateRequestActivity.this, MainActivity.class);
                onBackPressed();

            }
        });
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        //mResultTextView.setText(year + "/" +  monthOfYear + "/" + dayOfMonth);
        this.year = year + "";
        this.month = (monthOfYear + 1) + "";
        this.day = dayOfMonth + "";
        Log.d("Date", year + "/" +  monthOfYear + "/" + dayOfMonth);
        Date now = new Date();
        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(UpdateRequestActivity.this)
                .setDoneText("Ok")
                .setStartTime(now.getHours(),now.getMinutes())
                .setForced12hFormat()
                .setCancelText("Cancel");
        rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
        //Log.d("Time", hourOfDay + ":" + minute);
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        this.hours = hourOfDay + "";
        this.mins = minute + "";
        Log.d("Time", hourOfDay + ":" + minute);
        //"yyyy-MM-dd HH:mm:ss")
        if(day.length() != 2){
            day = "0" + day;
        }
        if(month.length() != 2){
            month = "0" + month;
        }
        if(hours.length() != 2){
            hours = "0" + hours;
        }

        if(mins.length() != 2){
            mins = "0" + mins;
        }
        endTime = year + "-" + month + "-" + day + " " + hours + ":" + mins + ":" + "00" ;
        etRequestDuration.setText(DateFormatter.formatDate(endTime));
    }

    public class DecimalDigitsInputFilter implements InputFilter {
        Pattern mPattern;
        public DecimalDigitsInputFilter(int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }
    }
    private class updateRequest extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(UpdateRequestActivity.this, R.style.MyTheme);

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


            //authString = "admin:password";
            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL+ "request/" + requestId + "/";
            JSONObject jsoin = null;

            try {

                jsoin = new JSONObject();
                jsoin.put("id", requestId);
                jsoin.put("requestorId", requestorId);
                jsoin.put("productName", productName);
                jsoin.put("requirement", requestRequirement);
                jsoin.put("location", addressLine);
                jsoin.put("postal", postalCode);
                jsoin.put("startTime", startTime);
                jsoin.put("duration", requestDuration);
                jsoin.put("endTime", endTime);
                jsoin.put("price", price);
                jsoin.put("status", "active");




            } catch(JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }
            Log.d("JSON String: ", jsoin.toString());
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
            if(result) {
                Toast.makeText(getBaseContext(), "Request updated!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(UpdateRequestActivity.this, MainActivity.class);
                i.putExtra("updated_request_tab", 1);
                i.putExtra("udpated_request_swipe", 0);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }
}
