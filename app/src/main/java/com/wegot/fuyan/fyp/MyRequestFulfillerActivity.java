package com.wegot.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wegot.fuyan.fyp.Recycler.DividerItemDecoration;
import com.wegot.fuyan.fyp.Recycler.RecyclerViewEmptySupport;
import com.wegot.fuyan.fyp.Recycler.RequestFulfillersListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MyRequestFulfillerActivity extends AppCompatActivity {

    Request myRequest;
    Button receiveBtn, updateBtn;
    ListView myRequestFulfillerLV;
    AccountAdapter adapter;
    String username, password, authString, fulfillStatus,requestStatus, err;
    int myId, myRequestId, selectedId, transactionId;
    Context mContext;
    Account a = null;
    ArrayList <Account> fulfillerAccountList = new ArrayList<>();
    ArrayList <Integer> fulfillIdList = new ArrayList<>();
    ArrayList <Fulfill> fulfillList = new ArrayList<>();
    Transaction tr;
    private SwipeRefreshLayout swipeContainer;
    TextView dispute;
    private RequestFulfillersListAdapter mAdapter;
    private RecyclerViewEmptySupport recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request_fulfiller);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //font
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        SpannableString s = new SpannableString("Request Fulfillers");
        s.setSpan(new TypefaceSpan(this, "Roboto-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbar.setTitle(s);
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        //dispute
//        dispute = (TextView)findViewById(R.id.disputebt);
//        Typeface typeFace2=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-BoldItalic.ttf");
//        dispute.setTypeface(typeFace2);
//
//        dispute.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new getTransaction().execute(authString);
//
//            }
//        });
//
//        //font
//        TextView myTextView=(TextView)findViewById(R.id.my_request_fulfiller_title);
//        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");
//        myTextView.setTypeface(typeFace);

        // Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
//        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Your code to refresh the list here.
//                // Make sure you call swipeContainer.setRefreshing(false)
//                // once the network request has completed successfully.
//                fetchTimelineAsync(0);
//
//            }
//        });
        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);

        myRequest =(Request) getIntent().getSerializableExtra("selected_my_request");
        //tr = (Transaction)getIntent().getSerializableExtra("fulfiller_transaction");

        myRequestId = myRequest.getId();
        requestStatus = myRequest.getStatus();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);
        myId = pref.getInt("id", 0);

        authString  = username + ":" + password;

        //myRequestFulfillerLV = (ListView)findViewById(R.id.my_request_fulfiller_list);
        //receiveBtn = (Button)findViewById(R.id.receive_button);
        //updateBtn = (Button)findViewById(R.id.udpate_button);
        //adapter = new AccountAdapter(getApplicationContext(),R.layout.row_layout);
        //myRequestFulfillerLV.setAdapter(adapter);
        new getRequests().execute(authString);
        //new getMyRequestFulfiller().execute(authString);

        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.my_request_fulfiller_list);
        mAdapter = new RequestFulfillersListAdapter(fulfillerAccountList,fulfillIdList, myRequest);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setEmptyView(findViewById(R.id.empty_view));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);





    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

