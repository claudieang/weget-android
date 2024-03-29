package com.weget.fuyan.fyp;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class RequestFragment extends Fragment implements MaterialTabListener {
        private PendingRequestsFragment pend;
        private CompletedRequestsFragment completed;
        private ActiveRequestsFragment active;

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

        MaterialTabHost tabHost;
        ViewPager viewPager;
        ViewPagerAdapter androidAdapter;
        String URL;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            //setContentView(R.layout.activity_my_request);

            setHasOptionsMenu(true);
            view = getView();
            activity = getActivity();
            //change font
            //TextView myTextView=(TextView)view.findViewById(R.id.my_request_title);
            //Typeface typeFace=Typeface.createFromAsset(activity.getAssets(),"fonts/Quicksand-Bold.otf");
            //myTextView.setTypeface(typeFace);

            //tab host
            URL = getString(R.string.webserviceurl);
            tabHost = (MaterialTabHost) view.findViewById(R.id.tabHost);
            viewPager = (ViewPager) view.findViewById(R.id.viewPager);

            //adapter view
            androidAdapter = new ViewPagerAdapter(getChildFragmentManager());
            viewPager.setOffscreenPageLimit(3);
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
            SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
            username = pref.getString("username", null);
            password = pref.getString("password", null);
            myId = pref.getInt("id", 0);

            //tr = (Transaction)getIntent().getSerializableExtra("transaction");
        /*

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
            });*/

            Intent i = activity.getIntent();
            int swipeToOpen = i.getIntExtra("after_payment_request_swipe",-1);
            int swipeToOpen2 = i.getIntExtra("complete_request_swipe", -1);
            int swipeToOpen3 = i.getIntExtra("disputed_fulfill_swipe",-1);
            int swipeToOpen4 = i.getIntExtra("disputed_request_swipe", -1);

            int notificationrequestswipe = i.getIntExtra("notification_request_swipe",-1);


            if(swipeToOpen!=-1){
                viewPager.setCurrentItem(swipeToOpen);
            }
            if(swipeToOpen2!=-1){
                viewPager.setCurrentItem(swipeToOpen2);
            }
            if(swipeToOpen3!=-1){
                viewPager.setCurrentItem(swipeToOpen3);
            }
            if(swipeToOpen4!=-1){
                viewPager.setCurrentItem(swipeToOpen4);
            }

            if(notificationrequestswipe!=-1){
                viewPager.setCurrentItem(notificationrequestswipe);
            }


        }

        public ActiveRequestsFragment getActive(){
            return active;
        }

        public PendingRequestsFragment getPend() {
            return pend;
        }

        public CompletedRequestsFragment getCompleted(){
            return completed;
        }

        // view pager adapter
        private class ViewPagerAdapter extends FragmentStatePagerAdapter {

            public ViewPagerAdapter(FragmentManager fragmentManager) {
                super(fragmentManager);
            }

            public Fragment getItem(int num) {
                switch(num){
                    case 0:
                        active = new ActiveRequestsFragment();
                        return active;
                    case 1:
                        pend = new PendingRequestsFragment();
                        return pend;
                    case 2:
                        completed = new CompletedRequestsFragment();
                        return completed;
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
                        ret = "pending";
                        break;

                    case 2:
                        ret = "Completed";
                        break;
                }

                return ret;
            }
        }

        //no more, pull down refresh
        /*
        public void fetchTimelineAsync(int page) {
            // Send the network request to fetch the updated data
            // 'client' here is an instance of Android Async HTTP
            new getRequests().execute(authString);
        }
        */

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
                dialog.dismiss();
                if(result){

                    //new getMyRequests().execute(authString);

                }else {
                    Toast.makeText(getActivity().getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onPrepareOptionsMenu(Menu menu){
            MenuItem filter = menu.findItem(R.id.action_filter);
            MenuItem refresh1 = menu.findItem(R.id.action_refresh);
            refresh1.setVisible(false);
            filter.setVisible(false);
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
