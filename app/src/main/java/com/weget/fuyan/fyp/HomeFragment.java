package com.weget.fuyan.fyp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.maps.android.SphericalUtil;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.shadow.com.google.gson.Gson;
import com.sendbird.android.shadow.com.google.gson.reflect.TypeToken;
import com.weget.fuyan.fyp.Recycler.DividerItemDecoration;
import com.weget.fuyan.fyp.Recycler.RecyclerViewEmptySupport;
import com.weget.fuyan.fyp.Recycler.RequestAllListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Claudie on 9/3/16.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Button newRequestBtn;
    ImageButton addRequest, homepage, requestbt, fulfillbt;
    String usernameText, err, authString;
    Context mContext;
    TextView text;
    //ListView requestList;

    int requestImage = R.drawable.ordericon;
    ArrayList<Request> requestArrayList = new ArrayList<>();
    ArrayList<Request> originalList = new ArrayList<>();

    ArrayList<Request> filterList = new ArrayList<>();
    int myId;
    String URL;

    RequestListAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    //BOTTOM BAR TESTING
    //------------------------------------------------------
    private BottomNavigationBar bottomNavigationBar;
    int lastSelectedPosition = 0;
    //private String TAG = HomeActivity.class.getSimpleName();
    private CreateRequestActivity mLocationFragment;
    private ProfileActivity mFindFragment;
    private SupportMapFragment mapFragment;


    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //Google Maps variables
    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    public Location mLastLocation;
    public int radiusVal = -1;
    public Marker mCurrLocationMarker;
    String error;
    double lat;
    double lng;
    ArrayList<Double> latList = new ArrayList<>();
    ArrayList<Double> lngList = new ArrayList<>();

    //private final Context tempContext = getActivity().getApplicationContext();
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
    View view;
    Activity activity;
    private RecyclerViewEmptySupport recyclerView;
    private RequestAllListAdapter mAdapter;
    private LatLng lastLocation;
    private Circle circle;
    boolean ready = false;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        this.activity = activity;
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        this.activity = null;
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_screen, container, false);
        Log.d("sigh", "initiate home fragment");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_filter) {
            filter();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            //Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_SHORT).show();
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        view = getView();
        activity = getActivity();

        URL = getString(R.string.webserviceurl);
        TextView b1 = (TextView) view.findViewById(R.id.my_request_title);
        Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Light.ttf");
        b1.setTypeface(typeFace);

        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("MyPref", 0);
        String username = pref.getString("username", null);
        String password = pref.getString("password", null);
        myId = pref.getInt("id", -1);
        usernameText = "User: " + username;

        authString = username + ":" + password;
        //requestArrayList = new ArrayList<Request>();
        new getRequests().execute(authString);

        recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.active_request_list);
        mAdapter = new com.weget.fuyan.fyp.Recycler.RequestAllListAdapter(requestArrayList, myId);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setEmptyView(view.findViewById(R.id.empty_view));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        SendBird.init("0ABD752F-9D9A-46DE-95D5-37A00A1B3958", getContext());
        SendBird.connect(myId+"", new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    //Toast.makeText(mContext, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }


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


        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        //mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    mMap = googleMap;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.290270, 103.851959), 14));

                    // Initialize Google Play Svcs
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(activity, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        client = new GoogleApiClient.Builder(activity).addApi(AppIndex.API).build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                //handles radius filter
                int radiusResult=data.getIntExtra("radius", 0);

                boolean switcher = data.getBooleanExtra("switch", true);
                Log.d("returned result", radiusResult + "");
                mMap.clear();
                if(circle != null) {
                    circle.remove();
                    if (switcher) {
                        radiusVal = radiusResult;
                        drawCircle(lastLocation, radiusResult);
                    } else{
                        radiusVal = -1;
                    }
                }

                //handle price filter
                int priceResult = data.getIntExtra("price", 0);
                filterPrice(priceResult);
                new getLatlng().execute();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("returned result", "NO SHIT HERE");
                //Write your code if there's no result
                mMap.clear();
                if(circle != null){
                    circle.remove();
                    drawCircle(lastLocation, 600);
                }
                filterReset();
                new getLatlng().execute();
            }
        }
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
                Uri.parse("android-app://com.weget.fuyan.fyp/http/host/path")
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
                Uri.parse("android-app://com.weget.fuyan.fyp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class getRequests extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(activity, R.style.MyTheme);

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            ready = false;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            checkLocationPermission();

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "request/active/";


            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                //clear all used lists before adding the as markers
                requestArrayList.clear();
                //postalList.clear();
                //requestNameList.clear();

                Gson gson = new Gson();
                ArrayList<Request> ret = new ArrayList<>();
                ret = gson.fromJson(rst, new TypeToken<ArrayList<Request>>(){}.getType());
                requestArrayList.addAll(ret);

                success = true;
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            originalList.clear();
            originalList.addAll(requestArrayList);
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
            mAdapter.notifyDataSetChanged();
            new getLatlng().execute();

        }


    }

    private class getLatlng extends AsyncTask<String, Void, Boolean> {
        boolean success = false;

        @Override
        protected void onPreExecute() {
            //mMap.clear();
            latList.clear();
            lngList.clear();
            Log.d("Place Markers: (pre) ", requestArrayList.size() + "");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            for (int i = 0; i < requestArrayList.size(); i++) {
                String response;
                String postal = requestArrayList.get(i).getPostal();
                String baseURL = "https://maps.google.com/maps/api/geocode/json?components=countrySG|postal_code:";
                String key = "&key=AIzaSyDNbh3U6jmAeRGQogCmt6EcRXmFnYxbec4";
                //https://maps.google.com/maps/api/geocode/json?components=countrySG|postal_code:519599&key=AIzaSyDNbh3U6jmAeRGQogCmt6EcRXmFnYxbec4
                String mapurl = baseURL + postal + key;


                String maprst = UtilHttp.doHttpGet(mContext, mapurl);
                if (maprst == null) {
                    error = UtilHttp.err;
                    success = false;
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
            Log.d("Place Markers: ", requestArrayList.size() + "");
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                try {
                    addRequestMarkers();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }
            ready = true;
        }
    }

    protected void addRequestMarkers() throws JSONException {
        //lat = 1.3790849;
        //lng = 103.955139;
        HashMap<LatLng, ArrayList<Request>> markerMapList = new HashMap<>();

        if (mMap != null) {
            for (int i = 0; i < latList.size(); i++) {
                Log.d("Print", "latlist size is : " + latList.size());
                Log.d("Print", "lnglist size is : " + lngList.size());
                Log.d("Print", "requestArrayList size is : " + requestArrayList.size());
                LatLng templatLng = new LatLng(latList.get(i), lngList.get(i));
                Request tempRequest = requestArrayList.get(i);

                //check whether latlng overlaps
                if (markerMapList.size() == 0) {

                    ArrayList<Request> reqList = new ArrayList<>();
                    reqList.add(tempRequest);
                    markerMapList.put(templatLng, reqList);

                } else if (markerMapList.containsKey(templatLng)) { //means that latlng already exists but is a different request

                    ArrayList<Request> reqList = markerMapList.get(templatLng);
                    reqList.add(tempRequest);
                    markerMapList.put(templatLng, reqList);

                } else { //if no latlng has existed yet

                    ArrayList<Request> reqList = new ArrayList<>();
                    reqList.add(tempRequest);
                    markerMapList.put(templatLng, reqList);

                }
            }
            Log.d("Print", "markerMapList size is : " + markerMapList.size());

            //create markers based on latlng
            if (markerMapList.size() > 0) {
                MarkerOptions markerOptions = new MarkerOptions();
                //markerOptions.
                Iterator iter = markerMapList.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<LatLng, ArrayList<Request>> pair = (Map.Entry) iter.next();
                    LatLng addLatLng = pair.getKey();

                    ArrayList<Request> addReqs = pair.getValue();

                    Log.d("Print", "addReqs size is : " + addReqs.size());

                    markerOptions.position(addLatLng);
                    if (addReqs.size() > 1) {
                        markerOptions.title(addReqs.size() + " Requests");
                    } else {
                        markerOptions.title(addReqs.get(0).getProductName());
                    }
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


                    Marker mkr = mMap.addMarker(markerOptions);
                    mkr.setTag(addReqs);
                    if(radiusVal != -1) {
                        if (SphericalUtil.computeDistanceBetween(lastLocation, mkr.getPosition()) > radiusVal) {
                            mkr.setVisible(false);
                        }
                    }
                    Log.d("Print", "addReqs size is : " + addReqs.size());
                }

            }
            //markerList.add(mkr.getId());
            //LatLng requestMarker = new LatLng(lat, lng);
            //mMap.addMarker(new MarkerOptions().position(requestMarker).title("This is a request by: Shafiq"));


            Log.d("geo1", "mMap is : " + mMap);
            //need to set marker as onclick
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                  @Override
                  public void onInfoWindowClick(Marker marker) {

                      ArrayList<Request> reqList = (ArrayList<Request>) marker.getTag();


                      SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("MyPref", 0);
                      int id = pref.getInt("id", 0);

                      if (reqList != null && reqList.size() == 1) {

                          Request request = reqList.get(0);

                          if (request.getRequestorId() == id) {
                              //if the request viewed is mine
                              Intent intent = new Intent(getContext(), RequesterViewDetails.class);
                              intent.putExtra("selected_request", request);
                              startActivity(intent);
                          } else { //if the request viewed is someone elses
                              Intent intent = new Intent(getContext(), FulfillviewRequestDetails.class);
                              intent.putExtra("selected_request", request);
                              startActivity(intent);
                          }
                      } else { //pop up with list of requests

                          Intent intent = new Intent(getContext(), RequestPopUp.class);
                          intent.putExtra("reqList", reqList);
                          intent.putExtra("myId", myId);

                          startActivity(intent);


                      }

                  }
              }
            );
            Log.d("geo1", "HIHIIHIHIHIIHIHI");
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
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
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
        Log.d("circle", "checking for correct behaviour");
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

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
//
        // Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Your current position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);
//
        //Create Radius
        Log.d("circle", "going to draw circle");
        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        drawCircle(lastLocation, 500);


        //move camera to marker
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        CameraPosition cmp = new CameraPosition(latLng, 16, 0, 0);

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
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Ask user if need to explain
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt user once we show explanation
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                //No need to explain
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
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
                    if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }
                    mMap.setMyLocationEnabled(true);
                } else {
                    //Permission denied, Disable the functionality that depends on this permission
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    private void drawCircle(LatLng point, int radius) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(radius);

        // Border color of the circle
        circleOptions.strokeColor(Color.RED);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(3);

        // Adding the circle to the GoogleMap
        circle = mMap.addCircle(circleOptions);

    }

    public void refresh() {
        mMap.clear();
        new getRequests().execute(authString);
        //LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        drawCircle(lastLocation, 500);

    }

    public void filter(){
        if(ready) {
            Intent i = new Intent(activity, FilterActivity.class);
            startActivityForResult(i, 1);
        }
    }

    public void filterPrice(int maxPrice){
        //stores pre-filter original ArrayList

        //requestArrayList = originalList;
        requestArrayList.clear();
        Iterator<Request> setIterator = originalList.iterator();
        while(setIterator.hasNext()){
            Request r = setIterator.next();
            if(r.getPrice() <= maxPrice){
                requestArrayList.add(r);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    public void filterReset(){
        if(originalList != null) {
            //requestArrayList = originalList;

            Log.d("List sizes: ", requestArrayList.size() + ", " + originalList.size());

            requestArrayList.clear();
            requestArrayList.addAll(originalList);

            mAdapter.notifyDataSetChanged();
        }else{
            Log.d("filter reset", requestArrayList.size() + ", " + originalList.size());
        }
    }
}
