package com.wegot.fuyan.fyp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wegot.fuyan.fyp.FulfillFragments.ActiveFulfillsFragment;
import com.wegot.fuyan.fyp.FulfillFragments.CompletedFulfillsFragment;
import com.wegot.fuyan.fyp.FulfillFragments.PendingFulfillsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by Claudie on 9/3/16.
 */
public class FulfillFragment extends Fragment  implements MaterialTabListener {

    ImageButton addRequest,homepage,requestbt,fulfillbt;
    ListView myFulfillRequestLV;
    RequestAdapter adapter;
    int fulfillRequestImage = R.drawable.ordericon;
    int myId;
    Context mContext;
    String err, authString, username, password;
    ArrayList<Request> myFulfillRequestArrayList = new ArrayList<>();
    ArrayList<Fulfill> myFulfillList  = new ArrayList<>();
    ArrayList<Request> myFulfillRequestList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;
    View view;
    Activity activity;

    MaterialTabHost tabHost;
    ViewPager viewPager;
    ViewPagerAdapter androidAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_my_fulfill, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        activity = getActivity();
        //font
//        TextView myTextView=(TextView)view.findViewById(R.id.my_fulfill_title);
//        Typeface typeFace=Typeface.createFromAsset(activity.getAssets(),"fonts/TitilliumWeb-Bold.ttf");
//        myTextView.setTypeface(typeFace);
//        // Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
//        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Your code to refresh the list here.
//                // Make sure you call swipeContainer.setRefreshing(false)
//                // once the network request has completed successfully.
//                fetchTimelineAsync(0);
//            }
//        });
//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
        tabHost = (MaterialTabHost) view.findViewById(R.id.tabHost);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        //adapter view
        androidAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(androidAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int tabposition) {
                tabHost.setSelectedNavigationItem(tabposition);
            }
        });

        //for tab position
        for (int i = 0; i < androidAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(androidAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);
        authString  = username + ":" + password;

//        myFulfillRequestLV = (ListView)view.findViewById(R.id.my_fulfill_list);
//        adapter = new RequestAdapter(activity.getApplicationContext(),R.layout.row_layout);
//        myFulfillRequestLV.setAdapter(adapter);

        new getRequests().execute(authString);

//        myFulfillRequestLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
//                // Then you start a new Activity via Intent
//                Request rq = myFulfillRequestList.get(position);
//                Intent intent = new Intent(activity, MyFulfillRequestDetailsActivity.class);
//                intent.putExtra("selected_my_fulfill_request",(Serializable) rq);
//                startActivity(intent);
//            }
//        });

        Intent i = activity.getIntent();
        int swipeToOpen = i.getIntExtra("complete_fulfill_swipe",-1);
        int swipeToOpen2 = i.getIntExtra("disputed_fulfill_swipe",-1);
        if(swipeToOpen != -1){
            viewPager.setCurrentItem(swipeToOpen);
        }
        if(swipeToOpen2 != -1){
            viewPager.setCurrentItem(swipeToOpen2);
        }

    }

    // view pager adapter
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int num) {
            switch(num){
                case 0:
                    return new ActiveFulfillsFragment();
                case 1:
                    return new PendingFulfillsFragment();
                case 2:
                    return new CompletedFulfillsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int tabposition) {
            CharSequence ret = "";
            switch(tabposition){
                case 0:
                    ret = "Active";
                    break;
                case 1:
                    ret = "Pending";
                    break;

                case 2:
                    ret = "Completed";
                    break;
            }

            return ret;
        }
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // 'client' here is an instance of Android Async HTTP
        new getRequests().execute(authString);
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

                //new getMyFulfill().execute(authString);

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

                        Request request = new Request(id, requestorId, imageResource, productName, requirement, location,
                                postal, startTime, endTime, duration, price, status);
                        if(!status.equals("expired")&&!status.equals("active")&&!status.equals("inactive")) {
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

                adapter.clear();
                if(myFulfillRequestArrayList != null && !myFulfillRequestArrayList.isEmpty()){

                    for(Request r: myFulfillRequestArrayList){
                        int rId = r.getId();
                        for(Fulfill f : myFulfillList){
                            if (f.getRequestId() == rId){
                                check = true;
                            }
                        }

                        if(check) {

                            adapter.add(r);
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

        }
    }
    //tab on selected
    @Override
    public void onTabSelected(MaterialTab materialTab) {

        viewPager.setCurrentItem(materialTab.getPosition());
    }

    //tab on reselected
    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    //tab on unselected
    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }
}
