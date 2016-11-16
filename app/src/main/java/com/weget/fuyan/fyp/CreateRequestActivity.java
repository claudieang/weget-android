package com.weget.fuyan.fyp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

public class CreateRequestActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener {
    List<Address> fullAddress;
    double latitude, longtitude;

    String productName, requestRequirement, addressLine, requestDurationS, postalCodeS, priceS,
            startTime, endTime, err, username, password, authString, unit;
    int requestDuration, requestorId;
    double price;
    EditText etProductName, etRequestRequirement, etPostalCode, etAddressLine, etPrice;
    Button createRequestBtn;
    Button getAddressBtn;
    ImageButton getCurrLocBtn;
    Geocoder geocoder;
    Context mContext;
    double latFromGPS;
    double lngFromGPS;

    private Toolbar toolbar;
    String URL;
    String day, month, year, hours, mins;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker_name";
    private TextView etRequestDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);


        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        //font
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("");
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call


        //TextView myTextView = (TextView) findViewById(R.id.tv_request_title);
        //Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TitilliumWeb-Bold.ttf");
        //myTextView.setTypeface(typeFace);

        URL = getString(R.string.webserviceurl);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        requestorId = pref.getInt("id", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);

        etProductName = (EditText) findViewById(R.id.product_name_txt);
        etRequestRequirement = (EditText) findViewById(R.id.product_requirement_txt);
        etPostalCode = (EditText) findViewById(R.id.postal_code_txt);
        etAddressLine = (EditText) findViewById(R.id.address_line_txt);
        //etRequestDuration = (EditText) findViewById(R.id.request_duration_txt);
        etRequestDuration = (TextView) findViewById(R.id.request_duration_txt);

        etPrice = (EditText) findViewById(R.id.request_price_txt);
        getAddressBtn = (Button) findViewById(R.id.get_address_btn);

        //createRequestBtn = (Button) findViewById(R.id.create_request_btn);
        Button done_Btn = (Button) findViewById(R.id.create_btn);
        ImageButton cancel_Btn = (ImageButton) findViewById(R.id.close_btn);

        ((TextView) findViewById(R.id.request_title)).setTypeface(typeFaceBold);
        ((TextView) findViewById(R.id.request_title)).setTypeface(typeFaceBold);
        etProductName.setTypeface(typeFace);
        etRequestRequirement.setTypeface(typeFace);
        etPostalCode.setTypeface(typeFace);
        etAddressLine.setTypeface(typeFace);
        etRequestDuration.setTypeface(typeFace);
        etPrice.setTypeface(typeFace);
        //createRequestBtn.setTypeface(typeFace);


        geocoder = new Geocoder(this);
