package com.weget.fuyan.fyp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

import com.weget.fuyan.fyp.Recycler.DividerItemDecoration;
import com.weget.fuyan.fyp.Recycler.RequestAllListAdapter;

import java.util.ArrayList;

/**
 * Created by HP on 10/20/2016.
 */
public class RequestPopUp extends Activity {

    RequestAllListAdapter adapter;
    ListView listView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.request_popup);

        recyclerView = (RecyclerView) findViewById(R.id.listView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
    

        ArrayList<Request> reqList = (ArrayList<Request>) getIntent().getSerializableExtra("reqList");
        int myId = (Integer) getIntent().getSerializableExtra("myId");

        adapter = new RequestAllListAdapter(reqList,myId);

        Log.d("Print","reqList size is : " + reqList.size());

        recyclerView.setAdapter(adapter);



        //setting the pop up resolution based on phones screen resolution
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //(int)(height*0.7)
        getWindow().setLayout((int)(width*0.7), RecyclerView.LayoutParams.WRAP_CONTENT);



    }
}
