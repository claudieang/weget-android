package com.weget.fuyan.fyp;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.sendbird.android.shadow.com.google.gson.Gson;
import com.sendbird.android.shadow.com.google.gson.reflect.TypeToken;
import com.weget.fuyan.fyp.Recycler.RecyclerViewEmptySupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 4/4/2016.
 */

public class ActiveRequestsFragment extends Fragment {
    ImageButton addRequest,homepage,requestbt,fulfillbt;
    ListView myRequestLV;
    //RequestListAdapter adapter;
    int  requestImage = R.drawable.ordericon;
    int myId;
    Context mContext;
    String err, authString, username, password, baseURL;
    ArrayList<Request> myRequestArrayList = new ArrayList<>();
    ArrayList<Account> fulfillerAccountList = new ArrayList<>();
    ArrayList<MergedRequest> mergedList = new ArrayList<>();
    ArrayList<Integer> counterList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;
    View view;
    Activity activity;
    //private RecyclerView recyclerView;
    private RecyclerViewEmptySupport recyclerView;
    private com.weget.fuyan.fyp.Recycler.RequestActiveListAdapter mAdapter;
    SwipeRefreshLayout swipeRefresh;
    TextView emptyView;
    boolean hasActiveRequests = false;

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

        baseURL = getString(R.string.webserviceurl);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);

        authString  = username + ":" + password;
        new getMyRequests(true).execute(authString);

        recyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.my_request_list);

        mAdapter = new com.weget.fuyan.fyp.Recycler.RequestActiveListAdapter(mergedList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setEmptyView(view.findViewById(R.id.empty_view));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        emptyView = (TextView)view.findViewById(R.id.empty_view);

        /*
 * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
 * performs a swipe-to-refresh gesture.
 */
        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        new getMyRequests(false).execute(authString);
                        RequestFragment parentFragment = (RequestFragment)getParentFragment();
                        parentFragment.getPend().new getMyRequests(false).execute(authString);
                        parentFragment.getCompleted().new getMyRequests(false).execute(authString);

                    }
                }
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
            String url = baseURL+ "account/" + myId + "/requestVO/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                mergedList.clear();

                try {
                    Gson gson  = new Gson();
                    ArrayList<MergedRequest> merged = new ArrayList<>();
                    merged = gson.fromJson(rst, new TypeToken<List<MergedRequest>>(){}.getType());

                    for(MergedRequest m : merged){
                        if(m.getStatus().equals("active")){
                            mergedList.add(m);
                        }
                    }

                    if (mergedList.size() > 0){
                        hasActiveRequests = true;
                    } else {
                        hasActiveRequests =false;
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
            //new getMyRequestFulfiller().execute(myRequestArrayList);

            if(!hasActiveRequests){
                emptyView.setText("No requests made yet.");
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