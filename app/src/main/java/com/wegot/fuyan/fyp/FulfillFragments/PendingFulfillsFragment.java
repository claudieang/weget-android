package com.wegot.fuyan.fyp.FulfillFragments;

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
import android.widget.Toast;

import com.wegot.fuyan.fyp.Fulfill;
import com.wegot.fuyan.fyp.MyFulfillRequestDetailsActivity;
import com.wegot.fuyan.fyp.PendingdetailsFulfiller;
import com.wegot.fuyan.fyp.R;
import com.wegot.fuyan.fyp.Recycler.DividerItemDecoration;
import com.wegot.fuyan.fyp.Recycler.RecyclerItemClickListener;
import com.wegot.fuyan.fyp.Recycler.RecyclerViewEmptySupport;
import com.wegot.fuyan.fyp.Request;
import com.wegot.fuyan.fyp.RequestDetailsActivity;
import com.wegot.fuyan.fyp.UtilHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP on 4/4/2016.
 */

public class PendingFulfillsFragment extends Fragment {
    ImageButton addRequest,homepage,requestbt,fulfillbt;
    ListView myRequestLV;
    //RequestListAdapter adapter;
    int fulfillRequestImage = R.drawable.ordericon;
    int  requestImage = R.drawable.ordericon;
    int myId;
    Context mContext;
    String err, authString, username, password;
    ArrayList<Request> myFulfillRequestArrayList = new ArrayList<>();
    ArrayList<Fulfill> myFulfillList  = new ArrayList<>();
    ArrayList<Request> myFulfillRequestList = new ArrayList<>();
    View view;
    Activity activity;
    private RecyclerViewEmptySupport recyclerView;
    private com.wegot.fuyan.fyp.Recycler.RequestListAdapter mAdapter;

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

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);

        recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.my_request_list);

        mAdapter = new com.wegot.fuyan.fyp.Recycler.RequestListAdapter(myFulfillRequestArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(view.findViewById(R.id.empty_view2));
        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);


        authString  = username + ":" + password;
        new getRequests().execute(authString);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(activity.getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Request rq = myFulfillRequestArrayList.get(position);
                        Intent intent = new Intent(getActivity(), PendingdetailsFulfiller.class);
                        intent.putExtra("selected_my_pending_fulfill",(Serializable) rq);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private class getMyFulfill extends AsyncTask<String, Void, Boolean> {
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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/account/" + myId+"/fulfill/";

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

                new getMyFulfills().execute(authString);
            }else {
                Toast.makeText(activity.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    private class getRequests extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
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

                new getMyFulfill().execute(authString);

            }else {
                Toast.makeText(activity.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getMyFulfills extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/account/" + myId+"/fulfill/request/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                myFulfillRequestArrayList.clear();
                myFulfillRequestList.clear();

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


                        if(status.equals("pending")) {
                            Request request = new Request(id, requestorId, imageResource, productName, requirement, location,
                                    postal, startTime, endTime, duration, price, status);
                            myFulfillRequestArrayList.add(request);
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
                boolean check = false;

                //adapter.clear();
                if(myFulfillRequestArrayList != null && !myFulfillRequestArrayList.isEmpty()){

                    for(Request r: myFulfillRequestArrayList){
                        int rId = r.getId();
                        for(Fulfill f : myFulfillList){
                            if (f.getRequestId() == rId){
                                check = true;
                            }
                        }

                        if(check) {

                            //adapter.add(r);
                            myFulfillRequestList.add(r);
                        }

                        check = false;

                    }
                }
                Log.d("Print", "Value: " + myFulfillRequestArrayList.size());
                // Now we call setRefreshing(false) to signal refresh has finished
                //swipeContainer.setRefreshing(false);
                //Toast.makeText(getApplicationContext(), "Populating My Fulfills!", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(activity.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }
            mAdapter.notifyDataSetChanged();

        }
    }
}