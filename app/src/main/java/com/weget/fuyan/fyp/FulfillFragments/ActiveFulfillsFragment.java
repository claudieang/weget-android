package com.weget.fuyan.fyp.FulfillFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weget.fuyan.fyp.Fulfill;
import com.weget.fuyan.fyp.MyfulfillDetails;
import com.weget.fuyan.fyp.R;
import com.weget.fuyan.fyp.Recycler.RecyclerItemClickListener;
import com.weget.fuyan.fyp.Recycler.RecyclerViewEmptySupport;
import com.weget.fuyan.fyp.Request;
import com.weget.fuyan.fyp.UtilHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP on 4/4/2016.
 */

public class ActiveFulfillsFragment extends Fragment {
    ImageButton addRequest,homepage,requestbt,fulfillbt;
    ListView myRequestLV;
    //RequestListAdapter adapter;
    int fulfillRequestImage = R.drawable.ordericon;
    int  requestImage = R.drawable.ordericon;
    int myId;
    Context mContext;
    String err, authString, username, password;
    ArrayList<Request> myFulfillRequestArrayList = new ArrayList<>();
    View view;
    Activity activity;
    private RecyclerViewEmptySupport recyclerView;
    private com.weget.fuyan.fyp.Recycler.FulfillActiveListAdapter mAdapter;
    String URL;
    SwipeRefreshLayout swipeRefresh;
    ProgressDialog dialog;
    TextView emptyView;
    boolean haveActiveFulfills = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_fulfills, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setContentView(R.layout.activity_my_request);
        view = getView();
        activity = getActivity();


        URL = getString(R.string.webserviceurl);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);
        recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.my_request_list);

        mAdapter = new com.weget.fuyan.fyp.Recycler.FulfillActiveListAdapter(myFulfillRequestArrayList,2);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(view.findViewById(R.id.empty_view));
        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        emptyView = (TextView)view.findViewById(R.id.empty_view);


        authString  = username + ":" + password;
        new getMyFulfills(true).execute(authString);

        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        new getMyFulfills(false).execute(authString);

                    }
                }
        );

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(activity.getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Request rq = myFulfillRequestArrayList.get(position);
                        Intent intent = new Intent(getActivity(), MyfulfillDetails.class);
                        intent.putExtra("selected_fulfill_request",(Serializable) rq);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    /*

    private class getRequests extends AsyncTask<String, Void, Boolean> {
        Boolean showDialog;

        public getRequests(boolean showDialog){
            this.showDialog = showDialog;
        }


        @Override
        protected void onPreExecute() {
            if(showDialog) {
                dialog = new ProgressDialog(activity, R.style.MyTheme);
                dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                if (!activity.isFinishing()) {
                    dialog.show();
                }
            }
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

                success = true;
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result){

                new getMyFulfill(showDialog).execute(authString);



            }else {
                Toast.makeText(activity.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getMyFulfill extends AsyncTask<String, Void, Boolean> {
        Boolean showDialog;

        public getMyFulfill(boolean showDialog){
            this.showDialog = showDialog;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "account/" + myId+"/fulfill/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                myFulfillRequestArrayList.clear();

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for(int i = 0; i < jsoArray.length(); i++) {
                        JSONObject jso = jsoArray.getJSONObject(i);

                        int id = jso.getInt("id");
                        int requestId = jso.getInt("requestId");
                        int fulfillerId = jso.getInt("fulfillerId");
                        String status = jso.getString("status");

                        Fulfill f = new Fulfill(id,requestId, fulfillerId,status);
                        if(status.equals("pending")||status.equals("completed")||status.equals("dispute")) {
                            myFulfillList.add(f);
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

                new getMyFulfills(showDialog).execute(authString);
            }else {
                Toast.makeText(activity.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }


        }
    }
    */



    private class getMyFulfills extends AsyncTask<String, Void, Boolean> {

        Boolean showDialog;

        public getMyFulfills(boolean showDialog){
            this.showDialog = showDialog;
        }


        @Override
        protected void onPreExecute() {
            if(showDialog) {
                dialog = new ProgressDialog(activity, R.style.MyTheme);
                dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                if (!activity.isFinishing()) {
                    dialog.show();
                }
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "account/" + myId+"/fulfill/request/active/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                myFulfillRequestArrayList.clear();
                //myFulfillRequestList.clear();

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for(int i = 0; i < jsoArray.length(); i++) {
                        JSONObject jso = jsoArray.getJSONObject(i);

                        int id = jso.getInt("id");
                        int requestorId = jso.getInt("requestorId");
                        int imageResource = fulfillRequestImage;
                        String productName = jso.getString("productName");
                        String requirement = jso.getString("requirement");
                        String location = jso.getString("location");
                        int postal = jso.getInt("postal");
                        String startTime = jso.getString("startTime");
                        int duration = jso.getInt("duration");
                        String endTime = jso.getString("endTime");
                        double price = jso.getDouble("price");
                        String status = jso.getString("status");


                        if(status.equals("active")) {
                            Request request = new Request(id, requestorId, imageResource, productName, requirement, location,
                                    postal, startTime, endTime, duration, price, status);
                            myFulfillRequestArrayList.add(request);
                        }

                    }

                    if(myFulfillRequestArrayList.size() > 0){
                        haveActiveFulfills = true;
                    } else {
                        haveActiveFulfills = false;
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

            if(showDialog) {
                dialog.dismiss();
            }

            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }

            if(result){
                //boolean check = false;


                //adapter.clear();

                Log.d("Print", "Value: " + myFulfillRequestArrayList.size());
                // Now we call setRefreshing(false) to signal refresh has finished
                //swipeContainer.setRefreshing(false);
                //Toast.makeText(getApplicationContext(), "Populating My Fulfills!", Toast.LENGTH_SHORT).show();

                if(!haveActiveFulfills){
                    emptyView.setText("No fulfills made yet.");
                }


            }else {
                Toast.makeText(activity.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }
            mAdapter.notifyDataSetChanged();

        }
    }
}