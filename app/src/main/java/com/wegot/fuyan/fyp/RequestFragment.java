package com.wegot.fuyan.fyp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class RequestFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_my_request, container, false);
        return view;
    }
    ImageButton addRequest,homepage,requestbt,fulfillbt;
    ListView myRequestLV;
    RequestAdapter adapter;
    int  requestImage = R.drawable.ordericon;
    int myId;
    Context mContext;
    String err, authString, username, password;
    ArrayList<Request> myRequestArrayList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;
    View view;
    Activity activity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setContentView(R.layout.activity_my_request);
        view = getView();
        activity = getActivity();
        //change font
        TextView myTextView=(TextView)view.findViewById(R.id.my_request_title);
        Typeface typeFace=Typeface.createFromAsset(activity.getAssets(),"fonts/Quicksand-Bold.otf");
        myTextView.setTypeface(typeFace);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.swipeContainer);
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

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);

        //tr = (Transaction)getIntent().getSerializableExtra("transaction");


        myRequestLV = (ListView)view.findViewById(R.id.my_request_list);
        adapter = new RequestAdapter(activity.getApplicationContext(),R.layout.row_layout);
        myRequestLV.setAdapter(adapter);

        authString  = username + ":" + password;

        new getRequests().execute(authString);

        myRequestLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
                // Then you start a new Activity via Intent
                Request rq = myRequestArrayList.get(position);
                Intent intent = new Intent(getActivity(), MyRequestFulfillerActivity.class);
                intent.putExtra("selected_my_request",(Serializable) rq);
                startActivity(intent);
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // 'client' here is an instance of Android Async HTTP
        new getRequests().execute(authString);
    }

    private class getRequests extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(activity, R.style.MyTheme);

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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/active/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result){

                new getMyRequests().execute(authString);

            }else {
                Toast.makeText(getActivity().getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

            if(dialog.isShowing()){
                dialog.dismiss();
            }

        }
    }

    private class getMyRequests extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/account/" + myId+"/request/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                myRequestArrayList.clear();

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for(int i = 0; i < jsoArray.length(); i++) {
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
                        if(!status.equals("expired")) {
                            myRequestArrayList.add(request);
                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                success = true;
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {


            if(result){
                adapter.clear();

                if(myRequestArrayList != null && !myRequestArrayList.isEmpty()){

                    for(Request r: myRequestArrayList){

                        adapter.add(r);

                    }
                }
                Log.d("Print", "Value: " + myRequestArrayList.size());
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                //Toast.makeText(getApplicationContext(), "Populating My Requests!", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(getActivity().getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
