package com.weget.fuyan.fyp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weget.fuyan.fyp.Recycler.DividerItemDecoration;
import com.weget.fuyan.fyp.Recycler.RecyclerViewEmptySupport;
import com.weget.fuyan.fyp.Recycler.RequestFulfillersListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyRequestFulfillerActivity extends AppCompatActivity {

    Request myRequest;
    Button receiveBtn, updateBtn;
    ListView myRequestFulfillerLV;
    AccountAdapter adapter;
    String username, password, authString, fulfillStatus,requestStatus, err;
    int myId, myRequestId, selectedId, transactionId;
    Context mContext;
    Account a = null;
    ArrayList <AccountExtended> fulfillerAccountList = new ArrayList<>();
    ArrayList <Integer> fulfillIdList = new ArrayList<>();
    ArrayList <Fulfill> fulfillList = new ArrayList<>();
    Transaction tr;
    private SwipeRefreshLayout swipeContainer;
    TextView dispute;
    private RequestFulfillersListAdapter mAdapter;
    private RecyclerViewEmptySupport recyclerView;
    String URL;
    TextView emptyView;
    boolean noFulfillers = false;
    ProgressDialog dialog;


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
        URL = getString(R.string.webserviceurl);
        myRequest =(Request) getIntent().getSerializableExtra("selected_my_request");
        //tr = (Transaction)getIntent().getSerializableExtra("fulfiller_transaction");
        dialog = new ProgressDialog(MyRequestFulfillerActivity.this, R.style.MyTheme);

        myRequestId = myRequest.getId();
        requestStatus = myRequest.getStatus();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        username = pref.getString("username", null);
        password = pref.getString("password", null);


        authString  = username + ":" + password;


        new getMyRequestFulfill().execute(authString);


        emptyView = (TextView)findViewById(R.id.empty_view);

        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.my_request_fulfiller_list);
        mAdapter = new RequestFulfillersListAdapter(fulfillerAccountList,fulfillIdList, myRequest);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setEmptyView(findViewById(R.id.empty_view));
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




    private class getMyRequestFulfill extends AsyncTask<String, Void, Boolean> {
        int fulfillId, requestId, fulfillerId;
        String status;

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);

            if(!isFinishing()) {
                dialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "request/" + myRequestId+"/fulfill/";

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
        AccountExtended account;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            final String basicAuth = "Basic " + Base64.encodeToString(params[0].getBytes(), Base64.NO_WRAP);

            boolean success = false;
            String url = URL + "request/" + myRequestId +"/fulfillers/";

            String rst = UtilHttp.doHttpGetBasicAuthentication(mContext, url, basicAuth);
            if (rst == null) {
                err = UtilHttp.err;
                success = false;
            } else {
                fulfillerAccountList.clear();
                Log.d("Print","rst length is : " + rst.length());

                try {
//                    JSONArray jsoArray = new JSONArray(rst);
//                    Log.d("Print","jso length is : " + jsoArray.length());
//                    if(jsoArray.length() > 0){
//                        for(int i = 0; i < jsoArray.length(); i++) {
//                            JSONObject jso = jsoArray.getJSONObject(i);
//                            id = jso.getInt("id");
//                            username = jso.getString("username");
//                            contactNo = jso.getInt("contactNo");
//                            email = jso.getString("email");
//                            fulfiller = jso.getString("fulfiller");
//                            picture = jso.getString("picture");
//                            double requestTotal = jso.getDouble("requestTotal");
//                            double requestNo = jso.getDouble("requestNo");
//                            double fulfillTotal = jso.getDouble("fulfillTotal");
//                            double fulfillNo = jso.getDouble("fulfillNo");
//                            double requestMade = jso.getDouble("requestMade");
//                            double fulfillMade = jso.getDouble("fulfillMade");



//                            account = new AccountExtended(id, username, password, contactNo, email, fulfiller, picture,requestTotal, requestNo, fulfillTotal, fulfillNo, requestMade, fulfillMade);
//
//
//                            fulfillerAccountList.add(account);
//
                    Gson gson = new Gson();
                    ArrayList<AccountExtended> list = gson.fromJson(rst,new TypeToken<List<AccountExtended>>(){}.getType());
                    fulfillerAccountList.addAll(list);
                       // }
//                        noFulfillers = false;
//                    } else {
//                        noFulfillers = true;
//                    }
                    if(fulfillerAccountList.size() == 0){
                        noFulfillers = false;
                    }else{
                        noFulfillers = true;
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
            if(noFulfillers){
                emptyView.setText("No fulfillers have accepted this request yet.");
            }
            dialog.dismiss();
            mAdapter.notifyDataSetChanged();

            if(!result) {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();

            }

        }
    }
}