/*
        productName = etProductName.getText().toString();
        requestRequirement = etRequestRequirement.getText().toString();
        addressLine = etAddressLine.getText().toString();
        requestDurationS = etRequestDuration.getText().toString();
        requestDuration = Integer.parseInt(requestDurationS);


*/
        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(CreateRequestActivity.this)
                        .setDateRange(new MonthAdapter.CalendarDay(), new MonthAdapter.CalendarDay(2020, 1, 1));
                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });

        getAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postalCodeS = etPostalCode.getText().toString();

                if (postalCodeS != null && postalCodeS.trim().length() > 0) {
                    //postalCode = Integer.parseInt(postalCodeS);

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

                                //Log.d("Address line: ", completeAddress);
                                //Log.d("City: ", city);
                                //Log.d("State: ", state);
                                //Log.d("Country: ", country);
                                //Log.d("Postal Code: ", postalCode);

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
                } else{
                    // instantiate the location manager, note you will need to request permissions in your manifest
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    // get the last know location from your location manager.
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //doesnt seem to work
                    Log.d("Print", "location data1 is : " + location);
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.d("Print", "location data2 is : " + location);
                    }

                    try {
                        List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Log.d("Print", "address list size is : " + address.size());

                        if (address != null && !address.isEmpty()) {

                            String completeAddress = address.get(0).getAddressLine(0);
                            String country = address.get(0).getCountryName();
                            String postalCode = address.get(0).getPostalCode();

                            String addressLineResult = completeAddress + ", " + country;
                            etAddressLine.setText(addressLineResult);
                            etPostalCode.setText(postalCode);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });


        done_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productName = etProductName.getText().toString();
                requestRequirement = etRequestRequirement.getText().toString();
                postalCodeS = etPostalCode.getText().toString();
                addressLine = etAddressLine.getText().toString();
                requestDurationS = etRequestDuration.getText().toString();
                priceS = etPrice.getText().toString();
                //requestDuration = Integer.parseInt(requestDurationS);

                if (productName != null && productName.trim().length() > 0) {

                    if (requestRequirement != null && requestRequirement.trim().length() > 0) {

                        if (postalCodeS != null && postalCodeS.trim().length() > 0) {

                            //postalCode = Integer.parseInt(postalCodeS);

                            if (addressLine != null && addressLine.trim().length() > 0) {

                                if (requestDurationS != null && requestDurationS.trim().length() > 0) {
                                    //requestDuration = Integer.parseInt(requestDurationS);
                                    requestDuration = 0;


                                    if (priceS != null && priceS.trim().length() > 0) {
                                        price = Double.parseDouble(priceS);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date now = new Date();
                                        startTime = sdf.format(now);

                                        if(endTime.length() != 0) {
                                            Date end = null;
                                            try {
                                                end = sdf.parse(endTime);

                                            } catch (ParseException pe) {
                                                etRequestDuration.setError("Invalid End Date");
                                            }
                                            if (end.before(now)) {
                                                etRequestDuration.setError("Invalid End Date");
                                            } else {
                                                authString = username + ":" + password;
                                                new createRequest().execute(authString);
                                            }
                                        } else {
                                            etRequestDuration.setError("Invalid End Date");
                                        }



                                    } else {
                                        etPrice.setError("Price required!");
                                    }
                                } else {
                                    etRequestDuration.setError("End Date required!");
                                }

                            } else {
                                etAddressLine.setError("Address required!");
                            }

                        } else {
                            etPostalCode.setError("Postal code required!");
                        }

                    } else {
                        etRequestRequirement.setError("Specify requirements!");
                    }

                } else {
                    etProductName.setError("Product name required!");
                }


            }
        });

        cancel_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateRequestActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        //mResultTextView.setText(year + "/" +  monthOfYear + "/" + dayOfMonth);
        monthOfYear=monthOfYear+1;
        this.year = year + "";
        this.month = monthOfYear + "";
        this.day = dayOfMonth + "";
        Log.d("Date", year + "/" +  monthOfYear + "/" + dayOfMonth);
        Date now = new Date();
        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(CreateRequestActivity.this)
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
        etRequestDuration.setTextSize(12);
    }


    private class createRequest extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(CreateRequestActivity.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            if (!isFinishing()) {
                dialog.show();
            }


        }

        @Override
        protected Boolean doInBackground(String... params) {


            //authString = "admin:password";
            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "request/";
            JSONObject jsoin = null;

            try {

                jsoin = new JSONObject();
                jsoin.put("requestorId", requestorId);
                jsoin.put("productName", productName);
                jsoin.put("requirement", requestRequirement);
                jsoin.put("location", addressLine);
                jsoin.put("postal", postalCodeS);
                jsoin.put("startTime", startTime);
                jsoin.put("duration", requestDuration);
                jsoin.put("endTime", endTime);
                jsoin.put("price", price);
                jsoin.put("status", "active");


            } catch (JSONException e) {
                e.printStackTrace();
                err = e.getMessage();
            }
            Log.d("JSON String: ", jsoin.toString());
            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, jsoin.toString() + basicAuth);
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
            if (result) {
                Toast.makeText(getBaseContext(), "Request created!", Toast.LENGTH_LONG).show();
                //Intent i = new Intent(CreateRequestActivity.this, HomeActivity.class);
                Intent i = new Intent(CreateRequestActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getBaseContext(), err, Toast.LENGTH_LONG).show();
            }

        }
    }

    public void onLocationChanged(Location location) {
        updateGPSCoordinates(location);
    }

    public void updateGPSCoordinates(Location location) {
        if (location != null) {
            latFromGPS = location.getLatitude();
            lngFromGPS = location.getLongitude();
        }
    }


}
