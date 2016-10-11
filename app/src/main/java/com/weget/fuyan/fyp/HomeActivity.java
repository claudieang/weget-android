package com.weget.fuyan.fyp;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    Button newRequestBtn;
    ImageButton addRequest, homepage, requestbt, fulfillbt;
    String usernameText, err, authString;
    Context mContext;
    TextView text;
    ListView requestList;
    int requestImage = R.drawable.ordericon;
    ArrayList<Request> requestArrayList = new ArrayList<>();
    final String URL = getString(R.string.webserviceurl);

    RequestAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    //BOTTOM BAR TESTING
    //------------------------------------------------------
    private BottomNavigationBar bottomNavigationBar;
    int lastSelectedPosition = 0;
    private String TAG = HomeActivity.class.getSimpleName();
    private CreateRequestActivity mLocationFragment;
    private ProfileActivity mFindFragment;
    private SupportMapFragment mapFragment;
    private GoogleMap map;


    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //Google Maps variables
    private GoogleMap mMap;
    private ArrayList<String> markerList;
    protected GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    public Location mLastLocation;
    public Marker mCurrLocationMarker;
    String error;
    double lat;
    double lng;
    ArrayList<Double> latList = new ArrayList<>();
    ArrayList<Double> lngList = new ArrayList<>();
    ArrayList<String> postalList = new ArrayList<String>();
    ArrayList<String> requestNameList = new ArrayList<>();

    private final Context tempContext = this;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView b1 = (TextView) findViewById(R.id.my_request_title);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        b1.setTypeface(typeFace);
        //change font
        //TextView myTextView = (TextView) findViewById(R.id.textview2);
        //Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TitilliumWeb-BoldItalic.ttf");
        //myTextView.setTypeface(typeFace);
        //myTextView.setTextSize(35);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", null);
        String password = pref.getString("password", null);
        usernameText = "User: " + username;
        //text = (TextView) findViewById(R.id.textview2);
        //text.setText("Welcome, " + username + " ");
        requestList = (ListView) findViewById(R.id.active_request_list);
        adapter = new RequestAdapter(getApplicationContext(), R.layout.row_layout);
        requestList.setAdapter(adapter);
        //newRequestBtn = (Button)findViewById(R.id.new_request_btn);
        addRequest = (ImageButton) findViewById(R.id.addrequest);
        homepage = (ImageButton) findViewById(R.id.homepage);
        requestbt = (ImageButton) findViewById(R.id.request);
        fulfillbt = (ImageButton) findViewById(R.id.fulfill);

        authString = username + ":" + password;

        new getRequests().execute(authString);

        //change username
        TextView nav_tv = (TextView) findViewById(R.id.userName);
        nav_tv.setText(username);
        //set profile pic
        String profilePicture = pref.getString("picture", null);
        ImageView profileImage = (ImageView)findViewById(R.id.avatar);;

        if(profilePicture.equals("")){
            profileImage.setImageResource(R.drawable.ic_profile);
        }else{
            byte[] decodeString = Base64.decode(profilePicture, Base64.NO_WRAP);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(
                    decodeString, 0, decodeString.length);
            profileImage.setImageBitmap(decodebitmap);
        }

        /*







        newRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (HomeActivity.this, CreateRequestActivity.class);
                startActivity(i);

            }
        });
        */
        //default button colors
        //ImageButton request_button = (ImageButton)findViewById(R.id.request);
        //ImageButton fulfill_button = (ImageButton)findViewById(R.id.fulfill);
        //ImageButton home_button = (ImageButton)findViewById(R.id.homepage);
        //request_button.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
        //fulfill_button.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
        //int color = Color.parseColor("#00B0FF"); //The color u want
        //home_button.setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // White Tint

        /*
        addRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, CreateRequestActivity.class);
                startActivity(i);

            }
        });

//        homepage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent (HomeActivity.this, MainPage.class);
//                startActivity(i);
//
//            }
//        });

        requestbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, MyRequestActivity.class);
                startActivity(i);

            }
        });
        fulfillbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, MyFulfillActivity.class);
                startActivity(i);

            }
        });
        */
        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
                // Then you start a new Activity via Intent
                Request rq = requestArrayList.get(position);
                Intent intent = new Intent(HomeActivity.this, RequestDetailsActivity.class);
                intent.putExtra("selected_request", (Serializable) rq);
                startActivity(intent);
            }
        });

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    markerList = new ArrayList<>();
                    mMap = googleMap;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.290270,103.851959),14));

                    // Initialize Google Play Svcs
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(tempContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            buildGoogleApiClient();
                            mMap.setMyLocationEnabled(true);
                        }
                    } else {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        mNavItems.add(new NavItem("Home", "See all nearby Requests", R.drawable.ic_home_black_24dp));
        mNavItems.add(new NavItem("Settings", "Manage your preferences", R.drawable.ic_settings_black_24dp));
        mNavItems.add(new NavItem("About", "Get to know Weget", R.drawable.ic_info_outline_black_24dp));
        mNavItems.add(new NavItem("Logout", "Sign out from Weget", R.drawable.ic_exit_to_app_black_24dp));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.app_name, R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_black_24dp, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_shopping_basket_black_24dp, "My Requests"))
                .addItem(new BottomNavigationItem(R.drawable.ic_loupe_black_24dp, "Create Request"))
                .addItem(new BottomNavigationItem(R.drawable.ic_directions_run_black_24dp, "My Fulfills"))
                .addItem(new BottomNavigationItem(R.drawable.ic_chat_black_24dp, "Chat"))
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setActiveColor("#00B0FF")
                .setInActiveColor("#CCCCCC")
                .setBarBackgroundColor("#ECECEC")
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                switch(position){
                    case 0:

                        break;
                    case 1:
                        Intent i = new Intent(HomeActivity.this, MyRequestActivity.class);
                        startActivity(i);
                        break;
                    case 2:
                        Intent i1 = new Intent(HomeActivity.this, CreateRequestActivity.class);
                        startActivity(i1);
                        break;
                    case 3:
                        Intent i2 = new Intent(HomeActivity.this, MyFulfillActivity.class);
                        startActivity(i2);
                        break;
                    case 4:
                        break;

                }
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });


    }



    /*

    * Called when a particular item from the navigation drawer
    * is selected.
    */
    private void selectItemFromDrawer(int position) {
        /*
        Fragment fragment = new PreferencesFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();
        */
        Intent intent = null;
        switch(position){

            case 0:
                intent = new Intent (this, HomeActivity.class);
                break;
            case 1:
                intent = new Intent (this, ProfileActivity.class);
                break;
            case 2:
                intent = new Intent (this, HomeActivity.class);
                break;
            case 3:
                intent = new Intent(this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
        }

        if(intent!=null){
            startActivity(intent);
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }


    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // 'client' here is an instance of Android Async HTTP
        new getRequests().execute(authString);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.wegot.fuyan.fyp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.wegot.fuyan.fyp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class getRequests extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(HomeActivity.this, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "request/active/";


            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                requestArrayList.clear();
                markerList.clear();

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for (int i = 0; i < jsoArray.length(); i++) {
                        JSONObject jso = jsoArray.getJSONObject(i);

                        int id = jso.getInt("id");
                        int requestorId = jso.getInt("requestorId");
                        int imageResource = requestImage;
                        String productName = jso.getString("productName");
                        String requirement = jso.getString("requirement");
                        String location = jso.getString("location");
                        int postal = jso.getInt("postal");
                        String startTime = jso.getString("startTime");
                        int duration = jso.getInt("duration");
                        String endTime = jso.getString("endTime");
                        double price = jso.getDouble("price");
                        String status = jso.getString("status");

                        Request request = new Request(id, requestorId, imageResource, productName, requirement, location,
                                postal, startTime, endTime, duration, price, status);

                        requestArrayList.add(request);
                        postalList.add(""+postal);
                        requestNameList.add(productName);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                success = true;
            }

//            postalList.add("520894");
//            postalList.add("520899");
//            postalList.add("520891");

            //GEO starts here
            int size = postalList.size();


            for (int i = 0; i < size; i++) {
                String response;
                String postal = postalList.get(i);
                String baseURL = "https://maps.google.com/maps/api/geocode/json?components=countrySG|postal_code:";
                String key = "&key=AIzaSyDNbh3U6jmAeRGQogCmt6EcRXmFnYxbec4";
                //https://maps.google.com/maps/api/geocode/json?components=countrySG|postal_code:519599&key=AIzaSyDNbh3U6jmAeRGQogCmt6EcRXmFnYxbec4
                String mapurl = baseURL + postal + key;


                String maprst = UtilHttp.doHttpGet(mContext, mapurl);
                if (maprst == null) {
                    error = UtilHttp.err;
                } else {
                    try {
                        JSONObject jso = new JSONObject(maprst);


                        lng = ((JSONArray) jso.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lng");

                        lat = ((JSONArray) jso.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lat");

                        latList.add(lat);
                        lngList.add(lng);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    success = true;

                }
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {



                if (requestArrayList != null && !requestArrayList.isEmpty()) {
                    adapter.clear();

                    for (Request r : requestArrayList) {

                        adapter.add(r);

                    }
                }

                try {
                    addRequestMarkers();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Print", "Value: " + requestArrayList.size());

                swipeContainer.setRefreshing(false);
                //Toast.makeText(getApplicationContext(), "Populating Active Requests!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }


    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bottombar, menu);
        return true;
    }


    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            //titlebar back button
            case android.R.id.home:
                finish();
                return true;

            case R.id.home_item:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                Intent homeIntent = new Intent(this, HomeActivity.class);
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
                Intent myRequestIntent = new Intent(this, MyRequestActivity.class);
                startActivity(myRequestIntent);
                Toast.makeText(this, "Redirecting to My Request Page.", Toast.LENGTH_SHORT).show();
                return true;


            case R.id.my_fulfill_item:
                Intent myFulfillIntent = new Intent(this, MyFulfillActivity.class);
                startActivity(myFulfillIntent);
                Toast.makeText(this, "Redirecting to My Fulfill Page.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logout_item:

                Intent logoutIntent = new Intent(this, LoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
                finish();


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Google Maps section


    protected void addRequestMarkers() throws JSONException {
        //lat = 1.3790849;
        //lng = 103.955139;

        for (int i = 0; i < latList.size(); i++) {
            LatLng templatLng = new LatLng(latList.get(i), lngList.get(i));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(templatLng);
            markerOptions.title("Request: "+requestArrayList.get(i).getProductName());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            Marker mkr = mMap.addMarker(markerOptions);
            mkr.setTag(requestArrayList.get(i));
            markerList.add(mkr.getId());

            

        }




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Initialize Google Play Svcs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }




    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        Log.d("geo1", "LOLOLOOLOLOLOLOLO");


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mMap.clear();
        try {
            addRequestMarkers();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your current position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //Create Radius
        drawCircle(new LatLng(location.getLatitude(), location.getLongitude()));


        //move camera to marker
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        CameraPosition cmp = new CameraPosition(latLng,16,50,0);

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cmp));


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Ask user if need to explain
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt user once we show explanation
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                //No need to explain
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                //If request is cancelled, the resulting arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission was granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }
                    mMap.setMyLocationEnabled(true);
                } else {
                    //Permission denied, Disable the functionality that depends on this permission
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(500);

        // Border color of the circle
        circleOptions.strokeColor(Color.RED);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(3);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }


}
