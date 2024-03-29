package com.weget.fuyan.fyp;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.sendbird.android.shadow.com.google.gson.Gson;
import com.sendbird.android.shadow.com.google.gson.reflect.TypeToken;
import com.weget.fuyan.fyp.Recycler.RecyclerItemClickListener;
import com.weget.fuyan.fyp.Recycler.RecyclerViewEmptySupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 4/4/2016.
 */

public class CompletedRequestsFragment extends Fragment {
    ImageButton addRequest,homepage,requestbt,fulfillbt;
    ListView myRequestLV;
    //RequestListAdapter adapter;
    int  requestImage = R.drawable.ordericon;
    int myId;
    Context mContext;
    String err, authString, username, password;
    ArrayList<Request> myRequestArrayList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;
    View view;
    Activity activity;
    private RecyclerViewEmptySupport recyclerView;
    private com.weget.fuyan.fyp.Recycler.RequestCompletedListAdapter mAdapter;
    String URL;
    SwipeRefreshLayout swipeRefresh;
    TextView emptyView3;
    boolean hasCompletedRequests = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_requests, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setContentView(R.layout.activity_my_request);
        view = getView();
        activity = getActivity();
        //change font
        //TextView myTextView=(TextView)view.findViewById(R.id.my_request_title);
        //Typeface typeFace=Typeface.createFromAsset(activity.getAssets(),"fonts/Quicksand-Bold.otf");
        //myTextView.setTypeface(typeFace);
/*
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
*/
        URL = getString(R.string.webserviceurl);

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);

        //tr = (Transaction)getIntent().getSerializableExtra("transaction");


//        myRequestLV = (ListView)view.findViewById(R.id.my_request_list);
//        //adapter = new RequestAdapter(activity.getApplicationContext(),R.layout.request_list_layout);
//        adapter = new RequestListAdapter(activity.getApplicationContext(), R.layout.request_list_layout);
//        myRequestLV.setAdapter(adapter);
        recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.my_request_list);

        emptyView3 = (TextView)view.findViewById(R.id.empty_view3);


        mAdapter = new com.weget.fuyan.fyp.Recycler.RequestCompletedListAdapter(myRequestArrayList, "request");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setEmptyView(view.findViewById(R.id.empty_view3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);


        authString  = username + ":" + password;
        new getMyRequests(true).execute(authString);
        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        new getMyRequests(false).execute(authString);
                        RequestFragment parentFragment = (RequestFragment)getParentFragment();
                        parentFragment.getActive().new getMyRequests(false).execute(authString);
                        parentFragment.getPend().new getMyRequests(false).execute(authString);

                    }
                }
        );

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(activity.getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Request rq = myRequestArrayList.get(position);
                        Intent intent = new Intent(getActivity(), CompletedRequest.class);
                        intent.putExtra("completed_request",(Serializable) rq);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }




    public class getMyRequests extends AsyncTask<String, Void, Boolean> {
        Boolean showDialog;

        ProgressDialog dialog;
        public getMyRequests(boolean showDialog){
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
            String url = URL + "account/" + myId + "/request/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                myRequestArrayList.clear();

                try {
//                    JSONArray jsoArray = new JSONArray(rst);
//                    for (int i = 0; i < jsoArray.length(); i++) {
//                        JSONObject jso = jsoArray.getJSONObject(i);
//
//                        int id = jso.getInt("id");
//                        int requestorId = jso.getInt("requestorId");
//                        int imageResource = requestImage;
//                        String productName = jso.getString("productName");
//                        String requirement = jso.getString("requirement");
//                        String location = jso.getString("location");
//                        String postal = jso.getString("postal");
//                        String startTime = jso.getString("startTime");
//                        int duration = jso.getInt("duration");
//                        String endTime = jso.getString("endTime");
//                        double price = jso.getDouble("price");
//                        String status = jso.getString("status");
//
//                        Request request = new Request(id, requestorId, imageResource, productName, requirement, location,
//                                postal, startTime, endTime, duration, price, status);
//                        if (status.equals("completed") || status.equals("Dispute")) {
//                            myRequestArrayList.add(request);
//                        }
//
//                        //mAdapter.notifyDataSetChanged();
//
//                    }
                    Gson gson  = new Gson();
                    ArrayList<Request> merged = new ArrayList<>();
                    merged = gson.fromJson(rst, new TypeToken<List<Request>>(){}.getType());

                    for(Request m : merged){
                        if(m.getStatus().equals("completed") || m.getStatus().equals("dispute")){
                            myRequestArrayList.add(m);
                        }
                    }
                    if(myRequestArrayList.size() > 0){
                        hasCompletedRequests = true;
                    } else {
                        hasCompletedRequests = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                success = true;
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mAdapter.notifyDataSetChanged();

            if(!hasCompletedRequests){
                emptyView3.setText("No requests completed yet.");
            }

            if(showDialog) {
                dialog.dismiss();
            }

            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }

        }
    }
}