//        receiveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(MyRequestFulfillerActivity.this)
//                        .setTitle("Alert!")
//                        .setMessage("Confirm received order?")
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                new doReceive().execute(authString);
//                            }})
//                        .setNegativeButton(android.R.string.no, null).show();
//            }
//        });
//
//        updateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                new getRequestsAgainAgain().execute(authString);
//
//            }
//        });




    //}
    private class getTransaction extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/"+myRequestId+"/transaction/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                try {
                    JSONObject jso = new JSONObject(rst);
                    transactionId = jso.getInt("transactionId");



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


                new dispute().execute(authString);

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class dispute extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog = new ProgressDialog(MyRequestFulfillerActivity.this, R.style.MyTheme);

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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/transaction/" + transactionId+"/dispute/";
            JSONObject jsoin = null;
            try{
                jsoin = new JSONObject();
                jsoin.put("accountId", myId);
                jsoin.put("message", "LOL nothing here!");


            }catch(JSONException e){
                e.printStackTrace();
                err = e.getMessage();
            }

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, jsoin.toString() + basicAuth);
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
            if(result) {
                Intent i = new Intent(MyRequestFulfillerActivity.this, MainActivity.class);
                i.putExtra("after_dispute_tab", 1);
                i.putExtra("disputed_request_swipe", 2);
                Toast.makeText(getApplicationContext(), "Complaint Logged!", Toast.LENGTH_SHORT).show();
                startActivity(i);


            }else{

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }


    }










    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // 'client' here is an instance of Android Async HTTP
        new getRequests().execute(authString);
    }

    private class getRequestsAgainAgain extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(MyRequestFulfillerActivity.this, R.style.MyTheme);

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

                new getRequest().execute(authString);

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getRequest extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/"+myRequestId+"/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;

                JSONObject jso = null;
                try{
                    jso = new JSONObject(rst);
                    requestStatus = jso.getString("status");

                }catch(JSONException e){
                    e.printStackTrace();
                    err = e.getMessage();
                }
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result){

                if(requestStatus.equals("active")) {

                    Intent intent = new Intent(MyRequestFulfillerActivity.this, UpdateRequestActivity.class);
                    intent.putExtra("selected_request_toupdate", (Serializable) myRequest);
                    startActivity(intent);
                }else{
                    Intent i = new Intent (MyRequestFulfillerActivity.this, MyRequestActivity.class);
                    Toast.makeText(getApplicationContext(), "Request Expired!", Toast.LENGTH_SHORT).show();
                    startActivity(i);

                }

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class doReceive extends AsyncTask<String, Void, Boolean> {


        ProgressDialog dialog = new ProgressDialog(MyRequestFulfillerActivity.this, R.style.MyTheme);

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
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/transaction/" + myRequestId+"/received/";

            String rst = UtilHttp.doHttpPostBasicAuthentication(mContext, url, basicAuth);
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
            if(result) {
                Intent i = new Intent(MyRequestFulfillerActivity.this, MainActivity.class);
                i.putExtra("after_received_tab", 1);
                i.putExtra("complete_request_swipe", 2);
                Toast.makeText(getApplicationContext(), "Received!", Toast.LENGTH_SHORT).show();
                startActivity(i);


            }else{

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }


    }

    private class getRequestsAgain extends AsyncTask<String, Void, Boolean> {

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

                new getFulfill().execute(authString);

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getFulfill extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/fulfill/"+selectedId+"/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {

                success = true;

                JSONObject jso = null;
                try{
                    jso = new JSONObject(rst);
                    fulfillStatus = jso.getString("status");

                }catch(JSONException e){
                    e.printStackTrace();
                    err = e.getMessage();
                }
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result){

                if(fulfillStatus.equals("active")) {

                    Intent intent = new Intent(MyRequestFulfillerActivity.this, RequestFulfillerDetailsActivity.class);
                    intent.putExtra("selected_fulfill_id", selectedId);
                    intent.putExtra("selected_fulfiller", (Serializable) a);
                    intent.putExtra("selected_request_tofulfull", (Serializable) myRequest);
                    startActivity(intent);
                }else{
                    Intent i = new Intent (MyRequestFulfillerActivity.this, MyRequestActivity.class);
                    Toast.makeText(getApplicationContext(), "Request Expired!", Toast.LENGTH_SHORT).show();
                    startActivity(i);

                }

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
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


                new getMyRequestFulfill().execute(authString);

            }else {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getMyRequestFulfill extends AsyncTask<String, Void, Boolean> {
        int fulfillId, requestId, fulfillerId;
        String status;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/" + myRequestId+"/fulfill/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                fulfillList.clear();
                fulfillIdList.clear();

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for(int i = 0; i < jsoArray.length(); i++) {
                        JSONObject jso = jsoArray.getJSONObject(i);

                        fulfillId = jso.getInt("id");
                        requestId = jso.getInt("requestId");
                        fulfillerId = jso.getInt("fulfillerId");
                        status = jso.getString("status");

                        Fulfill f = new Fulfill(fulfillId,requestId, fulfillerId,status);



                        fulfillList.add(f);
                        fulfillIdList.add(fulfillId);


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
            if(result) {

                new getMyRequestFulfiller().execute(authString);


            }else{

                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
            }

        }


    }



    private class getMyRequestFulfiller extends AsyncTask<String, Void, Boolean> {

        int id, contactNo;
        String username, password, email, fulfiller, picture;
        Account account;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = "https://weget-2015is203g2t2.rhcloud.com/webservice/request/" + myRequestId+"/fulfillers/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                fulfillerAccountList.clear();

                try {
                    JSONArray jsoArray = new JSONArray(rst);
                    for(int i = 0; i < jsoArray.length(); i++) {
                        JSONObject jso = jsoArray.getJSONObject(i);

                        id = jso.getInt("id");
                        username = jso.getString("username");
                        password = jso.getString("password");
                        contactNo = jso.getInt("contactNo");
                        email = jso.getString("email");
                        fulfiller = jso.getString("fulfiller");
                        picture = jso.getString("picture");

                        account = new Account(id, username, password, contactNo, email, fulfiller, picture);


                        fulfillerAccountList.add(account);


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
            mAdapter.notifyDataSetChanged();
            if(!result) {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();

            }

        }
    }
}
