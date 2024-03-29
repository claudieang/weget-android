package com.weget.fuyan.fyp.FulfillFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.sendbird.android.shadow.com.google.gson.Gson;
import com.sendbird.android.shadow.com.google.gson.reflect.TypeToken;
import com.weget.fuyan.fyp.FulfillFragment;
import com.weget.fuyan.fyp.R;
import com.weget.fuyan.fyp.Recycler.RecyclerViewEmptySupport;
import com.weget.fuyan.fyp.Request;
import com.weget.fuyan.fyp.UtilHttp;

import java.util.ArrayList;
import java.util.List;

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
   // ArrayList<Fulfill> myFulfillList  = new ArrayList<>();
   // ArrayList<Request> myFulfillRequestList = new ArrayList<>();
    View view;
    Activity activity;
    private RecyclerViewEmptySupport recyclerView;
    private com.weget.fuyan.fyp.Recycler.RequestListAdapter mAdapter;
    String URL;
    SwipeRefreshLayout swipeRefresh;
    ProgressDialog dialog;
    TextView emptyView2;
    boolean havePendingFulfills = false;

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

        mAdapter = new com.weget.fuyan.fyp.Recycler.RequestListAdapter(myFulfillRequestArrayList, 1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(view.findViewById(R.id.empty_view2));
        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        emptyView2 = (TextView)view.findViewById(R.id.empty_view2);


        authString  = username + ":" + password;
        new getMyFulfills(true).execute(authString);

        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        new getMyFulfills(false).execute(authString);
                        FulfillFragment parentFragment = (FulfillFragment)getParentFragment();
                        parentFragment.getActive().new getMyFulfills(false).execute(authString);
                        parentFragment.getCompleted().new getMyFulfills(false).execute(authString);

                    }
                }
        );
    }

    public class getMyFulfills extends AsyncTask<String, Void, Boolean> {

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
            String url = URL + "account/" + myId+"/fulfill/request/pending/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                myFulfillRequestArrayList.clear();
                //myFulfillRequestList.clear();

                try {
//                    JSONArray jsoArray = new JSONArray(rst);
//                    for(int i = 0; i < jsoArray.length(); i++) {
//                        JSONObject jso = jsoArray.getJSONObject(i);
//
//                        int id = jso.getInt("id");
//                        int requestorId = jso.getInt("requestorId");
//                        int imageResource = fulfillRequestImage;
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
//
//                        if(status.equals("pending")) {
//                            Request request = new Request(id, requestorId, imageResource, productName, requirement, location,
//                                    postal, startTime, endTime, duration, price, status);
//                            myFulfillRequestArrayList.add(request);
//                        }
//
//                    }

                    Gson gson  = new Gson();
                    ArrayList<Request> merged = new ArrayList<>();
                    merged = gson.fromJson(rst, new TypeToken<List<Request>>(){}.getType());

                    for(Request m : merged){
                        if(m.getStatus().equals("pending")){
                            myFulfillRequestArrayList.add(m);
                        }
                    }

                    if (myFulfillRequestArrayList.size() > 0){
                        havePendingFulfills = true;
                    } else {
                        havePendingFulfills = false;
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


            if(showDialog) {
                dialog.dismiss();
            }

            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }

            if(result){

                //boolean check = false;

                if(!havePendingFulfills){
                    emptyView2.setText("No pending fulfills.");
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